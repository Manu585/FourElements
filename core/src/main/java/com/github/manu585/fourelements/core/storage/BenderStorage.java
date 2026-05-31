package com.github.manu585.fourelements.core.storage;

import com.github.manu585.fourelements.core.bender.Bender;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BenderStorage implements Cache<UUID, Bender> {

  private final Map<UUID, Bender> bendersMap = new HashMap<>();

  @Override
  public Bender get(UUID key) {
    return bendersMap.get(key);
  }

  @Override
  public Bender remove(UUID key) {
    return bendersMap.containsKey(key) ? bendersMap.remove(key) : null;
  }

  @Override
  public void put(UUID key, Bender value) {
    bendersMap.put(key, value);
  }

  @Override
  public void clear() {
    bendersMap.clear();
  }

}
