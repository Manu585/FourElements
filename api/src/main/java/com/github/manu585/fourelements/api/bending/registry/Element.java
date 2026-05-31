package com.github.manu585.fourelements.api.bending.registry;

import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NonNull;

// TODO: Implement sub elements
public enum Element {

  AIR("Air", "#9be7ff"),
  WATER("Water", "#3aa0ff"),
  EARTH("Earth", "#7a5230"),
  FIRE("Fire", "d93030"),
  CHI("Chi", "#ffff9b"),
  AVATAR("Avatar", "#cc72f2");

  private final Element parent;
  private final String displayName;
  private final String chatColorHex;

  Element(Element parent, String displayName, String chatColorHex) {
    this.parent = parent;
    this.displayName = displayName;
    this.chatColorHex = chatColorHex;
  }

  Element(String displayName, String chatColorHex) {
    this(null, displayName, chatColorHex);
  }

  public boolean isSubElement() {
    return parent != null;
  }

  public boolean isSubElementOf(Element element) {
    return parent == element;
  }

  public Element parentElement() {
    return parent;
  }

  public String displayName() {
    return displayName;
  }

  public String chatColorHex() {
    return chatColorHex;
  }

  public int toIntHex() {
    return Integer.parseInt(chatColorHex.substring(1), 16);
  }

  public @NonNull TextColor textColor() {
    return TextColor.color(toIntHex());
  }

}
