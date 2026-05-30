package com.github.manu585.fourelements.core.database;

/**
 * Centralized SQL statement constants for the FourElements database layer.
 * All table and column names match the canonical schema.
 */
public final class SqlStatements {

  public static final String CREATE_SCHEMA_VERSION_TABLE =
          "CREATE TABLE IF NOT EXISTS schema_version ("
                  + "version INT PRIMARY KEY, "
                  + "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                  + ")";

  // -----------------------------------------------------------------------
  // DDL - Table creation
  // -----------------------------------------------------------------------
  public static final String CREATE_PLAYERS_TABLE =
          "CREATE TABLE IF NOT EXISTS players ("
                  + "uuid CHAR(36) PRIMARY KEY, "
                  + "username VARCHAR(16) NOT NULL, "
                  + "first_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                  + "last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                  + ")";

  /**
   * Upserts a player record, updating username and last_seen on duplicate.
   */
  public static final String INSERT_PLAYER =
          "INSERT INTO players (uuid, username, first_seen, last_seen) "
                  + "VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) "
                  + "ON DUPLICATE KEY UPDATE username = VALUES(username), last_seen = CURRENT_TIMESTAMP";

  // -----------------------------------------------------------------------
  // DML - Selects
  // -----------------------------------------------------------------------

  /**
   * Selects the highest applied schema version.
   */
  public static final String SELECT_SCHEMA_VERSION =
          "SELECT MAX(version) FROM schema_version";
  /**
   * Records a schema version as applied.
   */
  public static final String INSERT_SCHEMA_VERSION =
          "INSERT INTO schema_version (version, applied_at) "
                  + "VALUES (?, CURRENT_TIMESTAMP)";

  private SqlStatements() {
    throw new AssertionError("No instances");
  }

}
