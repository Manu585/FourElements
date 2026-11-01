package com.github.manu585.fourelements.bukkit.manager.bendermode;

import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.bender.mode.BendingMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Input;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Switches a bender between {@link BendingMode#PASSIVE} and {@link BendingMode#COMBAT} when they
 * press the configured sequence of inputs, every step within {@link #STEP_WINDOW} of the one before.
 *
 * <p>Main thread only.
 */
public class BendingModeCombination implements Combination {

  private static final long STEP_WINDOW_NANOS = Duration.ofMillis(600).toNanos();
  private static final Duration STEP_WINDOW = Duration.ofNanos(STEP_WINDOW_NANOS);

  private record Progress(int index, long lastStepNanos) {}

  private final Map<UUID, Progress> progress = new HashMap<>();

  private final List<Input> inputs;
  private final BenderManager benderManager;

  public BendingModeCombination(List<Input> inputs, BenderManager benderManager) {
    if (inputs.isEmpty()) {
      throw new IllegalArgumentException("A combination needs at least one input");
    }
    this.inputs = List.copyOf(inputs);
    this.benderManager = benderManager;
  }

  @Override
  public void processInput(UUID uuid, Input input) {
    if (uuid == null) return;

    long now = System.nanoTime();
    Progress current = progress.get(uuid);

    // Too slow, start over
    if (current != null && now - current.lastStepNanos() > STEP_WINDOW_NANOS) {
      current = null;
    }

    int index = current == null ? 0 : current.index();
    if (!inputsMatch(input, inputs.get(index))) {
      // A wrong input may still be the first step of a new try
      index = inputsMatch(input, inputs.getFirst()) ? 0 : -1;
      if (index < 0) {
        progress.remove(uuid);
        return;
      }
    }

    if (index + 1 >= inputs.size()) {
      progress.remove(uuid);
      onSuccess(uuid);
      return;
    }
    progress.put(uuid, new Progress(index + 1, now));
  }

  @Override
  public void onSuccess(UUID uuid) {
    OnlineBenderPlayer benderPlayer = benderManager.getBender(uuid);
    BendingMode next = benderPlayer.mode() == BendingMode.COMBAT ? BendingMode.PASSIVE : BendingMode.COMBAT;
    benderPlayer.mode(next);

    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      announce(player, next);
    }
  }

  private void announce(Player player, BendingMode mode) {
    boolean combat = mode == BendingMode.COMBAT;

    player.showTitle(Title.title(
        Component.empty(),
        Component.text(combat ? "Combat mode" : "Passive mode", combat ? NamedTextColor.GOLD : NamedTextColor.GRAY),
        Title.Times.times(Duration.ZERO, STEP_WINDOW.plusMillis(400), Duration.ofMillis(200))));
    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.6f, combat ? 1.4f : 0.8f);
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
