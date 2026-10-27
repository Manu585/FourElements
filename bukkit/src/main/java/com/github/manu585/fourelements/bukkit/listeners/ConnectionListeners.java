package com.github.manu585.fourelements.bukkit.listeners;

import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.manager.ChiManager;
import com.github.manu585.fourelements.bukkit.tasks.ChiTask;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

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

  // TODO: Make dedicated TaskManager and Registry
  private final Map<UUID, ChiTask> activeTasks = new HashMap<>();

  private final Plugin plugin;
  private final BenderManager benderManager;

  public ConnectionListeners(Plugin plugin, BenderManager benderManager) {
    this.benderManager = benderManager;
    this.plugin = plugin;
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

    ChiTask chiTask = new ChiTask(plugin, new ChiManager(benderManager.getBender(uuid), event.getPlayer()));
    chiTask.start();
    activeTasks.put(uuid, chiTask);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    OnlineBenderPlayer online = benderManager.remove(event.getPlayer().getUniqueId());
    if (online == null) {
      return;
    }

    if (activeTasks.containsKey(event.getPlayer().getUniqueId())) {
      ChiTask task = activeTasks.get(event.getPlayer().getUniqueId());
      task.stop();
    }

    activeTasks.remove(event.getPlayer().getUniqueId());
    benderManager.save(online.benderPlayer());
  }

}
