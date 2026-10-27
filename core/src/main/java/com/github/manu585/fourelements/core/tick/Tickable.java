package com.github.manu585.fourelements.core.tick;

/**
 * Something that runs once every server tick, driven by the platform's ticker.
 */
@FunctionalInterface
public interface Tickable {

  void tick();

}
