package com.github.manu585.fourelements.api.bending.ability;

import com.github.manu585.fourelements.api.bending.ability.definition.AbilityDefinition;
import com.github.manu585.fourelements.api.bending.ability.definition.AbilityMetadata;
import com.github.manu585.fourelements.api.bending.registry.Element;
import java.util.UUID;

public interface Ability {

  UUID instanceId();

  UUID casterId();

  AbilityDefinition definition();

  default Element element() {
    return definition().metadata().element();
  }

}
