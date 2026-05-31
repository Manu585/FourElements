package com.github.manu585.fourelements.bukkit.bootstrap;

import com.github.manu585.fourelements.api.FourElementsAPI;
import com.github.manu585.fourelements.bukkit.api.FourElementsProviderImpl;
import com.github.manu585.fourelements.bukkit.commands.assembler.CommandsAssembler;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.bukkit.listeners.ConnectionListeners;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.registry.BenderRegistry;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.sql.SQLException;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public final class FourElementsBootstrap {

  private final JavaPlugin plugin;
  private final DatabaseManager databaseManager;
  private final BenderManager benderManager;
  private final FourElementsProviderImpl provider;

  public FourElementsBootstrap(JavaPlugin plugin) throws SQLException {
    this.plugin = plugin;
    this.databaseManager = new DatabaseManager(plugin);
    this.benderManager = new BenderManager(plugin, new BenderRegistry());
    this.provider = new FourElementsProviderImpl(benderManager);
  }

  public void onEnable() {
    plugin.getLogger().info(plugin.getName() + " plugin enabled!");

    registerCommands();
    registerListeners();
    registerAPI();
  }

  public void onDisable() {
    plugin.getLogger().info(plugin.getName() + " plugin disabled.");
  }

  public void registerListeners() {
    plugin.getServer().getPluginManager().registerEvents(new ConnectionListeners(benderManager), plugin);
  }

  public void registerCommands() {
    CommandsAssembler commands = new CommandsAssembler(benderManager);
    plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(commands.build(), "Four Elements commands", List.of("fe", "bending")));
  }

  public void registerAPI() {
    FourElementsAPI.setProvider(provider);
  }

}
