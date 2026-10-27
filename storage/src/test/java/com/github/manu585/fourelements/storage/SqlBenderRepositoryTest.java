package com.github.manu585.fourelements.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.storage.bender.SqlBenderRepository;
import com.github.manu585.fourelements.storage.migration.Migrator;
import com.github.manu585.fourelements.storage.sql.Database;
import com.github.manu585.fourelements.storage.sql.DatabaseException;
import com.github.manu585.fourelements.storage.sql.DatabaseSettings;
import com.github.manu585.fourelements.storage.sql.SqlDialect;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Runs the migrations and the repository against a real database of each supported kind.
 *
 * <p>SQLite always runs, it needs nothing. MySQL runs when {@code FE_TEST_MYSQL=1} is set
 * (see {@code dev/docker-compose.yml} for a server). Its tables are dropped first.
 */
class SqlBenderRepositoryTest {

  @TempDir
  static Path tempDir;

  static Stream<SqlDialect> dialects() {
    Stream.Builder<SqlDialect> dialects = Stream.<SqlDialect>builder().add(SqlDialect.SQLITE);
    if (System.getenv("FE_TEST_MYSQL") != null) {
      dialects.add(SqlDialect.MYSQL);
    }
    return dialects.build();
  }

  private static DatabaseSettings settings(SqlDialect dialect) {
    return new DatabaseSettings(dialect, "localhost", 3307, "fourelements", "fourelements", "fourelements", // the port of dev/docker-compose.yml
        tempDir.resolve(UUID.randomUUID() + ".db"), 1, 5000);
  }

  private static void dropEverything(Database database) throws SQLException {
    try (Connection connection = database.getConnection(); Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE IF EXISTS bender_sub_elements");
      statement.execute("DROP TABLE IF EXISTS benders");
      statement.execute("DROP TABLE IF EXISTS schema_version");
    }
  }

  @ParameterizedTest
  @MethodSource("dialects")
  void migrate_thenSaveAndLoad_roundTripsABender(SqlDialect dialect) throws SQLException {
    Database database = Database.connect(settings(dialect));
    try {
      dropEverything(database);
      Migrator migrator = new Migrator(database, Logger.getAnonymousLogger());
      migrator.migrate();
      migrator.migrate(); // already up to date, must not fail

      SqlBenderRepository repository = new SqlBenderRepository(database);
      UUID uuid = UUID.randomUUID();
      assertNull(repository.getBender(uuid).join());

      BenderPlayer saved = new BenderPlayer(uuid, Element.FIRE, true, Element.EARTH, List.of(SubElement.LIGHTNING, SubElement.ICE));
      repository.saveBender(saved).join();

      BenderPlayer loaded = repository.getBender(uuid).join();
      assertNotNull(loaded);
      assertEquals(Optional.of(Element.FIRE), loaded.element());
      assertTrue(loaded.isAvatar());
      assertEquals(Optional.of(Element.EARTH), loaded.activeElement());
      assertEquals(Set.of(SubElement.LIGHTNING, SubElement.ICE), loaded.subElements());

      // Saving again over the existing row replaces it, including the sub-elements
      repository.saveBender(new BenderPlayer(uuid)).join();
      BenderPlayer cleared = repository.getBender(uuid).join();
      assertNotNull(cleared);
      assertFalse(cleared.isBender());
      assertFalse(cleared.isAvatar());
      assertTrue(cleared.subElements().isEmpty());
    } finally {
      database.close();
    }
  }

  @ParameterizedTest
  @MethodSource("dialects")
  void failedSave_failsTheFutureInsteadOfSilentlySucceeding(SqlDialect dialect) throws SQLException {
    Database database = Database.connect(settings(dialect));
    try {
      dropEverything(database); // no schema at all, so every statement fails
      SqlBenderRepository repository = new SqlBenderRepository(database);

      CompletionException failure = assertThrows(CompletionException.class,
          () -> repository.saveBender(new BenderPlayer(UUID.randomUUID(), Element.AIR, false, null, List.of())).join());
      assertInstanceOf(DatabaseException.class, failure.getCause());
    } finally {
      database.close();
    }
  }

}
