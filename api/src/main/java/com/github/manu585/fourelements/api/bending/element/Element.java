package com.github.manu585.fourelements.api.bending.element;

public interface Element {

  default String name() {
    return getClass().getSimpleName();
  }

  Elements element();

  String chatColorHex();

}
