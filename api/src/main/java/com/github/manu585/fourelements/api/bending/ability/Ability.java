package com.github.manu585.fourelements.api.bending.ability;

import com.github.manu585.fourelements.api.bending.metadata.AbilityMetadata;
import java.util.UUID;

public interface Ability {

  UUID abilityId();

  UUID casterId();

  AbilityMetadata metadata();

}
