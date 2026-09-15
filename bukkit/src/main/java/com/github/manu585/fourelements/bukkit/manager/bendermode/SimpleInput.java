package com.github.manu585.fourelements.bukkit.manager.bendermode;

import org.bukkit.Input;

public record SimpleInput(
    boolean isForward,
    boolean isBackward,
    boolean isLeft,
    boolean isRight,
    boolean isJump,
    boolean isSneak,
    boolean isSprint
) implements Input {

  public static SimpleInput of(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
    return new SimpleInput(forward, backward, left, right, jump, sneak, sprint);
  }

}