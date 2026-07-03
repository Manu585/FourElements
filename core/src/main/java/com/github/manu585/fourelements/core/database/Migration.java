package com.github.manu585.fourelements.core.database;

import java.util.List;

/**
 * A single, ordered schema migration.
 *
 * <p>A migration bundles all SQL statements that move the database from
 * {@code version - 1} to {@code version}. Statements are executed in the order
 * they are declared, so tables must be listed after the tables they reference
 * via foreign keys.
 *
 * @param version    the schema version this migration produces (the first migration is {@code 1})
 * @param statements the DDL/DML statements to run, in execution order
 */
public record Migration(int version, List<String> statements) {

  public Migration {
    if (version < 1) {
      throw new IllegalArgumentException("Migration version must be >= 1, was " + version);
    }
    statements = List.copyOf(statements);
  }

}
