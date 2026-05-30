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

  public void persist(UUID uuid, Bender bender) {
    storage.put(uuid, bender);
  }

  public Bender getBender(UUID uuid) {
    return storage.get(uuid);
  }

  public void clear(UUID uuid) {
    storage.remove(uuid);
  }

}
