package com.github.manu585.fourelements.api.bending.registry;

import com.github.manu585.fourelements.api.bending.metadata.AbilityMetadata;
import com.github.manu585.fourelements.api.bending.metadata.air.AirBlast;

public enum Abilities {

  AIR_BLAST(new AirBlast());

  private final AbilityMetadata metadata;

  Abilities(AbilityMetadata metadata) {
    this.metadata = metadata;
  }

  public AbilityMetadata metadata() {
    return metadata;
  }

}
