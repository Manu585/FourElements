package com.github.manu585.fourelements.bukkit.hud;

import com.github.manu585.fourelements.api.bender.Chi;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NonNull;

/**
 * The chi bar in the action bar
 */
public final class ChiHud {

  private static final int MAX_CHARS = 25;
  private static final String CHI_CHAR = "┃";

  private static final TextColor LIGHT = TextColor.color(255, 255, 255);
  private static final TextColor DARK = TextColor.color(100, 100, 100);

  private ChiHud() {}

  public static void send(@NonNull Audience audience, @NonNull Chi chi) {
    audience.sendActionBar(() -> component(chi));
  }

  private static @NonNull Component component(@NonNull Chi chi) {
    int lightBarsAmount = (int) Math.ceil(chi.fraction() * MAX_CHARS);
    int darkBarsAmount = MAX_CHARS - lightBarsAmount;

    Component lightBars = Component.text(CHI_CHAR.repeat(lightBarsAmount), LIGHT);
    Component darkBars = Component.text(CHI_CHAR.repeat(darkBarsAmount), DARK);

    return lightBars.append(darkBars);
  }

}
