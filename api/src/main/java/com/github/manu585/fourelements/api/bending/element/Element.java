package com.github.manu585.fourelements.api.bending.element;

// TODO: Implement sub elements
public enum Element {

  AIR(null, "Air", "#9be7ff"),
  WATER(null, "Water", "#3aa0ff"),
  EARTH(null, "Earth", "#7a5230"),
  FIRE(null, "Fire", "d93030"),
  CHI(null, "Chi", "#ffff9b"),
  AVATAR(null, "Avatar", "#cc72f2");

  private final Element parent;
  private final String displayName;
  private final String chatColorHex;

  Element(Element parent, String displayName, String chatColorHex) {
    this.parent = parent;
    this.displayName = displayName;
    this.chatColorHex = chatColorHex;
  }

  public boolean isSubElement() {
    return parent != null;
  }

  public boolean isSubElementOf(Element element) {
    return parent == element;
  }

  public Element parent() {
    return parent;
  }

  public String displayName() {
    return displayName;
  }

  public String chatColorHex() {
    return chatColorHex;
  }

}
