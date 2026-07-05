package com.github.manu585.fourelements.bukkit.repository;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.core.database.DbUtils;
import com.github.manu585.fourelements.core.database.SqlQueries;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MySqlBenderRepository extends AbstractMySqlRepository implements BenderRepository {

  public MySqlBenderRepository(DatabaseManager databaseManager) {
    super(databaseManager);
  }

  @Override
  public CompletableFuture<BenderPlayer> getBender(UUID uuid) {
    return CompletableFuture.supplyAsync(() -> {
      try (Connection connection = databaseManager.getConnection()) {
        byte[] uuidBytes = DbUtils.toBytes(uuid);

        if (!benderExists(connection, uuidBytes)) {
          return null;
        }

        return new BenderPlayer(uuid, fetchElements(connection, uuidBytes));
      } catch (SQLException e) {
        throw new CompletionException("Failed to load bender " + uuid, e);
      }
    }, databaseManager.getExecutor());
  }

  @Override
  public CompletableFuture<Void> saveBender(Bender bender) {
    return executeAsync("Saving Bender " + bender.uuid(), connection -> {
      byte[] uuidBytes = DbUtils.toBytes(bender.uuid());

      boolean previousAutoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
      try {
        upsertBenderRow(connection, uuidBytes);
        replaceElements(connection, uuidBytes, bender.elements());
        connection.commit();
      } catch (SQLException e) {
        connection.rollback();
        throw e;
      } finally {
        connection.setAutoCommit(previousAutoCommit);
      }
    });
  }

  private boolean benderExists(Connection connection, byte[] uuidBytes) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(SqlQueries.GET_BENDER.getQuery())) {
      ps.setBytes(1, uuidBytes);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  private List<Element> fetchElements(Connection connection, byte[] uuidBytes) throws SQLException {
    List<Element> elements = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(SqlQueries.GET_BENDER_ELEMENTS.getQuery())) {
      ps.setBytes(1, uuidBytes);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          elements.add(Element.valueOf(rs.getString("element")));
        }
      }
    }
    return elements;
  }

  private void upsertBenderRow(Connection connection, byte[] uuidBytes) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(SqlQueries.SAVE_BENDER.getQuery())) {
      ps.setBytes(1, uuidBytes);
      ps.executeUpdate();
    }
  }

  /**
   * Full-replace of a bender's element set: clear the existing rows, then insert the
   * current ones. Simpler and less error-prone than diffing, and the sets are tiny.
   */
  private void replaceElements(Connection connection, byte[] uuidBytes, List<Element> elements) throws SQLException {
    try (PreparedStatement delete = connection.prepareStatement(SqlQueries.DELETE_BENDER_ELEMENTS.getQuery())) {
      delete.setBytes(1, uuidBytes);
      delete.executeUpdate();
    }

    if (elements.isEmpty()) {
      return;
    }

    try (PreparedStatement insert = connection.prepareStatement(SqlQueries.INSERT_BENDER_ELEMENT.getQuery())) {
      for (Element element : elements) {
        insert.setBytes(1, uuidBytes);
        insert.setString(2, element.name());
        insert.addBatch();
      }
      insert.executeBatch();
    }
  }

}
