package com.github.manu585.fourelements.storage.sql;

import java.nio.file.Path;
import java.util.Locale;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Where and how to connect. Plain data, reading it from a config is the caller's job.
 *
 * <p>Which fields matter depends on the {@link #dialect()}: SQLite only uses {@link #file()},
 * MySQL and MariaDB use the host, port, database and credentials.
 */
public record DatabaseSettings(
    SqlDialect dialect,
    String host,
    int port,
    String database,
    @Nullable String username,
    @Nullable String password,
    Path file,
    int poolSize,
    long connectionTimeoutMillis) {

  public String jdbcUrl() {
    return switch (dialect) {
      case MYSQL -> String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true", host, port, database);
      case SQLITE -> "jdbc:sqlite:" + file.toAbsolutePath().toString().replace('\\', '/') + "?foreign_keys=on&journal_mode=WAL&busy_timeout=" + connectionTimeoutMillis;
    };
  }

  /**
   * Human-readable target for log messages
   */
  public String address() {
    return dialect == SqlDialect.SQLITE
        ? "sqlite:" + file
        : dialect.name().toLowerCase(Locale.ROOT) + "://" + host + ":" + port + "/" + database;
  }

  // The generated toString would leak the password into logs
  @Override
  public @NonNull String toString() {
    return "DatabaseSettings[" + address() + "]";
  }

}
