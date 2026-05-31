package com.github.manu585.fourelements.bukkit.database;

import com.github.manu585.fourelements.bukkit.executor.DatabaseExecutorPool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

// TODO: Ugly class, fix in future
public final class DatabaseManager {

  private final HikariDataSource dataSource;
  private final DatabaseExecutorPool executorPool;

  public DatabaseManager(Plugin plugin) {
    FileConfiguration config = plugin.getConfig();

    int port = config.getInt("database.port");
    String host = config.getString("database.host");
    String database = config.getString("database.database");

    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true", host, port, database));
    hikariConfig.setUsername(config.getString("database.username"));
    hikariConfig.setPassword(config.getString("database.password"));
    hikariConfig.setMaximumPoolSize(config.getInt("database.pool-size"));
    hikariConfig.setConnectionTimeout(config.getInt("database.connection-timeout"));
    hikariConfig.setPoolName("FourElements-HikariPool");

    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

    this.dataSource = new HikariDataSource(hikariConfig);
    this.executorPool = new DatabaseExecutorPool(5);
  }

  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public ExecutorService getExecutor() {
    return executorPool.getExecutor();
  }

  public void close() {
    executorPool.shutdown();
    if (dataSource != null &&  !dataSource.isClosed()) {
      dataSource.close();
    }
  }

  public boolean isConnected() {
    return dataSource != null && !dataSource.isClosed();
  }

}
