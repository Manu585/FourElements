package com.github.manu585.fourelements.core.registry;

import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import java.util.UUID;

// Runtime bender
public class OnlineBenderRegistry extends MapRegistry<UUID, OnlineBenderPlayer> {

  public OnlineBenderPlayer getOrThrow(UUID uuid) {
    OnlineBenderPlayer onlineBenderPlayer = get(uuid);
    if (onlineBenderPlayer == null) {
      throw new IllegalArgumentException("Player with UUID " + uuid + " was not found.");
    }
    return onlineBenderPlayer;
  }

}
