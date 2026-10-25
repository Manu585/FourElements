package com.github.manu585.fourelements.bukkit.repository;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.core.bender.SubElementResult;
import com.github.manu585.fourelements.core.database.DbUtils;
import com.github.manu585.fourelements.core.database.SqlQueries;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import org.jspecify.annotations.Nullable;

public class MySqlBenderRepository extends AbstractMySqlRepository implements BenderRepository {

  private static final Logger LOGGER = Logger.getLogger(MySqlBenderRepository.class.getName());

  public MySqlBenderRepository(DatabaseManager databaseManager) {
    super(databaseManager);
  }

  @Override
  public CompletableFuture<BenderPlayer> getBender(UUID uuid) {
    return CompletableFuture.supplyAsync(() -> {
      try (Connection connection = databaseManager.getConnection()) {
        byte[] uuidBytes = DbUtils.toBytes(uuid);

        BenderRow row = fetchRow(connection, uuidBytes);
        if (row == null) {
          return null;
        }

        Element element = parseElement(uuid, row.elementName());
        boolean avatar = row.avatar() && element != null;
        if (row.avatar() && !avatar) {
          LOGGER.warning("Ignoring Avatar flag stored for bender " + uuid + " without a valid element");
        }

        // A missing, unknown or stale (non-Avatar) channeled element falls back to the home element
        Element channeled = parseElement(uuid, row.channeledName());

        BenderPlayer bender = new BenderPlayer(uuid, element, avatar, channeled, List.of());
        addStoredSubElements(bender, fetchSubElementNames(connection, uuidBytes));
        return bender;
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
        upsertBenderRow(connection, uuidBytes, bender.element().orElse(null), bender.isAvatar(),
            bender.isAvatar() ? bender.activeElement().orElse(null) : null);
        replaceSubElements(connection, uuidBytes, bender.subElements());
        connection.commit();
      } catch (SQLException e) {
        connection.rollback();
        throw e;
      } finally {
        connection.setAutoCommit(previousAutoCommit);
      }
    });
  }

  // ---- Loading ----

  /**
   * A stored bender row; {@code elementName} is null for a bender without an element
   */
  private record BenderRow(@Nullable String elementName, boolean avatar, @Nullable String channeledName) {}

  /** @return the stored row, or {@code null} if this bender was never saved */
  private @Nullable BenderRow fetchRow(Connection connection, byte[] uuidBytes) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(SqlQueries.GET_BENDER.getQuery())) {
      ps.setBytes(1, uuidBytes);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? new BenderRow(rs.getString("element"), rs.getBoolean("avatar"), rs.getString("channeled_element")) : null;
      }
    }
  }

  private List<String> fetchSubElementNames(Connection connection, byte[] uuidBytes) throws SQLException {
    List<String> names = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(SqlQueries.GET_BENDER_SUB_ELEMENTS.getQuery())) {
      ps.setBytes(1, uuidBytes);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          names.add(rs.getString("sub_element"));
        }
      }
    }
    return names;
  }

  /**
   * The database is outside our control (manual edits, removed enum constants), so stored
   * values are validated here and bad ones are logged and dropped instead of failing the login.
   */
  private static @Nullable Element parseElement(UUID uuid, @Nullable String name) {
    if (name == null) {
      return null;
    }
    try {
      return Element.valueOf(name);
    } catch (IllegalArgumentException e) {
      LOGGER.warning("Ignoring unknown element '" + name + "' stored for bender " + uuid);
      return null;
    }
  }

  /** The bender decides what it may possess; anything it refuses is logged and dropped. */
  private static void addStoredSubElements(BenderPlayer bender, List<String> names) {
    for (String name : names) {
      try {
        SubElement subElement = SubElement.valueOf(name);
        SubElementResult result = bender.addSubElement(subElement);
        if (result == SubElementResult.NO_ELEMENT || result == SubElementResult.INCOMPATIBLE) {
          LOGGER.warning("Ignoring sub-element '" + name + "' stored for bender " + bender.uuid() + ": invalid for element " + bender.element().orElse(null));
        }
      } catch (IllegalArgumentException e) {
        LOGGER.warning("Ignoring unknown sub-element '" + name + "' stored for bender " + bender.uuid());
      }
    }
  }

  // ---- Saving ----

  private void upsertBenderRow(Connection connection, byte[] uuidBytes, @Nullable Element element, boolean avatar, @Nullable Element channeled) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(SqlQueries.SAVE_BENDER.getQuery())) {
      ps.setBytes(1, uuidBytes);
      if (element == null) {
        ps.setNull(2, Types.VARCHAR);
      } else {
        ps.setString(2, element.name());
      }
      ps.setBoolean(3, avatar);
      if (channeled == null) {
        ps.setNull(4, Types.VARCHAR);
      } else {
        ps.setString(4, channeled.name());
      }
      ps.executeUpdate();
    }
  }

  /**
   * Full-replace of a bender's sub-element set: clear the existing rows, then insert the
   * current ones. Simpler and less error-prone than diffing, and the sets are tiny.
   */
  private void replaceSubElements(Connection connection, byte[] uuidBytes, Set<SubElement> subElements) throws SQLException {
    try (PreparedStatement delete = connection.prepareStatement(SqlQueries.DELETE_BENDER_SUB_ELEMENTS.getQuery())) {
      delete.setBytes(1, uuidBytes);
      delete.executeUpdate();
    }

    if (subElements.isEmpty()) {
      return;
    }

    try (PreparedStatement insert = connection.prepareStatement(SqlQueries.INSERT_BENDER_SUB_ELEMENT.getQuery())) {
      for (SubElement subElement : subElements) {
        insert.setBytes(1, uuidBytes);
        insert.setString(2, subElement.name());
        insert.addBatch();
      }
      insert.executeBatch();
    }
  }

}
