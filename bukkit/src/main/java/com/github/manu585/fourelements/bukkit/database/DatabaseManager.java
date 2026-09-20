package com.github.manu585.fourelements.bukkit.database;

import com.github.manu585.fourelements.bukkit.executor.DatabaseExecutorPool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

/**
 * Owns the connection pool and the executor database work runs on.
 *
 * <p>An instance only exists once the database has been reached, see {@link #connect}.
 */
public final class DatabaseManager {

  private final HikariDataSource dataSource;
  private final DatabaseExecutorPool executorPool;

  private DatabaseManager(HikariDataSource dataSource, DatabaseExecutorPool executorPool) {
    this.dataSource = dataSource;
    this.executorPool = executorPool;
  }

  /**
   * Opens the connection pool, failing fast if the database cannot be reached.
   *
   * @throws SQLException if no connection could be established, its message is the root cause
   */
  public static DatabaseManager connect(DatabaseSettings settings) throws SQLException {
    HikariDataSource dataSource;
    try {
      // Hikari opens a first connection right here and throws if that fails
      dataSource = new HikariDataSource(hikariConfig(settings));
    } catch (PoolInitializationException e) {
      throw new SQLException(rootMessage(e), e);
    }
    return new DatabaseManager(dataSource, new DatabaseExecutorPool(settings.poolSize()));
  }

  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public ExecutorService getExecutor() {
    return executorPool.getExecutor();
  }

  public void close() {
    executorPool.shutdown();
    dataSource.close();
  }

  private static HikariConfig hikariConfig(DatabaseSettings settings) {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(settings.jdbcUrl());
    hikariConfig.setUsername(settings.username());
    hikariConfig.setPassword(settings.password());
    hikariConfig.setMaximumPoolSize(settings.poolSize());
    hikariConfig.setConnectionTimeout(settings.connectionTimeoutMillis());
    hikariConfig.setPoolName("FourElements-HikariPool");

    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
    return hikariConfig;
  }

  /**
   * The driver wraps the actual reason several layers deep in multi-line messages,
   * the innermost one is the part worth showing to an admin.
   */
  private static String rootMessage(Throwable throwable) {
    Throwable root = throwable;
    while (root.getCause() != null) {
      root = root.getCause();
    }
    String message = root.getMessage();
    return message == null ? root.getClass().getSimpleName() : message.lines().findFirst().orElse(message);
  }

}
