package com.github.manu585.fourelements.storage.migration;

import com.github.manu585.fourelements.storage.sql.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Brings the database up to the last of the {@link Migrations#all() migrations} on startup.
 *
 * <p>A {@code schema_version} bookkeeping table records which migrations have run.
 * On each boot the migrator reads the highest applied version and executes only the
 * migrations newer than it, in order.
 *
 * <p><strong>Transaction caveat:</strong> in MySQL, DDL statements (such as
 * {@code CREATE TABLE}) trigger an implicit commit, so the surrounding transaction
 * cannot fully roll back a partially-applied DDL migration. Three things keep this safe:
 * the {@code CREATE TABLE IF NOT EXISTS} form makes re-runs harmless, an
 * {@code ALTER TABLE ... ADD COLUMN} whose column already exists (left behind by an earlier,
 * interrupted run) is skipped instead of failing, and the {@code schema_version} row — the
 * single source of truth for what has been applied — is written only after all statements
 * succeed. Every migration must therefore be safe to run again from its first statement.
 */
public final class Migrator {

  private static final String CREATE_SCHEMA_VERSION_TABLE = "CREATE TABLE IF NOT EXISTS schema_version ("
      + "version INT PRIMARY KEY, "
      + "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
      + ")";
  private static final String SELECT_SCHEMA_VERSION = "SELECT MAX(version) AS version FROM schema_version";
  private static final String INSERT_SCHEMA_VERSION = "INSERT INTO schema_version (version) VALUES (?)";

  private final Database database;
  private final Logger logger;

  public Migrator(Database database, Logger logger) {
    this.database = database;
    this.logger = logger;
  }

  /**
   * Applies every migration that has not been recorded yet.
   *
   * @throws SQLException if the connection cannot be obtained or a migration fails
   */
  public void migrate() throws SQLException {
    try (Connection connection = database.getConnection()) {
      createSchemaVersionTable(connection);

      int currentVersion = readCurrentVersion(connection);
      List<Migration> migrations = Migrations.all();

      if (currentVersion >= migrations.size()) {
        logger.info("Database schema up to date (version " + currentVersion + ")");
        return;
      }

      for (Migration migration : migrations) {
        if (migration.version() > currentVersion) {
          applyMigration(connection, migration);
        }
      }
    }
  }

  private void createSchemaVersionTable(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(CREATE_SCHEMA_VERSION_TABLE);
    }
  }

  private int readCurrentVersion(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(SELECT_SCHEMA_VERSION)) {
      return resultSet.next() ? resultSet.getInt("version") : 0;
    }
  }

  private void applyMigration(Connection connection, Migration migration) throws SQLException {
    boolean previousAutoCommit = connection.getAutoCommit();
    connection.setAutoCommit(false);

    try (Statement statement = connection.createStatement()) {
      for (String sql : migration.statements().apply(database.dialect())) {
        execute(statement, sql);
      }
      recordVersion(connection, migration.version());

      connection.commit();
      logger.info("Applied migration to schema version " + migration.version() + " (" + migration.description() + ")");
    } catch (SQLException e) {
      connection.rollback();
      throw new SQLException("Migration to version " + migration.version() + " failed, rolled back", e);
    } finally {
      connection.setAutoCommit(previousAutoCommit);
    }
  }

  private void execute(Statement statement, String sql) throws SQLException {
    try {
      statement.execute(sql);
    } catch (SQLException e) {
      if (database.dialect().isDuplicateColumn(e) && isAddColumn(sql)) {
        logger.warning("Column already exists, skipping (left over from an interrupted migration): " + sql);
        return;
      }
      throw e;
    }
  }

  private static boolean isAddColumn(String sql) {
    String normalized = sql.trim().toUpperCase(Locale.ROOT);
    return normalized.startsWith("ALTER TABLE") && normalized.contains(" ADD COLUMN ");
  }

  private void recordVersion(Connection connection, int version) throws SQLException {
    try (PreparedStatement insert = connection.prepareStatement(INSERT_SCHEMA_VERSION)) {
      insert.setInt(1, version);
      insert.executeUpdate();
    }
  }

}
