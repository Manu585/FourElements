package com.github.manu585.fourelements.bukkit.bootstrap;

import com.github.manu585.fourelements.api.FourElementsApi;
import com.github.manu585.fourelements.bukkit.bstats.BstatsMetrics;
import com.github.manu585.fourelements.bukkit.commands.AvatarCommand;
import com.github.manu585.fourelements.bukkit.commands.InfoCommand;
import com.github.manu585.fourelements.bukkit.commands.ChooseCommand;
import com.github.manu585.fourelements.bukkit.commands.SubElementCommand;
import com.github.manu585.fourelements.bukkit.config.DatabaseSettingsReader;
import com.github.manu585.fourelements.bukkit.listeners.ConnectionListeners;
import com.github.manu585.fourelements.bukkit.listeners.bending.AvatarSwitchElementListener;
import com.github.manu585.fourelements.bukkit.listeners.bending.EnterBendingModeListener;
import com.github.manu585.fourelements.bukkit.systems.CommandSystem;
import com.github.manu585.fourelements.bukkit.systems.ListenerSystem;
import com.github.manu585.fourelements.core.exceptions.StartupException;
import com.github.manu585.fourelements.core.system.PluginSystem;
import java.sql.SQLException;
import com.github.manu585.fourelements.storage.migration.Migrator;
import com.github.manu585.fourelements.storage.sql.Database;
import com.github.manu585.fourelements.storage.sql.DatabaseSettings;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * Composition root of the plugin.
 *
 * <p>{@link #start} runs the startup phases in order and only hands out an instance once
 * all of them succeeded, so a bootstrap is always fully started and never half-built:
 *
 * <ol>
 *   <li>{@code connectDatabase}: open the pool, abort if the database is unreachable</li>
 *   <li>{@code migrateSchema}: bring the db schema up to the latest version</li>
 *   <li>{@code wireServices}: build repositories, managers and the API provider</li>
 *   <li>{@code enableSystems}: register commands and listeners</li>
 *   <li>{@code publishApi}: expose the provider through {@link FourElementsApi}</li>
 * </ol>
 *
 * <p>{@link #shutdown} undoes them in reverse.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FourElementsBootstrap {

  private final Plugin plugin;
  private final Database database;
  private final Services services;
  private final List<PluginSystem> systems;

  /**
   * Starts the plugin.
   *
   * @throws StartupException if a phase failed for an expected reason, everything opened up to that point has already been released again
   */
  public static FourElementsBootstrap start(Plugin plugin) throws StartupException {
    Database database = connectDatabase(plugin);
    try {
      migrateSchema(plugin, database);
      Services services = wireServices(database);
      List<PluginSystem> systems = enableSystems(plugin, services);
      publishApi(services);

      if (plugin.getConfig().getBoolean("bstats", true)) {
        new BstatsMetrics(plugin, services.benderManager());
      } else {
        plugin.getLogger().info("bStats is disabled in config.yml, no statistics are collected.");
      }

      plugin.getLogger().info(plugin.getName() + " enabled!");
      return new FourElementsBootstrap(plugin, database, services, systems);
    } catch (StartupException | RuntimeException exception) {
      database.close();
      throw exception;
    }
  }

  public void shutdown() {
    saveOnlineBenders();
    disableSystems();
    database.close();

    plugin.getLogger().info(plugin.getName() + " disabled.");
  }

  // ---- Startup phases ----

  private static @NonNull Database connectDatabase(@NonNull Plugin plugin) throws StartupException {
    DatabaseSettings settings = DatabaseSettingsReader.read(plugin.getConfig(), plugin.getDataFolder().toPath());
    try {
      return Database.connect(settings);
    } catch (SQLException e) {
      throw new StartupException("No database connection to " + settings.address() + " (" + e.getMessage() + ")", e);
    } catch (IllegalArgumentException | IllegalStateException e) {
      throw new StartupException("Invalid database settings in config.yml (" + e.getMessage() + ")", e);
    }
  }

  private static void migrateSchema(@NonNull Plugin plugin, Database database) throws StartupException {
    try {
      new Migrator(database, plugin.getLogger()).migrate();
    } catch (SQLException e) {
      String reason = e.getCause() != null ? e.getMessage() + ": " + e.getCause().getMessage() : e.getMessage();
      throw new StartupException("Database schema migration failed (" + reason + ")", e);
    }
  }

  private static @NonNull Services wireServices(Database database) {
    return Services.wire(database);
  }

  private static @NonNull List<PluginSystem> enableSystems(Plugin plugin, Services services) {
    List<PluginSystem> systems = List.of(
        commandSystem(plugin, services),
        listenerSystem(plugin, services)
    );
    systems.forEach(PluginSystem::enable);
    return systems;
  }

  private static void publishApi(@NonNull Services services) {
    FourElementsApi.setProvider(services.provider());
  }

  // ---- Shutdown phases ----

  private void saveOnlineBenders() {
    // All at once instead of one after the other, a failed save is logged and must not stop the shutdown
    CompletableFuture<?>[] saves = services.benderManager().online().stream()
        .map(online -> services.benderManager().save(online.benderPlayer()).exceptionally(error -> {
          plugin.getLogger().severe("Could not save " + online.uuid() + " on shutdown: " + error.getMessage());
          return null;
        }))
        .toArray(CompletableFuture[]::new);
    CompletableFuture.allOf(saves).join();
  }

  private void disableSystems() {
    systems.reversed().forEach(PluginSystem::disable);
  }

  // ---- Systems ----

  @Contract("_, _ -> new")
  private static @NonNull PluginSystem commandSystem(Plugin plugin, @NonNull Services services) {
    return new CommandSystem(plugin, List.of(
        new InfoCommand(services.benderManager()),
        new ChooseCommand(services.benderManager()),
        new AvatarCommand(services.benderManager()),
        new SubElementCommand(services.benderManager())
    ));
  }

  @Contract("_, _ -> new")
  private static @NonNull PluginSystem listenerSystem(Plugin plugin, @NonNull Services services) {
    return new ListenerSystem(plugin, List.of(
        new ConnectionListeners(services.benderManager()),
        new EnterBendingModeListener(services.benderManager(), services.bendingModeCombination()),
        new AvatarSwitchElementListener(services.benderManager())
    ));
  }

}
