package com.github.manu585.fourelements.core.database;

import lombok.Getter;

/**
 * Centralized SQL statement constants for the FourElements database layer.
 * All table and column names match the canonical schema.
 */
public enum SqlStatements {

  CREATE_SCHEMA_VERSION_TABLE("CREATE TABLE IF NOT EXISTS schema_version ("
      + "version INT PRIMARY KEY, "
      + "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
      + ")"
  ),

  CREATE_PLAYERS_TABLE("CREATE TABLE IF NOT EXISTS players ("
      + "uuid BINARY(16) PRIMARY KEY, "
      + "elements VARCHAR(16) NULL"
      + ")"
  ),

  SAVE_PLAYER("INSERT INTO players (uuid, elements) VALUES (?, ?)");

  @Getter
  private final String query;

  SqlStatements(String query) {
    this.query = query;
  }

}
