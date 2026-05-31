package com.github.manu585.fourelements.bukkit.manager;

import com.github.manu585.fourelements.core.bender.Bender;
import com.github.manu585.fourelements.core.storage.BenderStorage;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;

public final class BenderManager {

  private final JavaPlugin plugin;
  private final BenderStorage storage;

  public BenderManager(JavaPlugin plugin, BenderStorage storage) {
    this.plugin = plugin;
    this.storage = storage;
  }

  // Called upon first connection of a player
  // stores bender in map, assigns elements, ...
  public void onConnect(Bender bender) {
    persist(bender);

    // Add elements from DB, yada yada
  }

  public Bender getBender(UUID uuid) {
    return storage.get(uuid);
  }

  public void persist(Bender bender) {
    storage.put(bender.uuid(), bender);
  }

  public void delete(UUID uuid) {
    storage.remove(uuid);
  }

}
