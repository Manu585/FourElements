package com.github.manu585.fourelements.bukkit.repository;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.bukkit.executor.DatabaseExecutorPool;
import com.github.manu585.fourelements.core.database.SqlStatements;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public class MySqlBenderRepository extends AbstractMySqlRepository implements BenderRepository {

  public MySqlBenderRepository(DatabaseManager databaseManager) {
    super(databaseManager);
  }

  @Override
  public CompletableFuture<Void> savePlayer(Bender bender) {
    return executeAsync("Saving Bender " + bender.uuid(), connection -> {
      try (PreparedStatement ps = connection.prepareStatement(SqlStatements.SAVE_PLAYER.getQuery())) {
        ps.execute();
      }
    });
  }

}
