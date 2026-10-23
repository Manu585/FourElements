package com.github.manu585.fourelements.bukkit.manager;

import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.concurrent.KeyedTaskQueue;
import com.github.manu585.fourelements.core.registry.OnlineBenderRegistry;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * Owns the lifecycle of runtime benders and every database access made on their behalf.
 *
 * <p>A bender is {@link #load loaded} off-thread during {@code AsyncPlayerPreLogin} and
 * stashed as pending, then {@link #promote promoted} into an {@link OnlineBenderPlayer}
 * on {@code PlayerJoin} and {@link #remove removed} on {@code PlayerQuit}.
 *
 * <p>All repository reads and writes for one UUID (login load, quit save, offline edits,
 * online-edit saves) run through a per-UUID queue, so they execute strictly in the order
 * they were requested and can never overwrite each other with stale state.
 */
public final class BenderManager {

  private final OnlineBenderRegistry registry;
  private final BenderRepository repository;

  // Benders loaded at pre-login, awaiting promotion when the player actually joins
  private final Map<UUID, BenderPlayer> pending = new ConcurrentHashMap<>();

  private final KeyedTaskQueue<UUID> ioQueue = new KeyedTaskQueue<>();

  public BenderManager(OnlineBenderRegistry registry, BenderRepository repository) {
    this.registry = registry;
    this.repository = repository;
  }

  /**
   * Load a bender (creating a fresh one if never saved) and stash it as pending until
   * the player joins. Queued behind any in-flight edit or save for the same UUID, so the
   * loaded state includes every change requested before this call.
   *
   * @param uuid logging-in player's UUID
   * @return a future completing once the bender is pending, or exceptionally if the load fails
   */
  public CompletableFuture<BenderPlayer> load(UUID uuid) {
    return ioQueue.submit(uuid, () -> repository.getBender(uuid).thenApply(loaded -> {
      BenderPlayer benderPlayer = loaded != null ? loaded : new BenderPlayer(uuid);
      pending.put(uuid, benderPlayer);
      return benderPlayer;
    }));
  }

  /**
   * Promote a pending bender into an online runtime object and register it.
   *
   * @param uuid joining player's UUID
   *
   * @return the new {@link OnlineBenderPlayer}, or {@code null} if nothing was pending (error on login)
   */
  public OnlineBenderPlayer promote(UUID uuid) {
    BenderPlayer benderPlayer = pending.remove(uuid);
    if (benderPlayer == null) {
      return null;
    }

    OnlineBenderPlayer online = new OnlineBenderPlayer(benderPlayer);
    registry.put(uuid, online);
    return online;
  }

  public OnlineBenderPlayer getBender(UUID uuid) {
    return registry.getOrThrow(uuid);
  }

  public boolean isOnline(UUID uuid) {
    return registry.contains(uuid);
  }

  /**
   * Evict a bender on quit, also clearing any leftover pending entry. The caller is
   * expected to persist the returned bender with {@link #save}.
   *
   * @param uuid quitting player's UUID
   * @return the removed {@link OnlineBenderPlayer}, or {@code null} if none was online
   */
  public OnlineBenderPlayer remove(UUID uuid) {
    pending.remove(uuid);
    return registry.remove(uuid);
  }

  /**
   * @return all currently online benders (unmodifiable view)
   */
  public Collection<OnlineBenderPlayer> online() {
    return registry.values();
  }

  /**
   * Persist a bender. The element state is snapshotted immediately on the calling thread,
   * so later mutations cannot leak into (or race with) the asynchronous write.
   *
   * @param benderPlayer bender to save
   * @return a future completing when the write is done
   */
  public CompletableFuture<Void> save(BenderPlayer benderPlayer) {
    BenderPlayer snapshot = snapshot(benderPlayer);
    return ioQueue.submit(snapshot.uuid(), () -> repository.saveBender(snapshot));
  }

  /**
   * Apply an edit to a bender whether they are online or not, then persist the result.
   *
   * <p>Online (or pending) benders are mutated in place on the <b>caller's thread</b>, so
   * the change is live immediately; keep the edit main-thread safe. Offline benders are
   * loaded, mutated and saved as one queued step on a database thread, where the edit is
   * applied to a private object. Concurrent edits of the same bender are applied one
   * after another, never from the same stale state.
   *
   * @param uuid bender to edit
   * @param edit mutation to apply to the {@link BenderPlayer}; its return value is passed through to the caller (e.g. whether an element was newly added)
   * @param <R>  result type of the edit
   * @return a future completing with the edit's result once persisted, or completing
   *     exceptionally if the edit throws or the load or save fails
   */
  public <R> CompletableFuture<R> editBender(UUID uuid, Function<BenderPlayer, R> edit) {
    BenderPlayer live = liveBender(uuid);
    if (live != null) {
      return applyAndPersist(live, edit, this::save);
    }
    return ioQueue.submit(uuid, () -> editStored(uuid, edit));
  }

  /**
   * Grab a {@link BenderPlayer} either from the active registry or the pending Map.
   *
   * @param uuid UUID of Bender to get
   * @return Existing {@link BenderPlayer}
   */
  private BenderPlayer liveBender(UUID uuid) {
    OnlineBenderPlayer online = registry.get(uuid);
    return online != null ? online.benderPlayer() : pending.get(uuid);
  }

  /**
   * Creates an independent copy of the given bender for persistence.
   *
   * <p>The copy shares no mutable state with the original, so it can be handed to another
   * thread while the original keeps being modified.
   *
   * @param benderPlayer bender to copy
   * @return a detached copy with the same UUID and elements
   */
  private static @NonNull BenderPlayer snapshot(BenderPlayer benderPlayer) {
    return new BenderPlayer(benderPlayer.uuid(), benderPlayer.elements());
  }

  /**
   * Runs inside a queued task (the stored bender is private to it), so it writes straight
   * to the repository; going through {@link #save} here would wait on its own task.
   */
  private <R> CompletableFuture<R> editStored(UUID uuid, Function<BenderPlayer, R> edit) {
    // Re-check: the player may have logged in while this edit waited in the queue. Then
    // the live object is the source of truth and a load from the database would be stale.
    BenderPlayer live = liveBender(uuid);
    if (live != null) {
      return applyAndPersist(live, edit, this::saveNow);
    }

    return repository.getBender(uuid).thenCompose(loaded -> {
      BenderPlayer benderPlayer = loaded != null ? loaded : new BenderPlayer(uuid);
      return applyAndPersist(benderPlayer, edit, this::saveNow);
    });
  }

  private CompletableFuture<Void> saveNow(BenderPlayer benderPlayer) {
    return repository.saveBender(snapshot(benderPlayer));
  }

  private <R> CompletableFuture<R> applyAndPersist(BenderPlayer target, Function<BenderPlayer, R> edit, Function<BenderPlayer, CompletableFuture<Void>> persist) {
    R result;
    try {
      result = edit.apply(target);
    } catch (RuntimeException e) {
      return CompletableFuture.failedFuture(e);
    }
    return persist.apply(target).thenApply(ignored -> result);
  }

}
