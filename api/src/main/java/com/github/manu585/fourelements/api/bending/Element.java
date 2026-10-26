package com.github.manu585.fourelements.api.bending;

/**
 * The four classical elements. A bender has exactly one (or none), the Avatar is not an
 * element but a status on top of one, see {@link com.github.manu585.fourelements.api.bender.Bender#isAvatar()}.
 */
public enum Element {

  AIR("Air", "#9be7ff"),
  WATER("Water", "#3aa0ff"),
  EARTH("Earth", "#7a5230"),
  FIRE("Fire", "#d93030");

  private final String displayName;
  private final String chatColorHex;

  /**
   * Element constructor.
   *
   * @param displayName Display name of Element
   * @param chatColorHex Hex color code of Element
   */
  Element(String displayName, String chatColorHex) {
    this.displayName = displayName;
    this.chatColorHex = chatColorHex;
  }

  public String displayName() {
    return displayName;
  }

  public String chatColorHex() {
    return chatColorHex;
  }

  public static Element nextElementInCycle(Element previous) {
    return switch (previous) {
      case AIR -> WATER;
      case WATER -> EARTH;
      case EARTH -> FIRE;
      case FIRE -> AIR;
    };
  }

}
