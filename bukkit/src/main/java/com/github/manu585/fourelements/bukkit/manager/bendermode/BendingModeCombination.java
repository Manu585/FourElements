package com.github.manu585.fourelements.bukkit.manager.bendermode;

import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Input;

public class BendingModeCombination implements Combination {

  private final Map<UUID, Integer> playerInputIndexes = new HashMap<>();

  private final List<Input> inputs;
  private final BenderManager benderManager;

  public BendingModeCombination(List<Input> inputs, BenderManager benderManager) {
    this.inputs = List.copyOf(inputs);
    this.benderManager = benderManager;
  }

  @Override
  public void processInput(UUID uuid, Input input) {
    if (uuid == null) return;

    playerInputIndexes.putIfAbsent(uuid, 0);

    if (inputsMatch(input, nextInput(uuid))) {
      playerInputIndexes.replace(uuid, playerInputIndexes.get(uuid) + 1);

      if (playerInputIndexes.get(uuid) >= combination().size()) {
        onSuccess(uuid);
      }
    } else {
      playerInputIndexes.remove(uuid); // reset
    }
  }

  @Override
  public List<Input> combination() {
    return this.inputs;
  }

  @Override
  public Input nextInput(UUID uuid) {
    return inputs.get(playerInputIndexes.get(uuid));
  }

  @Override
  public void onSuccess(UUID uuid) {
    OnlineBenderPlayer benderPlayer = benderManager.getBender(uuid);
    benderPlayer.setInBendingMode(true);

    playerInputIndexes.remove(uuid);
  }

  private boolean inputsMatch(Input a, Input b) {
    return a.isForward() == b.isForward()
        && a.isBackward() == b.isBackward()
        && a.isLeft() == b.isLeft()
        && a.isRight() == b.isRight()
        && a.isJump() == b.isJump()
        && a.isSneak() == b.isSneak()
        && a.isSprint() == b.isSprint();
  }

}
