package com.github.manu585.fourelements.bukkit;

import com.github.manu585.fourelements.bukkit.commands.assembler.FourElementsCommands;
import com.github.manu585.fourelements.bukkit.listeners.ConnectionListeners;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.storage.BenderStorage;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.sql.SQLException;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public final class FourElementsInternalBootstrap {

  private final JavaPlugin plugin;
  private final BenderManager benderManager;

  public FourElementsInternalBootstrap(JavaPlugin plugin) throws SQLException {
    this.plugin = plugin;
    this.benderManager = new BenderManager(plugin, new BenderStorage());
  }

  public void onEnable() {
    plugin.getLogger().info(plugin.getName() + " plugin enabled!");

    registerCommands();
    registerListeners();
  }

  public void onDisable() {
    plugin.getLogger().info(plugin.getName() + " plugin disabled.");
  }

  public void registerListeners() {
    plugin.getServer().getPluginManager().registerEvents(new ConnectionListeners(benderManager), plugin);
  }

  public void registerCommands() {
    FourElementsCommands commands = new FourElementsCommands(benderManager);

    plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(
            commands.build(),
            "Four Elements commands",
            List.of("fe", "bending")));
  }

}
