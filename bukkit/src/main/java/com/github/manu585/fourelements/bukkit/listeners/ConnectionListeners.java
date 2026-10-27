package com.github.manu585.fourelements.bukkit.listeners;

import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.manager.ChiHud;
import com.github.manu585.fourelements.bukkit.systems.TickSystem;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drives the bender lifecycle across a connection:
 *
 * <ol>
 *   <li><b>Load</b> ({@link AsyncPlayerPreLoginEvent}) off the main thread, block on the
 *       manager to fetch (or create) the bender and stash it as pending.</li>
 *   <li><b>Attach</b> ({@link PlayerJoinEvent})  on the main thread, promote the pending
 *       bender to an online runtime object.</li>
 *   <li><b>Persist &amp; evict</b> ({@link PlayerQuitEvent}) remove the online bender and
 *       save it back asynchronously.</li>
 * </ol>
 */
public class ConnectionListeners implements Listener {

  private final BenderManager benderManager;
  private final TickSystem tickSystem;

  public ConnectionListeners(BenderManager benderManager, TickSystem tickSystem) {
    this.benderManager = benderManager;
    this.tickSystem = tickSystem;
  }

  @EventHandler
  public void onLogin(AsyncPlayerPreLoginEvent event) {
    UUID uuid = event.getUniqueId();

    try {
      // Queued behind pending edits/saves for this UUID, so the loaded state is current
      benderManager.load(uuid).join();
    } catch (Exception e) {
      // Load failed
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Could not load your bender data. Please try again.", NamedTextColor.RED));
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    benderManager.promote(uuid);

    tickSystem.register(uuid, new ChiHud(benderManager.getBender(uuid), event.getPlayer()));
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    OnlineBenderPlayer online = benderManager.remove(event.getPlayer().getUniqueId());
    if (online == null) {
      return;
    }

    tickSystem.unregister(event.getPlayer().getUniqueId());
    benderManager.save(online.benderPlayer());
  }

}
