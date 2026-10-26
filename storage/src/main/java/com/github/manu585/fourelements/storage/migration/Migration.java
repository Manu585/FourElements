package com.github.manu585.fourelements.storage.migration;

import com.github.manu585.fourelements.storage.sql.SqlDialect;
import java.util.List;
import java.util.function.Function;

/**
 * A single, ordered schema migration: the statements that move the database from
 * {@code version - 1} to {@code version}.
 *
 * <p>The statements are built for the database in use, so one migration serves every
 * {@link SqlDialect}. They run in declaration order, tables must be listed after the tables
 * they reference. Every migration must be safe to run again from its first statement, see
 * {@link Migrator}.
 *
 * @param version the schema version this migration produces (the first migration is {@code 1})
 * @param description what the migration is for, shown in the log
 * @param statements builds the DDL/DML statements for a dialect
 */
public record Migration(int version, String description, Function<SqlDialect, List<String>> statements) {

  public Migration {
    if (version < 1) {
      throw new IllegalArgumentException("Migration version must be >= 1, was " + version);
    }
  }

}
