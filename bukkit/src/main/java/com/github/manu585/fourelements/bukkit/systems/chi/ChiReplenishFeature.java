package com.github.manu585.fourelements.bukkit.systems.chi;

import com.github.manu585.fourelements.bukkit.features.PlayerFeature;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.tick.TickScope;
import org.bukkit.entity.Player;

/**
 * Gives every player their chi back over time.
 */
public final class ChiReplenishFeature implements PlayerFeature {

  private static final int CHI_PER_SECOND = 2;
  private static final int TICKS_PER_SECOND = 20;

  @Override
  public void attach(Player player, OnlineBenderPlayer bender, TickScope ticks) {
    ticks.every(TICKS_PER_SECOND, () -> bender.chi().restore(CHI_PER_SECOND));
  }

}
