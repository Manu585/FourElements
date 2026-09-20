package com.github.manu585.fourelements.bukkit.database;

import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NonNull;

/**
 * Immutable snapshot of the database section of the plugin config.
 */
public record DatabaseSettings(String host, int port, String database, String username, String password, int poolSize, long connectionTimeoutMillis) {

  public static DatabaseSettings from(@NonNull ConfigurationSection config) {
    return new DatabaseSettings(
        config.getString("database.host"),
        config.getInt("database.port"),
        config.getString("database.database"),
        config.getString("database.username"),
        config.getString("database.password"),
        config.getInt("database.pool-size"),
        config.getLong("database.connection-timeout")
    );
  }

  public String jdbcUrl() {
    return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true", host, port, database);
  }

  /**
   * Human-readable target for log messages
   */
  public String address() {
    return host + ":" + port + "/" + database;
  }

  // The generated toString would leak the password into logs
  @Override
  public @NonNull String toString() {
    return "DatabaseSettings[" + address() + "]";
  }

}
