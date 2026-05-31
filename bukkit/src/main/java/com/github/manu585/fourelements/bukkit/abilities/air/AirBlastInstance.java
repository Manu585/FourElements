package com.github.manu585.fourelements.bukkit.abilities.air;

import com.github.manu585.fourelements.api.bending.ability.ActiveAbility;
import com.github.manu585.fourelements.api.bending.ability.definition.AbilityDefinition;
import java.util.UUID;

public class AirBlastInstance implements ActiveAbility {

  // unique id each cast / instance
  private final UUID instanceId = UUID.randomUUID();
  private final UUID casterId;
  private final AbilityDefinition definition;

  public AirBlastInstance(AbilityDefinition definition, UUID casterId) {
    this.definition = definition;
    this.casterId = casterId;
  }

  @Override
  public void start() {

  }

  @Override
  public void stop() {

  }

  @Override
  public UUID instanceId() {
    return null;
  }

  @Override
  public UUID casterId() {
    return null;
  }

  @Override
  public AbilityDefinition definition() {
    return definition;
  }

  @Override
  public boolean tick() {
    return false;
  }

}
