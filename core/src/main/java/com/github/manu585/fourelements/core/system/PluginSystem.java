package com.github.manu585.fourelements.core.system;

public interface PluginSystem {

  void enable();

  default void disable() {}

  default String name() {
    return getClass().getSimpleName();
  }

}
