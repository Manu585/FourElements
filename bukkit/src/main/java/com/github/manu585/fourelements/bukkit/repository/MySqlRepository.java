package com.github.manu585.fourelements.bukkit.repository;

import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MySqlRepository {

  private static final Logger LOGGER = Logger.getLogger(MySqlRepository.class.getName());

  protected final DatabaseManager databaseManager;

  public MySqlRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  @FunctionalInterface
  protected interface SqlFunction<T> {
    T apply(Connection connection) throws SQLException;
  }

  @FunctionalInterface
  protected interface SqlConsumer {
    void accept(Connection connection) throws SQLException;
  }

  protected <T> CompletableFuture<T> queryAsync(String description, T fallback, SqlFunction<T> function) {
    return CompletableFuture.supplyAsync(() -> {
      try (Connection connection = databaseManager.getConnection()) {
        return function.apply(connection);
      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Database query failed: " + description, e);
        return fallback;
      }
    }, databaseManager.getExecutor());
  }

  protected CompletableFuture<Void> executeAsync(String description, SqlConsumer consumer) {
    return CompletableFuture.runAsync(() -> {
      try (Connection connection = databaseManager.getConnection()) {
        consumer.accept(connection);
      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Database update failed: " + description, e);
      }
    }, databaseManager.getExecutor());
  }

}
