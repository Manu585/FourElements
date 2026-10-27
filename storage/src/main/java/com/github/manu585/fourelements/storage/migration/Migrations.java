package com.github.manu585.fourelements.storage.migration;

import java.util.List;

/**
 * Every schema migration, oldest first. The position in {@link #all()} is the schema version:
 * the first entry is version {@code 1}, the second {@code 2}, and so on.
 *
 * <p><strong>Never reorder or edit an already-released migration.</strong> Once a migration
 * has run on a real database its version is recorded, and it will not run again, changing it
 * would leave existing installs inconsistent. A schema change is a new migration appended to
 * the end of the list.
 */
public final class Migrations {

  private Migrations() {
    throw new AssertionError("No instances");
  }

  /**
   * @return the immutable, ordered list of migrations
   */
  public static List<Migration> all() {
    return List.of(
        // Order matters inside a migration: bender_sub_elements references benders
        new Migration(1, "Create the bender tables", dialect -> List.of(
            "CREATE TABLE IF NOT EXISTS benders ("
                + "uuid " + dialect.uuidType() + " PRIMARY KEY, "
                + "element VARCHAR(32) NULL"
                + ")",
            "CREATE TABLE IF NOT EXISTS bender_sub_elements ("
                + "bender_uuid " + dialect.uuidType() + " NOT NULL, "
                + "sub_element VARCHAR(32) NOT NULL, "
                + "PRIMARY KEY (bender_uuid, sub_element), "
                + "FOREIGN KEY (bender_uuid) REFERENCES benders(uuid) ON DELETE CASCADE"
                + ")"
        )),

        // The Avatar is a flag on top of the home element instead of an element value, and
        // chi is no longer a bender type. chi benders become non-benders.
        new Migration(2, "Make the Avatar a flag, drop chi", _ -> List.of(
            "ALTER TABLE benders ADD COLUMN avatar BOOLEAN NOT NULL DEFAULT FALSE",
            "UPDATE benders SET element = NULL WHERE element = 'CHI'"
        )),

        // Only meaningful while the avatar flag is set; the loader ignores it otherwise.
        new Migration(3, "Remember the element the Avatar channels", _ -> List.of(
            "ALTER TABLE benders ADD COLUMN channeled_element VARCHAR(32) NULL"
        ))
    );
  }

}
