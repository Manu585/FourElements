package com.github.manu585.fourelements.bukkit.manager;

import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.registry.OnlineBenderRegistry;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Owns the lifecycle of runtime benders.
 *
 * <p>A bender is loaded off-thread during {@code AsyncPlayerPreLogin} and stashed as
 * {@link #markPending pending}, he is then {@link #promote promoted} into an
 * {@link OnlineBenderPlayer} on {@code PlayerJoin} and {@link #remove removed} on
 * {@code PlayerQuit}.
 */
public final class BenderManager {

  private final OnlineBenderRegistry registry;
  private final BenderRepository repository;

  // Benders loaded at pre-login, awaiting promotion when the player actually joins
  private final Map<UUID, BenderPlayer> pending = new ConcurrentHashMap<>();

  public BenderManager(OnlineBenderRegistry registry, BenderRepository repository) {
    this.registry = registry;
    this.repository = repository;
  }

  /**
   * Stash a bender loaded during pre-login until the player joins.
   */
  public void markPending(BenderPlayer benderPlayer) {
    pending.put(benderPlayer.uuid(), benderPlayer);
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
    return registry.get(uuid);
  }

  public boolean isOnline(UUID uuid) {
    return registry.contains(uuid);
  }

  /**
   * Evict a bender on quit, also clearing any leftover pending entry.
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
   * Apply an edit to a bender whether they are online or not, then persist the result.
   *
   * <p>Online benders are mutated in place, so the change is live immediately, offline
   * benders are loaded from the repository (created fresh if they have never been saved),
   * mutated, and written back. Either way the bender is persisted before the returned
   * future completes.
   *
   * <p>Note the edit runs on the caller's thread for online benders (keep it main-thread
   * safe) and on a database thread for offline ones — that is fine, since an offline
   * bender is a private freshly-loaded object.
   *
   * @param uuid bender to edit
   * @param edit mutation to apply to the {@link BenderPlayer}; its return value is passed
   *     through to the caller (e.g. whether an element was newly added)
   * @param <R>  result type of the edit
   * @return a future completing with the edit's result once persisted, or completing
   *     exceptionally if the load or save fails
   */
  public <R> CompletableFuture<R> editBender(UUID uuid, Function<BenderPlayer, R> edit) {
    OnlineBenderPlayer online = registry.get(uuid);
    if (online != null) {
      BenderPlayer benderPlayer = online.benderPlayer();
      R result = edit.apply(benderPlayer);
      return repository.saveBender(benderPlayer).thenApply(ignored -> result);
    }

    return repository.getBender(uuid).thenCompose(loaded -> {
      BenderPlayer benderPlayer = loaded != null ? loaded : new BenderPlayer(uuid);
      R result = edit.apply(benderPlayer);
      return repository.saveBender(benderPlayer).thenApply(ignored -> result);
    });
  }

}
