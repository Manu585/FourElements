package com.github.manu585.fourelements.bukkit.hud;

import com.github.manu585.fourelements.bukkit.features.PlayerFeature;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.tick.TickScope;
import org.bukkit.entity.Player;

/**
 * Shows the chi bar to every player. The action bar stays visible for a few seconds, so a
 * refresh every few ticks is plenty to keep it up to date.
 */
public final class ChiHudFeature implements PlayerFeature {

  private static final int REFRESH_TICKS = 5;

  @Override
  public void attach(Player player, OnlineBenderPlayer bender, TickScope ticks) {
    ticks.every(REFRESH_TICKS, () -> ChiHud.send(player, bender.chi()));
  }

}
