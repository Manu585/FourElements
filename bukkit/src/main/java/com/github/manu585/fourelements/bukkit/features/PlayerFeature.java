package com.github.manu585.fourelements.bukkit.features;

import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.tick.TickScope;
import org.bukkit.entity.Player;

/**
 * Behaviour that is switched on for every player who joins, e.g. a HUD or passive regeneration.
 * Add an instance to {@link PlayerFeatures} to enable it.
 */
@FunctionalInterface
public interface PlayerFeature {

  /**
   * Called on the main thread when the player joined and their bender is online.
   *
   * @param ticks everything registered here stops by itself when the player quits
   */
  void attach(Player player, OnlineBenderPlayer bender, TickScope ticks);

}
