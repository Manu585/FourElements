package com.github.manu585.fourelements.api.bending.ability.definition;

import com.github.manu585.fourelements.api.bending.ability.ActiveAbility;
import java.util.UUID;
import java.util.function.BiFunction;

// Factory TODO: Could be cleaner, mayheps cleanup in future.
public record AbilityDefinition(String key, AbilityMetadata metadata, BiFunction<AbilityDefinition, UUID, ActiveAbility>factory) {

  public ActiveAbility create(UUID casterUuid) {
    return factory.apply(this, casterUuid);
  }

}
