package com.github.manu585.fourelements.bukkit.manager;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.core.registry.BenderRegistry;
import java.util.UUID;
import org.bukkit.plugin.Plugin;

public final class BenderManager {

  private final Plugin plugin;
  private final BenderRegistry storage;

  public BenderManager(Plugin plugin, BenderRegistry storage) {
    this.plugin = plugin;
    this.storage = storage;
  }

  // Called upon first connection of a player
  // stores bender in map, assigns elements, ...
  public void onConnect(Bender benderPlayer) {
    persist(benderPlayer);

    // Add elements from DB, yada yada
  }

  public Bender getBender(UUID uuid) {
    return storage.get(uuid);
  }

  public void persist(Bender benderPlayer) {
    storage.put(benderPlayer.uuid(), benderPlayer);
  }

  public void delete(UUID uuid) {
    storage.remove(uuid);
  }

}
