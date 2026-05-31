package com.github.manu585.fourelements.bukkit.abilities.air;

import com.github.manu585.fourelements.api.bending.ability.ActiveAbility;
import com.github.manu585.fourelements.api.bending.metadata.AbilityMetadata;
import java.util.UUID;

public class AirBlastInstance implements ActiveAbility {

  // unique id each cast / instance
  private final UUID instanceId = UUID.randomUUID();
  private final AbilityMetadata metadata;
  private final UUID casterId;

  public AirBlastInstance(AbilityMetadata metadata, UUID casterId) {
    this.metadata = metadata;
    this.casterId = casterId;
  }

  @Override
  public void start() {

  }

  @Override
  public void stop() {

  }

  @Override
  public UUID abilityId() {
    return instanceId;
  }

  @Override
  public UUID casterId() {
    return casterId;
  }

  @Override
  public AbilityMetadata metadata() {
    return metadata;
  }

  @Override
  public boolean tick() {
    return false;
  }

}
