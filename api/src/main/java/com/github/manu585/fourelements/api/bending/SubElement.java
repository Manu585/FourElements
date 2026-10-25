package com.github.manu585.fourelements.api.bending;

/**
 * All Sub-Elements. Every one belongs to exactly one classical {@link Element}, only
 * benders of that element and the Avatar may possess it.
 */
public enum SubElement {

  SPIRITUAL(Element.AIR, "Spiritual", "#d29bff"),
  FLIGHT(Element.AIR, "Flight", "#9ba7ff"),
  SOUND(Element.AIR, "Sound", "#bfbfbf"),

  HEALING(Element.WATER, "Healing", "#9bffa5"),
  PLANT(Element.WATER, "Plant", "#1e732f"),
  BLOOD(Element.WATER, "Blood", "#780c0c"),
  ICE(Element.WATER, "Ice", "#0c89c7"),

  METAL(Element.EARTH, "Metal", "#c9cfbe"),
  SAND(Element.EARTH, "Sand", "#ffe49b"),
  LAVA(Element.EARTH, "Lava", "#ffc89b"),

  LIGHTNING(Element.FIRE, "Lightning", "#ffffff"),
  COMBUSTION(Element.FIRE, "Combustion", "#bf3834");

  private final Element parentElement;
  private final String displayName;
  private final String chatColorHex;

  /**
   * Sub-Element constructor.
   *
   * @param parentElement Classical Element this Sub-Element derives from
   * @param displayName Display name of Sub-Element
   * @param chatColorHex Hex color code of Sub-Element
   */
  SubElement(Element parentElement, String displayName, String chatColorHex) {
    this.parentElement = parentElement;
    this.displayName = displayName;
    this.chatColorHex = chatColorHex;
  }

  /**
   * Get the classical Element this Sub-Element derives from.
   *
   * @return Parent element
   */
  public Element parent() {
    return parentElement;
  }

  public String displayName() {
    return displayName;
  }

  public String chatColorHex() {
    return chatColorHex;
  }

}
