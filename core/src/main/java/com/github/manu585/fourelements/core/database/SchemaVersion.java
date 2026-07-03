package com.github.manu585.fourelements.core.database;

import java.util.List;

/**
 * Ordered list of schema migrations for the FourElements database.
 *
 * <p>The position in {@link #migrations()} defines the schema version: the first
 * entry is version {@code 1}, the second is version {@code 2}, and so on. The
 * SchemaMigrator-equivalent on the platform side compares these versions
 * against the {@code schema_version} bookkeeping table and applies only the ones
 * that are still missing.
 *
 * <p><strong>Never reorder or edit an already-released migration.</strong> Once a
 * migration has run on a real database its version is recorded, and it will not run
 * again, changing it would leave existing installs inconsistent. Schema changes are
 * added as new migrations appended to the end of the list.
 */
public final class SchemaVersion {

  private SchemaVersion() {
    throw new AssertionError("No instances");
  }

  /**
   * Returns every known migration in ascending version order.
   *
   * @return the immutable, ordered list of migrations
   */
  public static List<Migration> migrations() {
    return List.of(
        // Version 1: initial schema.
        // Order matters!
        new Migration(1, List.of(
            SqlQueries.CREATE_ELEMENTS_TABLE.getQuery(),
            SqlQueries.CREATE_BENDERS_TABLE.getQuery(),
            SqlQueries.CREATE_SUB_ELEMENTS_TABLE.getQuery(),       // references elements
            SqlQueries.CREATE_BENDER_ELEMENTS_TABLE.getQuery(),    // references benders, elements
            SqlQueries.CREATE_BENDER_SUB_ELEMENTS_TABLE.getQuery() // references benders, sub_elements
        ))
    );
  }

  /**
   * @return the highest schema version this build knows about
   */
  public static int latestVersion() {
    return migrations().size();
  }

}
