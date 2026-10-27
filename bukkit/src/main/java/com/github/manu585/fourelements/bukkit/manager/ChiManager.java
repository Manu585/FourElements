package com.github.manu585.fourelements.bukkit.manager;

import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

@AllArgsConstructor
@Getter
public final class ChiManager {

  private static final int MAX_CHARS = 25;
  private static final char CHI_CHAR = '┃';

  private final OnlineBenderPlayer onlineBenderPlayer;
  private final Audience audience;

  public void sendChiActionBar() {
    audience.sendActionBar(this::formattedChiComponent);
  }

  public Component formattedChiComponent() {
    int lightBarsAmount = (int) Math.ceil(onlineBenderPlayer.chi().fraction() * MAX_CHARS);
    int darkBarsAmount = MAX_CHARS - lightBarsAmount;

    Component lightBars = Component.text(String.valueOf(CHI_CHAR).repeat(lightBarsAmount), TextColor.color(255, 255, 255));
    Component darkBars = Component.text(String.valueOf(CHI_CHAR).repeat(darkBarsAmount), TextColor.color(100, 100, 100));

    return lightBars.append(darkBars);
  }

}
