package com.github.manu585.fourelements.api.bending.ability;

public interface ActiveAbility extends Ability, Tickable {

  void start();

  void stop();

}
