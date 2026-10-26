package com.github.manu585.fourelements.storage.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.core.bender.SubElementResult;
import com.github.manu585.fourelements.storage.sql.SqlDialect;
import com.github.manu585.fourelements.storage.sql.UuidBytes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Reads and writes benders in the {@code benders} and {@code bender_sub_elements} tables. It
 * works on a connection it is handed, threads and transactions are not its business.
 *
 * <p>The database is outside our control (manual edits, removed enum constants), so reading
 * is lenient: stored values are validated, and bad ones are logged and dropped instead of
 * failing the login. The {@link BenderPlayer} has the final word on what is allowed.
 */
final class BenderTable {

  private static final Logger LOGGER = Logger.getLogger(BenderTable.class.getName());

  private static final String FIND_BENDER = "SELECT element, avatar, channeled_element FROM benders WHERE uuid = ?";
  private static final String FIND_SUB_ELEMENTS = "SELECT sub_element FROM bender_sub_elements WHERE bender_uuid = ?";
  private static final String DELETE_SUB_ELEMENTS = "DELETE FROM bender_sub_elements WHERE bender_uuid = ?";
  private static final String INSERT_SUB_ELEMENT = "INSERT INTO bender_sub_elements (bender_uuid, sub_element) VALUES (?, ?)";

  private final String upsertBender;

  BenderTable(SqlDialect dialect) {
    this.upsertBender = dialect.upsert("benders", "uuid", List.of("element", "avatar", "channeled_element"));
  }

  /**
   * @return the stored bender, or empty if it was never saved
   */
  Optional<BenderPlayer> find(Connection connection, UUID uuid) throws SQLException {
    byte[] key = UuidBytes.toBytes(uuid);

    try (PreparedStatement ps = connection.prepareStatement(FIND_BENDER)) {
      ps.setBytes(1, key);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return Optional.empty();
        }

        Element element = parseElement(uuid, rs.getString("element"));
        boolean avatar = rs.getBoolean("avatar");
        if (avatar && element == null) {
          avatar = false;
          LOGGER.warning("Ignoring Avatar flag stored for bender " + uuid + " without a valid element");
        }
        // A missing, unknown or stale (non-Avatar) channeled element falls back to the home element
        Element channeled = parseElement(uuid, rs.getString("channeled_element"));

        BenderPlayer bender = new BenderPlayer(uuid, element, avatar, channeled, List.of());
        addStoredSubElements(connection, key, bender);
        return Optional.of(bender);
      }
    }
  }

  /**
   * Store the bender, replacing what was stored before including all its sub-elements.
   * Meant to run inside a transaction, with a bender that does not change meanwhile.
   */
  void save(Connection connection, Bender bender) throws SQLException {
    byte[] key = UuidBytes.toBytes(bender.uuid());

    try (PreparedStatement ps = connection.prepareStatement(upsertBender)) {
      ps.setBytes(1, key);
      setNullableString(ps, 2, bender.element().map(Element::name).orElse(null));
      ps.setBoolean(3, bender.isAvatar());
      setNullableString(ps, 4, bender.isAvatar() ? bender.activeElement().map(Element::name).orElse(null) : null);
      ps.executeUpdate();
    }

    // Full replace of the set: simpler and less error-prone than diffing, and the sets are tiny
    try (PreparedStatement delete = connection.prepareStatement(DELETE_SUB_ELEMENTS)) {
      delete.setBytes(1, key);
      delete.executeUpdate();
    }
    try (PreparedStatement insert = connection.prepareStatement(INSERT_SUB_ELEMENT)) {
      for (SubElement subElement : bender.subElements()) {
        insert.setBytes(1, key);
        insert.setString(2, subElement.name());
        insert.addBatch();
      }
      insert.executeBatch();
    }
  }

  private void addStoredSubElements(Connection connection, byte[] key, BenderPlayer bender) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(FIND_SUB_ELEMENTS)) {
      ps.setBytes(1, key);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          addSubElement(bender, rs.getString("sub_element"));
        }
      }
    }
  }

  private static void addSubElement(BenderPlayer bender, String name) {
    SubElement subElement;
    try {
      subElement = SubElement.valueOf(name);
    } catch (IllegalArgumentException e) {
      LOGGER.warning("Ignoring unknown sub-element '" + name + "' stored for bender " + bender.uuid());
      return;
    }

    SubElementResult result = bender.addSubElement(subElement);
    if (result == SubElementResult.NO_ELEMENT || result == SubElementResult.INCOMPATIBLE) {
      LOGGER.warning("Ignoring sub-element '" + name + "' stored for bender " + bender.uuid() + ": invalid for element " + bender.element().orElse(null));
    }
  }

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

  private static void setNullableString(PreparedStatement ps, int index, @Nullable String value) throws SQLException {
    if (value == null) {
      ps.setNull(index, Types.VARCHAR);
    } else {
      ps.setString(index, value);
    }
  }

}
