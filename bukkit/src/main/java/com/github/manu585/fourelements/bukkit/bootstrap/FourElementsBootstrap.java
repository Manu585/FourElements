package com.github.manu585.fourelements.bukkit.bootstrap;

import com.github.manu585.fourelements.api.FourElementsApi;
import com.github.manu585.fourelements.bukkit.api.FourElementsProviderImpl;
import com.github.manu585.fourelements.bukkit.commands.AddCommand;
import com.github.manu585.fourelements.bukkit.commands.InfoCommand;
import com.github.manu585.fourelements.bukkit.systems.CommandSystem;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.bukkit.database.SchemaMigrator;
import com.github.manu585.fourelements.bukkit.listeners.ConnectionListeners;
import com.github.manu585.fourelements.bukkit.listeners.bending.EnterBendingModeListener;
import com.github.manu585.fourelements.bukkit.systems.ListenerSystem;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.manager.bendermode.BendingModeCombination;
import com.github.manu585.fourelements.bukkit.manager.bendermode.SimpleInput;
import com.github.manu585.fourelements.bukkit.repository.MySqlBenderRepository;
import com.github.manu585.fourelements.core.registry.OnlineBenderRegistry;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import com.github.manu585.fourelements.core.system.PluginSystem;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public final class FourElementsBootstrap {

  private final Plugin plugin;
  private final DatabaseManager databaseManager;

  private BenderManager benderManager;
  private OnlineBenderRegistry benderRegistry;
  private BendingModeCombination bendingModeCombination;
  private FourElementsProviderImpl provider;
  private List<PluginSystem> systems;
  private BenderRepository benderRepository;

  public FourElementsBootstrap(Plugin plugin) throws SQLException {
    this.plugin = plugin;
    this.databaseManager = new DatabaseManager(plugin);

    if (this.databaseManager.getConnection() == null || this.databaseManager.getConnection().isClosed()) {
      plugin.getLogger().severe("No active Database connection.");
      plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
  }

  public void onEnable() {
    handleData();
    initManagers();
    initApi();
    enableSystems();

    plugin.getLogger().info(plugin.getName() + " plugin enabled!");
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

  private void handleData() {
    try {
      new SchemaMigrator(databaseManager, plugin.getLogger()).migrate();
    } catch (SQLException e) {
      plugin.getLogger().severe(e.getMessage());
      plugin.getServer().getPluginManager().disablePlugin(plugin);
      return;
    }

    this.benderRepository = new MySqlBenderRepository(databaseManager);
    this.benderRegistry = new OnlineBenderRegistry();
  }

  private void initManagers() {
    this.benderManager = new BenderManager(this.benderRegistry, this.benderRepository);
    this.bendingModeCombination = new BendingModeCombination(List.of(SimpleInput.of(true, false, false, false, false, false, false)), benderManager);
  }

  private void initApi() {
    this.provider = new FourElementsProviderImpl(this.benderManager);
  }

  private void enableSystems() {
    systems = assembleSystems();
    systems.forEach(PluginSystem::enable);
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
    return new ListenerSystem(plugin, List.of(
        new ConnectionListeners(benderManager, benderRepository),
        new EnterBendingModeListener(benderManager, bendingModeCombination)
    ));
  }

  /**
   * Registers the {@link FourElementsApi} provider.
   */
  private void registerApi() {
    FourElementsApi.setProvider(provider);
  }

}
