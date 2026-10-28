package com.github.manu585.fourelements.core.tick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * Runs {@link Tickable}s at their own interval from one {@link #tick()} call per server tick,
 * so the platform needs a single repeating task no matter how many tickables there are.
 *
 * <p>Tickables are grouped by interval and then spread over the ticks of that interval
 * (round-robin), so ten tasks that run once a second do not all land on the same tick. A tick
 * only visits the tickables that are due, the others cost nothing.
 *
 * <p>Meant to be driven from one thread (the main thread). Registering and cancelling is
 * safe at any time, also from inside a running tickable.
 */
public final class TickScheduler {

  private final BiConsumer<Tickable, Throwable> onError;
  private final Map<Integer, Bucket> buckets = new ConcurrentHashMap<>();

  private long currentTick;

  /**
   * @param onError told about a tickable that threw, it is kept and runs again next time its turn comes
   */
  public TickScheduler(BiConsumer<Tickable, Throwable> onError) {
    this.onError = onError;
  }

  /**
   * Run this every {@code interval} ticks, starting within the next {@code interval} ticks.
   *
   * @param interval ticks between two runs, {@code 1} means every tick
   * @return a handle to stop it again
   * @throws IllegalArgumentException if {@code interval} is below 1
   */
  public Registration every(int interval, Tickable tickable) {
    if (interval < 1) {
      throw new IllegalArgumentException("Tick interval must be at least 1, was " + interval);
    }

    Bucket bucket = buckets.computeIfAbsent(interval, Bucket::new);
    return bucket.add(tickable);
  }

  /**
   * Advance by one tick and run everything that is due.
   */
  public void tick() {
    for (Bucket bucket : buckets.values()) {
      for (Entry entry : bucket.slot(currentTick)) {
        run(entry);
      }
    }
    currentTick++;
  }

  /**
   * Drop every registration.
   */
  public void clear() {
    buckets.values().forEach(Bucket::clear);
    buckets.clear();
  }

  // Cancelled by an earlier tickable in this very tick
  private void run(Entry entry) {
    if (entry.cancelled) {
      return;
    }

    try {
      entry.tickable.tick();
    } catch (Throwable t) {
      onError.accept(entry.tickable, t);
    }
  }

  private static final class Bucket {

    private final int interval;
    private final List<List<Entry>> slots;
    private final AtomicInteger nextSlot = new AtomicInteger();

    private Bucket(int interval) {
      this.interval = interval;
      this.slots = new ArrayList<>(interval);
      for (int i = 0; i < interval; i++) {
        slots.add(new CopyOnWriteArrayList<>());
      }
    }

    private Registration add(Tickable tickable) {
      List<Entry> slot = slots.get(Math.floorMod(nextSlot.getAndIncrement(), interval));
      Entry entry = new Entry(tickable, slot);
      slot.add(entry);
      return entry;
    }

    private List<Entry> slot(long tick) {
      return slots.get((int) (tick % interval));
    }

    private void clear() {
      slots.forEach(slot -> slot.forEach(Entry::cancel));
    }

  }

  private static final class Entry implements Registration {

    private final Tickable tickable;
    private final List<Entry> slot;
    private volatile boolean cancelled;

    private Entry(Tickable tickable, List<Entry> slot) {
      this.tickable = tickable;
      this.slot = slot;
    }

    @Override
    public void cancel() {
      cancelled = true;
      slot.remove(this);
    }

  }

}
