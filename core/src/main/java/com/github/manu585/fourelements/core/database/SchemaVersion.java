package com.github.manu585.fourelements.core.database;

/**
 * Schema versioning constants and migration definitions for the FourElements database.
 */
public final class SchemaVersion {

  /**
   * The current schema version. Increment when adding new migrations.
   */
  public static final int CURRENT_VERSION = 1;

  private SchemaVersion() {
    throw new AssertionError("No instances");
  }

  /**
   * Returns an array of migration SQL strings where index 0 corresponds to
   * migration version 1. Each migration string may contain multiple statements
   * separated by semicolons.
   *
   * @return the array of migration SQL strings
   */
  public static String[] getMigrations() {
    return new String[]{
            // Version 1
            SqlStatements.CREATE_SCHEMA_VERSION_TABLE + ";"
                    + SqlStatements.CREATE_PLAYERS_TABLE + ";"
                    + SqlStatements.CREATE_TRACKS_TABLE + ";"
                    + SqlStatements.CREATE_TIMES_TABLE + ";"
                    + SqlStatements.CREATE_MONTHLY_TIMES_TABLE + ";"
                    + SqlStatements.CREATE_RUN_HISTORY_TABLE
    };
  }

}
