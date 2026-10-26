package com.github.manu585.fourelements.storage.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import com.github.manu585.fourelements.storage.sql.Database;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Stores benders in a SQL database: {@link Database} runs the work on its threads and
 * {@link BenderTable} holds the SQL.
 */
public final class SqlBenderRepository implements BenderRepository {

  private final Database database;
  private final BenderTable table;

  public SqlBenderRepository(Database database) {
    this.database = database;
    this.table = new BenderTable(database.dialect());
  }

  @Override
  public CompletableFuture<BenderPlayer> getBender(UUID uuid) {
    return database.query("load bender " + uuid, connection -> table.find(connection, uuid))
        .thenApply(stored -> stored.orElse(null));
  }

  @Override
  public CompletableFuture<Void> saveBender(Bender bender) {
    // Copied here on the caller's thread, the database thread only ever sees the copy
    BenderPlayer snapshot = BenderPlayer.copyOf(bender);

    return database.transaction("save bender " + snapshot.uuid(), connection -> {
      table.save(connection, snapshot);
      return null;
    });
  }

}
