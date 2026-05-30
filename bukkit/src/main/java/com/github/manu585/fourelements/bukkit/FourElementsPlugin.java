package com.github.manu585.fourelements.bukkit;

import java.sql.SQLException;
import org.bukkit.plugin.java.JavaPlugin;

public class FourElementsPlugin extends JavaPlugin {

  private FourElementsBootstrap bootstrap;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    try {
      bootstrap = new FourElementsBootstrap(this);
      bootstrap.onEnable();
    } catch (SQLException e) {
      getLogger().severe("Failed to initialize database: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    if (bootstrap != null) {
      bootstrap.onDisable();
    }
  }

}
