package com.github.manu585.fourelements.bukkit.text;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * Turns elements and Sub-Elements into chat components in their own color. The API only
 * knows the hex code, so the text library stays on the platform side.
 */
public final class BendingText {

  private BendingText() {
    throw new AssertionError("No instances");
  }

  public static Component name(Element element) {
    return Component.text(element.displayName(), color(element.chatColorHex()));
  }

  public static Component name(SubElement subElement) {
    return Component.text(subElement.displayName(), color(subElement.chatColorHex()));
  }

  private static TextColor color(String hex) {
    return Objects.requireNonNull(TextColor.fromHexString(hex), "Invalid hex color " + hex);
  }

}
