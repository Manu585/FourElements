package com.github.manu585.fourelements.bukkit;

import com.github.manu585.fourelements.bukkit.bootstrap.FourElementsBootstrap;
import com.github.manu585.fourelements.bukkit.bootstrap.StartupException;
import org.bukkit.plugin.java.JavaPlugin;

public final class FourElementsPlugin extends JavaPlugin {

  // Stays null when startup was aborted. Cannot be final, Bukkit owns the constructor
  private FourElementsBootstrap bootstrap;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    try {
      bootstrap = FourElementsBootstrap.start(this);
    } catch (StartupException e) {
      getLogger().severe("Startup aborted: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    if (bootstrap != null) {
      bootstrap.shutdown();
    }
  }

}
