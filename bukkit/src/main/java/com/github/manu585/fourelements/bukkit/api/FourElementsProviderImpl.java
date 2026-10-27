package com.github.manu585.fourelements.bukkit.api;

import com.github.manu585.fourelements.api.FourElementsProvider;
import com.github.manu585.fourelements.api.bender.OnlineBender;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public final class FourElementsProviderImpl implements FourElementsProvider {

  private final BenderManager benderManager;

  public FourElementsProviderImpl(BenderManager benderManager) {
    this.benderManager = benderManager;
  }

  @Override
  public Optional<OnlineBender> getOnlineBender(UUID uuid) {
    return benderManager.findOnline(uuid).map(Function.identity());
  }

}
