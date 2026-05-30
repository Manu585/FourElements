package com.github.manu585.fourelements.core.storage;

import com.github.manu585.fourelements.core.bender.Bender;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BenderStorage implements Cache<UUID, Bender> {

  private final Map<UUID, Bender> benders = new HashMap<>();

  @Override
  public Bender get(UUID key) {
    return benders.get(key);
  }

  @Override
  public Bender remove(UUID key) {
    return benders.containsKey(key) ? benders.remove(key) : null;
  }

  @Override
  public void put(UUID key, Bender value) {
    benders.put(key, value);
  }

  @Override
  public void clear() {
    benders.clear();
  }

}
