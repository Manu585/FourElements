package com.github.manu585.fourelements.bukkit.api;

import com.github.manu585.fourelements.api.FourElementsProvider;
import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import java.util.UUID;

public final class FourElementsProviderImpl implements FourElementsProvider {

  private final BenderManager benderManager;

  public FourElementsProviderImpl(BenderManager benderManager) {
    this.benderManager = benderManager;
  }

  @Override
  public Bender getBender(UUID uuid) {
    return benderManager.getBender(uuid);
  }

}
