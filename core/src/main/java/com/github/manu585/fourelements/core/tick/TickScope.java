package com.github.manu585.fourelements.core.tick;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Everything registered through it is stopped together on {@link #close()}, e.g. all the
 * tickables of one player when they quit. Nothing can be registered after it is closed.
 */
public final class TickScope implements AutoCloseable {

  private final TickScheduler scheduler;
  private final List<Registration> registrations = new CopyOnWriteArrayList<>();

  private volatile boolean closed;

  public TickScope(TickScheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Run this every {@code interval} ticks until this scope is closed or the returned handle is cancelled.
   *
   * @param interval ticks between two runs, {@code 1} means every tick
   *
   * @throws IllegalStateException if the scope is already closed
   */
  public Registration every(int interval, Tickable tickable) {
    if (closed) {
      throw new IllegalStateException("Tick scope is closed");
    }

    Registration registration = scheduler.every(interval, tickable);
    registrations.add(registration);
    return registration;
  }

  @Override
  public void close() {
    closed = true;
    registrations.forEach(Registration::cancel);
    registrations.clear();
  }

}
