package com.github.manu585.fourelements.bukkit.listeners;

import com.github.manu585.fourelements.bukkit.features.PlayerFeatures;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
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
 *       bender to an online runtime object and switch on the {@link PlayerFeatures}.</li>
 *   <li><b>Persist &amp; evict</b> ({@link PlayerQuitEvent}) end the features, remove the
 *       online bender and save it back asynchronously.</li>
 * </ol>
 */
public class ConnectionListeners implements Listener {

  private final BenderManager benderManager;
  private final PlayerFeatures playerFeatures;

  public ConnectionListeners(BenderManager benderManager, PlayerFeatures playerFeatures) {
    this.benderManager = benderManager;
    this.playerFeatures = playerFeatures;
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
    Player player = event.getPlayer();
    OnlineBenderPlayer online = benderManager.promote(player.getUniqueId());
    if (online == null) {
      return;
    }

    playerFeatures.attach(player, online);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    playerFeatures.detach(uuid);

    OnlineBenderPlayer online = benderManager.remove(uuid);
    if (online == null) {
      return;
    }

    benderManager.save(online.benderPlayer());
  }

}
