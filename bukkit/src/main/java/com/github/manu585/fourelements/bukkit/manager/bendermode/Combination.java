package com.github.manu585.fourelements.bukkit.manager.bendermode;

import java.util.List;
import java.util.UUID;
import org.bukkit.Input;

public interface Combination {

  void processInput(UUID uuid, Input input);

  List<Input> combination();

  Input nextInput(UUID uuid);

  void onSuccess(UUID uuid);

}
