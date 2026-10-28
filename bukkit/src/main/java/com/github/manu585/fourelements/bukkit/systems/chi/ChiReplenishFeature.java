package com.github.manu585.fourelements.bukkit.systems.chi;

import com.github.manu585.fourelements.bukkit.features.PlayerFeature;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.bender.chi.ChiPool;
import com.github.manu585.fourelements.core.tick.Registration;
import com.github.manu585.fourelements.core.tick.TickScope;
import org.bukkit.entity.Player;

/**
 * Gives a player their chi back over time. It only runs while the player is missing chi.
 * It starts the moment chi is spent and stops again once the pool is full.
 */
public final class ChiReplenishFeature implements PlayerFeature {

  private static final double CHI_PER_SECOND = 2;
  private static final int TICKS_PER_SECOND = 20;
  private static final double CHI_PER_TICK = CHI_PER_SECOND / TICKS_PER_SECOND;

  @Override
  public void attach(Player player, OnlineBenderPlayer bender, TickScope ticks) {
    ChiPool chi = bender.chi();
    Replenish replenish = new Replenish(chi, ticks);
    chi.onDeficit(_ -> replenish.start());
  }

  // Only touched from the main thread
  private static final class Replenish {

    private final ChiPool chi;
    private final TickScope ticks;

    private Registration running;

    private Replenish(ChiPool chi, TickScope ticks) {
      this.chi = chi;
      this.ticks = ticks;
    }

    // Only called when the pool just stopped being full, so nothing is running yet
    private void start() {
      running = ticks.every(1, this::tick);
    }

    private void tick() {
      chi.restore(CHI_PER_TICK);
      if (chi.current() >= chi.max()) {
        running.cancel();
        running = null;
      }
    }

  }

}
