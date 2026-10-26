package com.github.manu585.fourelements.storage.sql;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Converts UUIDs to and from the 16 raw bytes stored in a {@link SqlDialect#uuidType() UUID column}.
 */
public final class UuidBytes {

  private UuidBytes() {
    throw new AssertionError("No instances");
  }

  public static byte[] toBytes(UUID uuid) {
    return ByteBuffer.allocate(16)
        .putLong(uuid.getMostSignificantBits())
        .putLong(uuid.getLeastSignificantBits())
        .array();
  }

  public static UUID fromBytes(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    return new UUID(buffer.getLong(), buffer.getLong());
  }

}
