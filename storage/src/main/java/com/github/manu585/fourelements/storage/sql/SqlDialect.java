package com.github.manu585.fourelements.storage.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * The two SQL flavors the plugin can store its data in. Everything not listed here is plain
 * SQL that both understand.
 */
public enum SqlDialect {

  /** MySQL & MariaDB */
  MYSQL("com.mysql.cj.jdbc.Driver", "BINARY(16)") {

    @Override
    public String upsert(String table, String key, List<String> values) {
      return insert(table, key, values) + " ON DUPLICATE KEY UPDATE " + String.join(", ", values.stream().map(column -> column + " = VALUES(" + column + ")").toList());
    }

    @Override
    public boolean isDuplicateColumn(SQLException e) {
      return e.getErrorCode() == 1060; // ER_DUP_FIELDNAME
    }

  },

  /** SQLite */
  SQLITE("org.sqlite.JDBC", "BLOB") {

    @Override
    public String upsert(String table, String key, List<String> values) {
      return insert(table, key, values) + " ON CONFLICT (" + key + ") DO UPDATE SET " + String.join(", ", values.stream().map(column -> column + " = EXCLUDED." + column).toList());
    }

    @Override
    public boolean isDuplicateColumn(SQLException e) {
      String message = e.getMessage();
      return message != null && message.toLowerCase(Locale.ROOT).contains("duplicate column name");
    }

  };

  private final String driverClass;
  private final String uuidType;

  SqlDialect(String driverClass, String uuidType) {
    this.driverClass = driverClass;
    this.uuidType = uuidType;
  }

  /**
   * Look a dialect up by the name used in the config.
   *
   * @param name {@code mysql}, {@code mariadb} or {@code sqlite}
   * @return the dialect
   * @throws IllegalArgumentException if the name is unknown
   */
  public static SqlDialect fromName(String name) {
    return switch (name.strip().toLowerCase(Locale.ROOT)) {
      case "mysql", "mariadb" -> MYSQL;
      case "sqlite" -> SQLITE;
      default -> throw new IllegalArgumentException("Unknown database type '" + name + "', use sqlite, mysql or mariadb");
    };
  }

  /**
   * @return class name of the JDBC driver
   */
  public String driverClass() {
    return driverClass;
  }

  /**
   * @return the column type that holds a UUID as its 16 raw bytes
   */
  public String uuidType() {
    return uuidType;
  }

  /**
   * Build a statement that inserts a row, or updates it if one with the same key exists.
   * The parameters are the key first, then the values in the order given.
   *
   * @param table table to write to
   * @param key the primary key column
   * @param values the columns to overwrite when the row exists already
   * @return the SQL with one {@code ?} per column
   */
  public abstract String upsert(String table, String key, List<String> values);

  /**
   * Check if an error means that the column to add exists already, e.g. left behind by an
   * earlier migration run that was interrupted.
   *
   * @param e the error a statement failed with
   * @return <code>true</code> for "duplicate column"
   */
  public abstract boolean isDuplicateColumn(SQLException e);

  /** {@code INSERT INTO table (key, a, b) VALUES (?, ?, ?)} */
  private static String insert(String table, String key, List<String> values) {
    return "INSERT INTO " + table + " (" + key + ", " + String.join(", ", values) + ") VALUES (?" + ", ?".repeat(values.size()) + ")";
  }

}
