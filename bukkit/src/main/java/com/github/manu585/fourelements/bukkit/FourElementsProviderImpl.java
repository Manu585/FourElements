package com.github.manu585.fourelements.bukkit;

import com.github.manu585.fourelements.api.FourElementsProvider;
import java.util.UUID;

public class FourElementsProviderImpl implements FourElementsProvider {

  public FourElementsProviderImpl() {}

  @Override
  public boolean isRacing(UUID playerUuid) {
    return true;
  }

}
