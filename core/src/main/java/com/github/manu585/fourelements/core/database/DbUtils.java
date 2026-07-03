package com.github.manu585.fourelements.core.database;

import java.nio.ByteBuffer;
import java.util.UUID;
import lombok.NonNull;

public final class DbUtils {

  private DbUtils() {
    throw new AssertionError("No instances");
  }

  /**
   * Encodes a UUID into the 16-byte form stored in the {@code BINARY(16)} column.
   */
  public static byte @NonNull [] toBytes(@NonNull UUID uuid) {
    return ByteBuffer.allocate(16)
        .putLong(uuid.getMostSignificantBits())
        .putLong(uuid.getLeastSignificantBits())
        .array();
  }

  public static @NonNull UUID readUuid(@NonNull ByteBuffer buffer) {
    return new UUID(buffer.getLong(), buffer.getLong());
  }

}
