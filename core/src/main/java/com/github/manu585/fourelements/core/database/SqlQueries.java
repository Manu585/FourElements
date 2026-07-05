package com.github.manu585.fourelements.core.database;

import lombok.Getter;

/**
 * Centralized SQL statement constants for the FourElements database layer.
 * All table and column names match the canonical schema.
 */
public enum SqlQueries {

  CREATE_SCHEMA_VERSION_TABLE("CREATE TABLE IF NOT EXISTS schema_version ("
      + "version INT PRIMARY KEY, "
      + "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
      + ")"),

  SELECT_SCHEMA_VERSION("SELECT MAX(version) AS version FROM schema_version"),

  INSERT_SCHEMA_VERSION("INSERT INTO schema_version (version) VALUES (?)"),


  // ---------------------------------------------------------------------------
  // Schema DDL. Elements are stored by their Element enum name: the enum in code
  // is the single source of truth for which elements (and sub-elements) exist and
  // how they relate, so no separate reference/join tables are needed.
  // ---------------------------------------------------------------------------

  CREATE_BENDERS_TABLE("CREATE TABLE IF NOT EXISTS benders ("
      + "uuid BINARY(16) PRIMARY KEY"
      + ")"),

  CREATE_BENDER_ELEMENTS_TABLE("CREATE TABLE IF NOT EXISTS bender_elements ("
      + "bender_uuid BINARY(16) NOT NULL, "
      + "element VARCHAR(32) NOT NULL, "
      + "PRIMARY KEY (bender_uuid, element), "
      + "FOREIGN KEY (bender_uuid) REFERENCES benders(uuid) ON DELETE CASCADE"
      + ")"),


  // ---------------------------------------------------------------------------
  // Runtime queries.
  // ---------------------------------------------------------------------------

  /** Existence check for a bender row. */
  GET_BENDER("SELECT 1 FROM benders WHERE uuid = ?"),

  /** All element enum names a bender possesses. */
  GET_BENDER_ELEMENTS("SELECT element FROM bender_elements WHERE bender_uuid = ?"),

  /** Insert the bender row if it does not already exist. */
  SAVE_BENDER("INSERT IGNORE INTO benders (uuid) VALUES (?)"),

  /** Clear a bender's element set before rewriting it (full-replace upsert). */
  DELETE_BENDER_ELEMENTS("DELETE FROM bender_elements WHERE bender_uuid = ?"),

  /** Add a single element to a bender. */
  INSERT_BENDER_ELEMENT("INSERT IGNORE INTO bender_elements (bender_uuid, element) VALUES (?, ?)");

  @Getter
  private final String query;

  SqlQueries(String query) {
    this.query = query;
  }

}
