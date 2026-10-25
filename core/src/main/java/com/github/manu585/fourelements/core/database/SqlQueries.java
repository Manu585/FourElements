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
  // Schema DDL. Elements are stored by their enum name: the enums in code are the
  // single source of truth for which elements (and sub-elements) exist and how they
  // relate, so no separate reference tables are needed. A bender has at most one main
  // element (a nullable column), and any number of sub-elements (a child table).
  // ---------------------------------------------------------------------------

  CREATE_BENDERS_TABLE("CREATE TABLE IF NOT EXISTS benders ("
      + "uuid BINARY(16) PRIMARY KEY, "
      + "element VARCHAR(32) NULL"
      + ")"),

  CREATE_BENDER_SUB_ELEMENTS_TABLE("CREATE TABLE IF NOT EXISTS bender_sub_elements ("
      + "bender_uuid BINARY(16) NOT NULL, "
      + "sub_element VARCHAR(32) NOT NULL, "
      + "PRIMARY KEY (bender_uuid, sub_element), "
      + "FOREIGN KEY (bender_uuid) REFERENCES benders(uuid) ON DELETE CASCADE"
      + ")"),


  // ---------------------------------------------------------------------------
  // The Avatar is a flag on top of the home element instead of an element
  // value, and chi is no longer a bender type. Existing Avatars get Air as home element
  // (their old row never recorded one), chi benders become non-benders.
  // ---------------------------------------------------------------------------

  ADD_BENDERS_AVATAR_COLUMN("ALTER TABLE benders ADD COLUMN avatar BOOLEAN NOT NULL DEFAULT FALSE"),

  MIGRATE_AVATAR_ROWS("UPDATE benders SET avatar = TRUE, element = 'AIR' WHERE element = 'AVATAR'"),

  MIGRATE_CHI_ROWS("UPDATE benders SET element = NULL WHERE element = 'CHI'"),


  // ---------------------------------------------------------------------------
  // Version 3: the element the Avatar is channeling now survives a relog. It is only
  // meaningful while the avatar flag is set; the loader ignores it otherwise.
  // ---------------------------------------------------------------------------

  ADD_BENDERS_CHANNELED_ELEMENT_COLUMN("ALTER TABLE benders ADD COLUMN channeled_element VARCHAR(32) NULL"),


  // ---------------------------------------------------------------------------
  // Runtime queries.
  // ---------------------------------------------------------------------------

  /**
   * A bender's row: its element enum name (NULL for a non-bender), whether it is the Avatar and the element the Avatar channels.
   */
  GET_BENDER("SELECT element, avatar, channeled_element FROM benders WHERE uuid = ?"),

  /**
   * All sub-element enum names a bender possesses.
   */
  GET_BENDER_SUB_ELEMENTS("SELECT sub_element FROM bender_sub_elements WHERE bender_uuid = ?"),

  /**
   * Insert the bender row, or update its element and Avatar flag if it already exists.
   */
  SAVE_BENDER("INSERT INTO benders (uuid, element, avatar, channeled_element) VALUES (?, ?, ?, ?) "
      + "ON DUPLICATE KEY UPDATE element = VALUES(element), avatar = VALUES(avatar), "
      + "channeled_element = VALUES(channeled_element)"),

  /**
   * Clear a bender's sub-elements before rewriting them (full-replace upsert).
   */
  DELETE_BENDER_SUB_ELEMENTS("DELETE FROM bender_sub_elements WHERE bender_uuid = ?"),

  /**
   * Add a single sub-element to a bender.
   */
  INSERT_BENDER_SUB_ELEMENT("INSERT IGNORE INTO bender_sub_elements (bender_uuid, sub_element) VALUES (?, ?)");

  @Getter
  private final String query;

  SqlQueries(String query) {
    this.query = query;
  }

}
