package com.github.manu585.fourelements.bukkit.listeners;

import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.bender.BenderPlayer;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.github.manu585.fourelements.core.repository.BenderRepository;
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
 *       repository to fetch (or create) the bender, then stash it as pending.</li>
 *   <li><b>Attach</b> ({@link PlayerJoinEvent})  on the main thread, promote the pending
 *       bender to an online runtime object.</li>
 *   <li><b>Persist &amp; evict</b> ({@link PlayerQuitEvent}) remove the online bender and
 *       save it back asynchronously.</li>
 * </ol>
 */
public class ConnectionListeners implements Listener {

  private final BenderManager benderManager;
  private final BenderRepository benderRepository;

  public ConnectionListeners(BenderManager benderManager, BenderRepository benderRepository) {
    this.benderManager = benderManager;
    this.benderRepository = benderRepository;
  }

  @EventHandler
  public void onLogin(AsyncPlayerPreLoginEvent event) {
    UUID uuid = event.getUniqueId();

    try {
      BenderPlayer benderPlayer = benderRepository.getBender(uuid).join();
      if (benderPlayer == null) {
        // First ever login
        benderPlayer = new BenderPlayer(uuid);
      }
      benderManager.markPending(benderPlayer);
    } catch (Exception e) {
      // Load failed
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Could not load your bender data. Please try again.", NamedTextColor.RED));
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();

    OnlineBenderPlayer online = benderManager.promote(uuid);
    if (online == null) {
      return;
    }

    // Default the active element to the first one the bender knows, if any.
    online.elements().stream().findFirst().ifPresent(online::setActiveElement);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    OnlineBenderPlayer online = benderManager.remove(event.getPlayer().getUniqueId());
    if (online == null) {
      return;
    }

    benderRepository.saveBender(online.benderPlayer());
  }

}
