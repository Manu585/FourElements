package com.github.manu585.fourelements.bukkit;

import com.github.manu585.fourelements.bukkit.bootstrap.FourElementsBootstrap;
import java.sql.SQLException;
import org.bukkit.plugin.java.JavaPlugin;

public final class FourElementsPlugin extends JavaPlugin {

  private FourElementsBootstrap internalBootstrap;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    try {
      internalBootstrap = new FourElementsBootstrap(this);
      internalBootstrap.onEnable();
    } catch (SQLException e) {
      getLogger().severe("Failed to initialize database: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    if (internalBootstrap != null) {
      internalBootstrap.onDisable();
    }
  }

}
