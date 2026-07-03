package com.github.manu585.fourelements.bukkit.repository;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.core.database.DbUtils;
import com.github.manu585.fourelements.core.database.SqlQueries;
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
      try (PreparedStatement ps = connection.prepareStatement(SqlQueries.SAVE_BENDER.getQuery())) {
        ps.setBytes(1, DbUtils.toBytes(bender.uuid()));
        ps.executeUpdate();
      }
    });
  }



}
