package com.github.manu585.fourelements.bukkit.config;

import com.github.manu585.fourelements.storage.sql.DatabaseSettings;
import com.github.manu585.fourelements.storage.sql.SqlDialect;
import java.nio.file.Path;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NonNull;

/**
 * Reads the {@code database} section of the plugin config into {@link DatabaseSettings}.
 */
public final class DatabaseSettingsReader {

  private DatabaseSettingsReader() {
    throw new AssertionError("No instances");
  }

  /**
   * Read the settings from the config.
   *
   * <p>A config written before the database type existed has no {@code database.type} and
   * means MySQL, even though the bundled default is SQLite, otherwise an update would
   * silently point an existing server at a new, empty database.
   *
   * @param config the plugin config
   * @param dataFolder the plugin's data folder, SQLite files are resolved against it
   * @throws IllegalArgumentException if the configured type is unknown
   */
  public static DatabaseSettings read(@NonNull ConfigurationSection config, @NonNull Path dataFolder) {
    String type = config.contains("database.type", true) ? config.getString("database.type", "sqlite") : "mysql";
    SqlDialect dialect = SqlDialect.fromName(type);

    // SQLite allows one writer at a time, so a single connection avoids "database is locked"
    int poolSize = dialect == SqlDialect.SQLITE ? 1 : config.getInt("database.pool-size");

    return new DatabaseSettings(
        dialect,
        config.getString("database.host", "localhost"),
        config.getInt("database.port"),
        config.getString("database.database", "fourelements"),
        config.getString("database.username"),
        config.getString("database.password"),
        dataFolder.resolve(config.getString("database.file", "fourelements.db")),
        poolSize,
        config.getLong("database.connection-timeout")
    );
  }

}
