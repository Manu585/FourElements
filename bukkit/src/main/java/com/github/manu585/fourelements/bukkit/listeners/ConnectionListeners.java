package com.github.manu585.fourelements.bukkit.listeners;

import com.github.manu585.fourelements.api.bending.element.Elements;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.bender.Bender;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListeners implements Listener {

  private final BenderManager benderManager;

  public ConnectionListeners(BenderManager benderManager) {
    this.benderManager = benderManager;
  }

  @EventHandler
  public void onLogin(AsyncPlayerPreLoginEvent event) {
    UUID uuid = event.getUniqueId();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    Bender bender = new Bender(player.getUniqueId(), Set.of(Elements.AIR));
    benderManager.persist(player.getUniqueId(), bender);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
  }

}
