package com.github.manu585.fourelements.bukkit.listeners.bending;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.manager.bendermode.Combination;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;

public class EnterBendingModeListener implements Listener {

  private final BenderManager benderManager;
  private final Combination combination;

  public EnterBendingModeListener(BenderManager benderManager, Combination combination) {
    this.benderManager = benderManager;
    this.combination = combination;
  }

  @EventHandler
  public void onPlayerInput(PlayerInputEvent event) {
    Player player = event.getPlayer();
    Bender bender = benderManager.getBender(player.getUniqueId());

    if (!bender.isBender()) return;

    // The event fires before the input is stored on the player, so only the event knows the new one
    combination.processInput(player.getUniqueId(), event.getInput());
  }

}
