package com.github.manu585.fourelements.bukkit;

import com.github.manu585.fourelements.bukkit.listeners.ConnectionListeners;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.storage.BenderStorage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.sql.SQLException;
import org.bukkit.plugin.java.JavaPlugin;

final class FourElementsBootstrap implements PluginBootstrap {

  private static final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("elements");

  private final JavaPlugin plugin;
  private final BenderManager benderManager;

  public FourElementsBootstrap(JavaPlugin plugin) throws SQLException {
    this.plugin = plugin;
    this.benderManager = new BenderManager(plugin, new BenderStorage());
  }

  @Override
  public void onEnable() {
    plugin.getLogger().info(plugin.getName() + " plugin enabled!");
    registerListeners();
  }

  @Override
  public void onDisable() {
    plugin.getLogger().info(plugin.getName() + " plugin disabled.");
  }

  @Override
  public void registerListeners() {
    plugin.getServer().getPluginManager().registerEvents(new ConnectionListeners(benderManager), plugin);
  }

  @Override
  public void bootstrap(BootstrapContext context) {
    context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {

    });
  }

}
