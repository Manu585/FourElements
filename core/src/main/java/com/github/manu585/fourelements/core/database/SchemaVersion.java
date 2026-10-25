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
        // Order matters! bender_sub_elements references benders.
        new Migration(1, List.of(
            SqlQueries.CREATE_BENDERS_TABLE.getQuery(),
            SqlQueries.CREATE_BENDER_SUB_ELEMENTS_TABLE.getQuery()
        )),
        // Version 2: Avatar becomes a flag, chi is dropped as a bender type.
        new Migration(2, List.of(
            SqlQueries.ADD_BENDERS_AVATAR_COLUMN.getQuery(),
            SqlQueries.MIGRATE_AVATAR_ROWS.getQuery(),
            SqlQueries.MIGRATE_CHI_ROWS.getQuery()
        )),
        // Version 3: remember the element the Avatar is channeling.
        new Migration(3, List.of(
            SqlQueries.ADD_BENDERS_CHANNELED_ELEMENT_COLUMN.getQuery()
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
