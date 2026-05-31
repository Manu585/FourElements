package com.github.manu585.fourelements.bukkit.abilities;

import com.github.manu585.fourelements.api.bending.ability.definition.AbilityDefinition;
import com.github.manu585.fourelements.api.bending.ability.definition.AbilityMetadata;
import com.github.manu585.fourelements.bukkit.abilities.air.AirBlastInstance;
import com.github.manu585.fourelements.core.registry.AbilityRegistry;
import com.github.manu585.fourelements.api.bending.registry.Element;
import lombok.Getter;

public enum Abilities {

  AIR_BLAST(new AbilityDefinition(
          "air_blast",
          new AbilityMetadata("AirBlast", "A powerful blast of air.", "Manu", Element.AIR),
          AirBlastInstance::new));

  @Getter
  private final AbilityDefinition definition;

  Abilities(AbilityDefinition definition) {
    this.definition = definition;
  }

  public static void registerAll(AbilityRegistry registry) {
    for (Abilities ability : values()) {
      registry.register(ability.getDefinition());
    }
  }

}
