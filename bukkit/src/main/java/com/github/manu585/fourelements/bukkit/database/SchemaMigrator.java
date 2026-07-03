package com.github.manu585.fourelements.bukkit.database;

import com.github.manu585.fourelements.core.database.Migration;
import com.github.manu585.fourelements.core.database.SchemaVersion;
import com.github.manu585.fourelements.core.database.SqlQueries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 * Brings the database up to {@link SchemaVersion#latestVersion()} on startup.
 *
 * <p>A {@code schema_version} bookkeeping table records which migrations have run.
 * On each boot the migrator reads the highest applied version and executes only the
 * migrations newer than it, in order.
 *
 * <p><strong>Transaction caveat:</strong> in MySQL, DDL statements (such as
 * {@code CREATE TABLE}) trigger an implicit commit, so the surrounding transaction
 * cannot fully roll back a partially-applied DDL migration. Two things keep this safe:
 * the {@code CREATE TABLE IF NOT EXISTS} form makes re-runs harmless, and the
 * {@code schema_version} row — the single source of truth for what has been applied —
 * is written only after all statements succeed.
 */
public final class SchemaMigrator {

  private final DatabaseManager databaseManager;
  private final Logger logger;

  public SchemaMigrator(final DatabaseManager databaseManager, final Logger logger) {
    this.databaseManager = databaseManager;
    this.logger = logger;
  }

  /**
   * Applies every migration that has not been recorded yet.
   *
   * @throws SQLException if the connection cannot be obtained or a migration fails
   */
  public void migrate() throws SQLException {
    try (Connection connection = databaseManager.getConnection()) {
      createSchemaVersionTable(connection);

      final int currentVersion = readCurrentVersion(connection);
      final List<Migration> migrations = SchemaVersion.migrations();

      if (currentVersion >= migrations.size()) {
        logger.info("Database schema up to date (version " + currentVersion + ")");
        return;
      }

      for (final Migration migration : migrations) {
        if (migration.version() > currentVersion) {
          applyMigration(connection, migration);
        }
      }
    }
  }

  private void createSchemaVersionTable(final Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(SqlQueries.CREATE_SCHEMA_VERSION_TABLE.getQuery());
    }
  }

  private int readCurrentVersion(final Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(SqlQueries.SELECT_SCHEMA_VERSION.getQuery())) {
      return resultSet.next() ? resultSet.getInt("version") : 0;
    }
  }

  private void applyMigration(final Connection connection, final Migration migration) throws SQLException {
    final boolean previousAutoCommit = connection.getAutoCommit();
    connection.setAutoCommit(false);

    try (Statement statement = connection.createStatement()) {
      for (final String sql : migration.statements()) {
        statement.execute(sql);
      }
      recordVersion(connection, migration.version());

      connection.commit();
      logger.info("Applied migration to schema version " + migration.version());
    } catch (final SQLException e) {
      connection.rollback();
      throw new SQLException("Migration to version " + migration.version() + " failed, rolled back", e);
    } finally {
      connection.setAutoCommit(previousAutoCommit);
    }
  }

  private void recordVersion(final Connection connection, final int version) throws SQLException {
    try (PreparedStatement insert = connection.prepareStatement(SqlQueries.INSERT_SCHEMA_VERSION.getQuery())) {
      insert.setInt(1, version);
      insert.executeUpdate();
    }
  }

}
