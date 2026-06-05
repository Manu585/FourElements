package com.github.manu585.fourelements.bukkit.repository;

import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.bukkit.repository.interfaces.SqlConsumer;
import com.github.manu585.fourelements.bukkit.repository.interfaces.SqlFunction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parent class for all MySql repositories
 * containing helper methods for easier Query consumption and execution
 */
public abstract class AbstractMySqlRepository {

  private static final Logger LOGGER = Logger.getLogger(AbstractMySqlRepository.class.getName());

  protected final DatabaseManager databaseManager;

  public AbstractMySqlRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
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
