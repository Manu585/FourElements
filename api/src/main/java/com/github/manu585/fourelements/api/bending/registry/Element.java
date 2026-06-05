package com.github.manu585.fourelements.api.bending.registry;

import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NonNull;

/**
 * All available bendable elements.
 */
public enum Element {

  AIR("Air", "#9be7ff"),
  SPIRITUAL(Element.AIR, "Spiritual", "#d29bff"),
  FLIGHT(Element.AIR, "Flight", "#9ba7ff"),
  SOUND(Element.AIR, "Sound", "#bfbfbf"),

  WATER("Water", "#3aa0ff"),
  HEALING(Element.WATER, "Healing", "#9bffa5"),
  PLANT(Element.WATER, "Plant", "#1e732f"),
  BLOOD(Element.WATER, "Blood", "#780c0c"),
  ICE(Element.WATER, "Ice", "#0c89c7"),

  EARTH("Earth", "#7a5230"),
  METAL(Element.EARTH, "Metal", "#c9cfbe"),
  SAND(Element.EARTH, "Sand", "#ffe49b"),
  LAVA(Element.EARTH, "Lava", "#ffc89b"),

  FIRE("Fire", "#d93030"),
  LIGHTNING(Element.FIRE, "Lightning", "#ffffff"),
  COMBUSTION(Element.FIRE, "Combustion", "#bf3834"),

  CHI("Chi", "#ffff9b"),

  AVATAR("Avatar", "#cc72f2");

  private final Element parent;
  private final String displayName;
  private final String chatColorHex;

  /**
   * Sub-Element constructor.
   *
   * @param parent Parent Element if given
   * @param displayName Display name of Element
   * @param chatColorHex Hex color code of Element
   */
  Element(Element parent, String displayName, String chatColorHex) {
    this.parent = parent;
    this.displayName = displayName;
    this.chatColorHex = chatColorHex;
  }

  /**
   * Element constructor.
   *
   * @param displayName Display name of Element
   * @param chatColorHex Hex color code of Element
   */
  Element(String displayName, String chatColorHex) {
    this(null, displayName, chatColorHex);
  }

  /**
   * Check if Element is a Sub-Element.
   *
   * @return <code>true</code> if it is one, else <code>false</code>
   */
  public boolean isSubElement() {
    return parent != null;
  }

  /**
   * Check if Element is Sub-Element of the parameters Element.
   *
   * @param element Main Element to compare
   * @return <code>true</code> if it is, else <code>false</code>
   */
  public boolean isSubElementOf(Element element) {
    return parent == element;
  }

  /**
   * Get the parent element of given Sub-Element.
   *
   * @return Parent element, if given Element is a main Element it will return null
   */
  public Element parentElement() {
    return parent;
  }

  /**
   * Get the display name of given element or Sub-Element.
   *
   * @return Display name String
   */
  public String displayName() {
    return displayName;
  }

  /**
   * Get the chat color hex code of given element or Sub-Element.
   *
   * @return hex color code String
   */
  public String chatColorHex() {
    return chatColorHex;
  }

  /**
   * Get the give hex color code String of Element or Sub-Element parsed as an Integer.
   *
   * @return Parsed Integer of hex color code
   */
  public int toIntHex() {
    return Integer.parseInt(chatColorHex.substring(1), 16);
  }

  /**
   * Get the parsed Paper Adventure {@link TextColor} of given Element or Sub-Element.
   *
   * @return Parsed Paper Adventure {@link TextColor} to use in Components
   */
  public @NonNull TextColor textColor() {
    return TextColor.color(toIntHex());
  }

}
