package com.github.manu585.fourelements.core.database;

/**
 * Centralized SQL statement constants for the IceBoating database layer.
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
  public static final String CREATE_TRACKS_TABLE =
          "CREATE TABLE IF NOT EXISTS tracks ("
                  + "track_id INT AUTO_INCREMENT PRIMARY KEY, "
                  + "name VARCHAR(64) NOT NULL UNIQUE, "
                  + "world VARCHAR(128) NOT NULL, "
                  + "checkpoints SMALLINT NOT NULL DEFAULT 0, "
                  + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                  + ")";
  public static final String CREATE_TIMES_TABLE =
          "CREATE TABLE IF NOT EXISTS times ("
                  + "time_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                  + "player_uuid CHAR(36) NOT NULL, "
                  + "track_id INT NOT NULL, "
                  + "time_ns BIGINT NOT NULL, "
                  + "sector_times_ns TEXT, "
                  + "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                  + "UNIQUE (player_uuid, track_id)"
                  + ")";
  public static final String CREATE_MONTHLY_TIMES_TABLE =
          "CREATE TABLE IF NOT EXISTS monthly_times ("
                  + "monthly_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                  + "player_uuid CHAR(36) NOT NULL, "
                  + "track_id INT NOT NULL, "
                  + "time_ns BIGINT NOT NULL, "
                  + "sector_times_ns TEXT, "
                  + "period CHAR(7) NOT NULL, "
                  + "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                  + "UNIQUE (player_uuid, track_id, period)"
                  + ")";
  public static final String CREATE_RUN_HISTORY_TABLE =
          "CREATE TABLE IF NOT EXISTS run_history ("
                  + "run_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                  + "player_uuid CHAR(36) NOT NULL, "
                  + "track_id INT NOT NULL, "
                  + "time_ns BIGINT NOT NULL, "
                  + "sector_times_ns TEXT, "
                  + "completed BOOLEAN NOT NULL DEFAULT TRUE, "
                  + "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                  + ")";
  /**
   * Upserts a player record, updating username and last_seen on duplicate.
   */
  public static final String INSERT_PLAYER =
          "INSERT INTO players (uuid, username, first_seen, last_seen) "
                  + "VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) "
                  + "ON DUPLICATE KEY UPDATE username = VALUES(username), last_seen = CURRENT_TIMESTAMP";

  // -----------------------------------------------------------------------
  // DML - Inserts / Upserts
  // -----------------------------------------------------------------------
  /**
   * Upserts a best time - replaces if the new time is faster.
   */
  public static final String INSERT_TIME =
          "INSERT INTO times (player_uuid, track_id, time_ns, sector_times_ns, recorded_at) "
                  + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) "
                  + "ON DUPLICATE KEY UPDATE "
                  + "time_ns = IF(VALUES(time_ns) < time_ns, VALUES(time_ns), time_ns), "
                  + "sector_times_ns = IF(VALUES(time_ns) < time_ns, VALUES(sector_times_ns), sector_times_ns), "
                  + "recorded_at = IF(VALUES(time_ns) < time_ns, CURRENT_TIMESTAMP, recorded_at)";
  /**
   * Upserts a monthly best time - replaces if the new time is faster for that period.
   */
  public static final String INSERT_MONTHLY_TIME =
          "INSERT INTO monthly_times (player_uuid, track_id, time_ns, sector_times_ns, period, recorded_at) "
                  + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) "
                  + "ON DUPLICATE KEY UPDATE "
                  + "time_ns = IF(VALUES(time_ns) < time_ns, VALUES(time_ns), time_ns), "
                  + "sector_times_ns = IF(VALUES(time_ns) < time_ns, VALUES(sector_times_ns), sector_times_ns), "
                  + "recorded_at = IF(VALUES(time_ns) < time_ns, CURRENT_TIMESTAMP, recorded_at)";
  /**
   * Inserts a run into the history log.
   */
  public static final String INSERT_RUN_HISTORY =
          "INSERT INTO run_history (player_uuid, track_id, time_ns, sector_times_ns, completed, recorded_at) "
                  + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
  /**
   * Selects a player's personal best on a track.
   */
  public static final String SELECT_PB =
          "SELECT time_id, player_uuid, track_id, time_ns, sector_times_ns, recorded_at "
                  + "FROM times "
                  + "WHERE player_uuid = ? AND track_id = ?";

  // -----------------------------------------------------------------------
  // DML - Selects
  // -----------------------------------------------------------------------
  /**
   * Selects the global best time on a track.
   */
  public static final String SELECT_GLOBAL_BEST =
          "SELECT t.time_id, t.player_uuid, p.username, t.track_id, t.time_ns, t.sector_times_ns, t.recorded_at "
                  + "FROM times t "
                  + "JOIN players p ON t.player_uuid = p.uuid "
                  + "WHERE t.track_id = ? "
                  + "ORDER BY t.time_ns ASC "
                  + "LIMIT 1";
  /**
   * Selects the top N times on a track.
   */
  public static final String SELECT_TOP_TIMES =
          "SELECT t.time_id, t.player_uuid, p.username, t.track_id, t.time_ns, t.sector_times_ns, t.recorded_at "
                  + "FROM times t "
                  + "JOIN players p ON t.player_uuid = p.uuid "
                  + "WHERE t.track_id = ? "
                  + "ORDER BY t.time_ns ASC "
                  + "LIMIT ?";
  /**
   * Selects the top N monthly times on a track for a given period.
   */
  public static final String SELECT_MONTHLY_TOP =
          "SELECT t.monthly_id, t.player_uuid, p.username, t.track_id, t.time_ns, t.sector_times_ns, t.period, t.recorded_at "
                  + "FROM monthly_times t "
                  + "JOIN players p ON t.player_uuid = p.uuid "
                  + "WHERE t.track_id = ? AND t.period = ? "
                  + "ORDER BY t.time_ns ASC "
                  + "LIMIT ?";
  /**
   * Selects a player's run history on a track, most recent first.
   */
  public static final String SELECT_PLAYER_HISTORY =
          "SELECT run_id, player_uuid, track_id, time_ns, sector_times_ns, completed, recorded_at "
                  + "FROM run_history "
                  + "WHERE player_uuid = ? AND track_id = ? "
                  + "ORDER BY recorded_at DESC "
                  + "LIMIT ?";
  /**
   * Counts how many players have a faster time than the given player on a track.
   * The player's rank = count + 1.
   */
  /**
   * Counts how many players have a strictly faster time on a track.
   * Returns a single row with the rank (1-based), or no rows if the player has no time.
   */
  public static final String SELECT_PLAYER_RANK =
          "SELECT (SELECT COUNT(*) + 1 FROM times t2 WHERE t2.track_id = t1.track_id AND t2.time_ns < t1.time_ns) AS rank "
                  + "FROM times t1 WHERE t1.player_uuid = ? AND t1.track_id = ?";

  /**
   * Selects a track by name.
   */
  public static final String SELECT_TRACK_BY_NAME =
          "SELECT track_id, name, world, checkpoints, created_at "
                  + "FROM tracks "
                  + "WHERE name = ?";
  /**
   * Inserts a new track.
   */
  public static final String INSERT_TRACK =
          "INSERT INTO tracks (name, world, checkpoints) "
                  + "VALUES (?, ?, ?)";
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
