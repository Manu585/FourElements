package com.github.manu585.fourelements.core.tick;

/**
 * Something that runs repeatedly, driven by a {@link TickScheduler} at the interval it was registered with.
 */
@FunctionalInterface
public interface Tickable {

  void tick();

}
