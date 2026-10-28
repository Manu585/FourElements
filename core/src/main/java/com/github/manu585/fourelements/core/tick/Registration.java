package com.github.manu585.fourelements.core.tick;

/**
 * Handle to something registered on a {@link TickScheduler}, to stop it again.
 */
@FunctionalInterface
public interface Registration {

  /**
   * Stop ticking. Safe to call more than once, and from inside the tickable itself.
   */
  void cancel();

}
