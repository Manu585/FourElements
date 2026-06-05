package com.github.manu585.fourelements.core.registry;

import com.github.manu585.fourelements.api.bender.Bender;
import java.util.UUID;

// Persistent bender
public final class BenderRegistry extends MapRegistry<UUID, Bender> {

  public Bender getOrThrow(UUID uuid) {
    Bender bender = get(uuid);
    if (bender == null) {
      throw new IllegalStateException("Bender " + uuid + " not found");
    }
    return bender;
  }

}
