package com.github.manu585.fourelements.bukkit.features;

import com.github.manu585.fourelements.bukkit.systems.TickSystem;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.tick.TickScope;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 * Attaches every {@link PlayerFeature} to a joining player and ends them all when the player quits.
 */
public final class PlayerFeatures {

  private final TickSystem tickSystem;
  private final List<PlayerFeature> features;
  private final Logger logger;

  private final Map<UUID, TickScope> scopes = new ConcurrentHashMap<>();

  public PlayerFeatures(TickSystem tickSystem, List<PlayerFeature> features, Logger logger) {
    this.tickSystem = tickSystem;
    this.features = List.copyOf(features);
    this.logger = logger;
  }

  public void attach(Player player, OnlineBenderPlayer bender) {
    TickScope scope = tickSystem.scope();
    detach(player.getUniqueId());
    scopes.put(player.getUniqueId(), scope);

    for (PlayerFeature feature : features) {
      // One failing feature must not keep the others from starting
      try {
        feature.attach(player, bender, scope);
      } catch (RuntimeException e) {
        logger.log(Level.SEVERE, "Feature " + feature.getClass().getSimpleName() + " failed to attach for " + player.getName(), e);
      }
    }
  }

  public void detach(UUID uuid) {
    TickScope scope = scopes.remove(uuid);
    if (scope != null) {
      scope.close();
    }
  }

}
