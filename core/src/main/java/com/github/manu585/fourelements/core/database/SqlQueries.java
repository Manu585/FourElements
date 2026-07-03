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

  CREATE_BENDERS_TABLE("CREATE TABLE IF NOT EXISTS benders ("
      + "uuid BINARY(16) PRIMARY KEY"
      + ")"),

  CREATE_ELEMENTS_TABLE("CREATE TABLE IF NOT EXISTS elements ("
      + "id SERIAL PRIMARY KEY, "
      + "identifier VARCHAR(16) NOT NULL UNIQUE"
      + ")"),

  CREATE_SUB_ELEMENTS_TABLE("CREATE TABLE IF NOT EXISTS sub_elements ("
      + "id SERIAL PRIMARY KEY, "
      + "element_id BIGINT UNSIGNED NOT NULL, "
      + "identifier VARCHAR(16) NOT NULL UNIQUE, "
      + "FOREIGN KEY (element_id) REFERENCES elements(id) ON DELETE CASCADE"
      + ")"),

  CREATE_BENDER_ELEMENTS_TABLE("CREATE TABLE IF NOT EXISTS bender_elements ("
      + "bender_uuid BINARY(16) NOT NULL, "
      + "element_id BIGINT UNSIGNED NOT NULL, "
      + "PRIMARY KEY (bender_uuid, element_id), "
      + "FOREIGN KEY (bender_uuid) REFERENCES benders(uuid) ON DELETE CASCADE, "
      + "FOREIGN KEY (element_id) REFERENCES elements(id) ON DELETE CASCADE"
      + ")"),

  CREATE_BENDER_SUB_ELEMENTS_TABLE("CREATE TABLE IF NOT EXISTS bender_sub_elements ("
      + "bender_uuid BINARY(16) NOT NULL, "
      + "sub_element_id BIGINT UNSIGNED NOT NULL, "
      + "PRIMARY KEY (bender_uuid, sub_element_id), "
      + "FOREIGN KEY (bender_uuid) REFERENCES benders(uuid) ON DELETE CASCADE, "
      + "FOREIGN KEY (sub_element_id) REFERENCES sub_elements(id) ON DELETE CASCADE"
      + ")"),

  /**
   * Inserts a bender, or refreshes the username if the UUID already exists.
   * Elements are stored separately in the {@code bender_elements} join table.
   */
  SAVE_BENDER("INSERT INTO benders (uuid) VALUES (?)");

  @Getter
  private final String query;

  SqlQueries(String query) {
    this.query = query;
  }

}
