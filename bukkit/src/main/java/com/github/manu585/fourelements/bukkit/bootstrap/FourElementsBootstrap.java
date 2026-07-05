package com.github.manu585.fourelements.bukkit.bootstrap;

import com.github.manu585.fourelements.api.FourElementsApi;
import com.github.manu585.fourelements.bukkit.api.FourElementsProviderImpl;
import com.github.manu585.fourelements.bukkit.commands.AddCommand;
import com.github.manu585.fourelements.bukkit.commands.InfoCommand;
import com.github.manu585.fourelements.bukkit.commands.system.CommandSystem;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.bukkit.database.SchemaMigrator;
import com.github.manu585.fourelements.bukkit.listeners.ConnectionListeners;
import com.github.manu585.fourelements.bukkit.listeners.system.ListenerSystem;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.repository.MySqlBenderRepository;
import com.github.manu585.fourelements.core.registry.OnlineBenderRegistry;
import com.github.manu585.fourelements.core.repository.BenderRepository;
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
  private final OnlineBenderRegistry benderRegistry;
  private final FourElementsProviderImpl provider;
  private final List<PluginSystem> systems;

  // Repositories
  private final BenderRepository benderRepository;

  public FourElementsBootstrap(Plugin plugin) throws SQLException {
    this.plugin = plugin;
    this.databaseManager = new DatabaseManager(plugin);

    // Migrate DB
    new SchemaMigrator(databaseManager, plugin.getLogger()).migrate();

    this.benderRepository = new MySqlBenderRepository(databaseManager);

    this.benderRegistry = new OnlineBenderRegistry();
    this.benderManager = new BenderManager(this.benderRegistry, this.benderRepository);
    this.provider = new FourElementsProviderImpl(benderManager);
    this.systems = assembleSystems();
  }

  public void onEnable() {
    plugin.getLogger().info(plugin.getName() + " plugin enabled!");
    systems.forEach(PluginSystem::enable);
    registerApi();
  }

  public void onDisable() {
    // Persist everyone still online before tearing anything down
    benderManager.online().forEach(online -> benderRepository.saveBender(online.benderPlayer()).join());

    // Reverse order to mirror setup
    for (int i = systems.size() - 1; i >= 0; i--) {
      systems.get(i).disable();
    }

    databaseManager.close();
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
   * Build command system.
   *
   * @return Command System
   */
  private PluginSystem commandSystem() {
    return new CommandSystem(plugin, List.of(
        new InfoCommand(benderManager),
        new AddCommand(benderManager)
    ));
  }

  /**
   * Build listener system.
   *
   * @return Listener system
   */
  private PluginSystem listenerSystem() {
    return new ListenerSystem(plugin, List.of(new ConnectionListeners(benderManager, benderRepository)));
  }

  /**
   * Registers the {@link FourElementsApi} provider.
   */
  private void registerApi() {
    FourElementsApi.setProvider(provider);
  }

}
