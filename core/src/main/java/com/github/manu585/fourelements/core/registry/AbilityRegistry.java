package com.github.manu585.fourelements.core.registry;

import com.github.manu585.fourelements.api.bending.ability.definition.AbilityDefinition;
import java.util.Collection;

// Differs from BendersRegistry since we don't want to manipulate this
// on runtime, Benders should be CRUD, this shouldn't, so we rely on Composition instead of inheritance
public final class AbilityRegistry {

  private final MapRegistry<String, AbilityDefinition> store = new MapRegistry<>();

  public void register(AbilityDefinition definition) {
    AbilityDefinition previous = store.put(definition.key(), definition);
    if (previous != null) {
      throw new IllegalStateException("Duplicate ability key: " + definition.key());
    }
  }

  public AbilityDefinition get(String id) {
    return store.get(id);
  }

  public Collection<AbilityDefinition> getAll() {
    return store.values();
  }

}
