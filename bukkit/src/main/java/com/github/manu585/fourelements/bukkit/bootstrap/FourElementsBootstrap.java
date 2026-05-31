package com.github.manu585.fourelements.bukkit.bootstrap;

import com.github.manu585.fourelements.api.FourElementsAPI;
import com.github.manu585.fourelements.bukkit.api.FourElementsProviderImpl;
import com.github.manu585.fourelements.bukkit.commands.system.CommandSystem;
import com.github.manu585.fourelements.bukkit.commands.WhoCommand;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.bukkit.listeners.ConnectionListeners;
import com.github.manu585.fourelements.bukkit.listeners.system.ListenerSystem;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.registry.BenderRegistry;
import com.github.manu585.fourelements.core.system.PluginSystem;
import java.sql.SQLException;
import java.util.List;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public final class FourElementsBootstrap {

  private final Plugin plugin;
  private final DatabaseManager databaseManager;
  private final BenderManager benderManager;
  private final FourElementsProviderImpl provider;
  private final List<PluginSystem> systems;

  public FourElementsBootstrap(Plugin plugin) throws SQLException {
    this.plugin = plugin;
    this.databaseManager = new DatabaseManager(plugin);
    this.benderManager = new BenderManager(plugin, new BenderRegistry());
    this.provider = new FourElementsProviderImpl(benderManager);
    this.systems = assembleSystems();
  }

  public void onEnable() {
    plugin.getLogger().info(plugin.getName() + " plugin enabled!");
    systems.forEach(PluginSystem::enable);
    registerAPI();
  }

  public void onDisable() {
    // Reverse order to mirror setup
    for (int i = systems.size() - 1; i >= 0; i--) {
      systems.get(i).disable();
    }

    plugin.getLogger().info(plugin.getName() + " plugin disabled.");
  }

  /**
   * Assemble plugin's systems
   * including Listeners and Commands
   *
   * @return List of assembled Systems
   */
  private List<PluginSystem> assembleSystems() {
    return List.of(
            commandSystem(),
            listenerSystem()
    );
  }

  /**
   * Build command system
   *
   * @return Command System
   */
  private PluginSystem commandSystem() {
    return new CommandSystem(plugin, List.of(
            new WhoCommand(benderManager)
    ));
  }

  /**
   * Build listener system
   *
   * @return Listener system
   */
  private PluginSystem listenerSystem() {
    return new ListenerSystem(plugin, List.of(
            new ConnectionListeners(benderManager)
    ));
  }

  private void registerAPI() {
    FourElementsAPI.setProvider(provider);
  }

}
