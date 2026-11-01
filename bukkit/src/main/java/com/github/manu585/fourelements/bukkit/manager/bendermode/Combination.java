package com.github.manu585.fourelements.bukkit.manager.bendermode;

import java.util.UUID;
import org.bukkit.Input;

public interface Combination {

  void processInput(UUID uuid, Input input);

  void onSuccess(UUID uuid);

}
