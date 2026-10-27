package com.github.manu585.fourelements.bukkit.systems;

import com.github.manu585.fourelements.core.system.PluginSystem;
import com.github.manu585.fourelements.core.tick.Tickable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Runs every registered {@link Tickable} once per tick from a single scheduler task, so
 * the tickables themselves need no start/stop of their own, register them, unregister them.
 */
@RequiredArgsConstructor
public final class TickSystem implements PluginSystem {

  private final Plugin plugin;

  private final List<Tickable> globalTickable = new CopyOnWriteArrayList<>();
  private final Map<UUID, List<Tickable>> perPlayerTickable = new ConcurrentHashMap<>();

  private BukkitTask bukkitTask;

  @Override
  public void enable() {
    bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickAll, 0, 1);
  }

  @Override
  public void disable() {
    if (bukkitTask != null) {
      bukkitTask.cancel();
    }
    globalTickable.clear();
    perPlayerTickable.clear();
  }

  /**
   * Tick this for as long as the plugin runs.
   */
  public void register(Tickable tickable) {
    globalTickable.add(tickable);
  }

  /**
   * Tick this until {@link #unregister(UUID)} is called for the same player.
   */
  public void register(UUID uuid, Tickable tickable) {
    perPlayerTickable.computeIfAbsent(uuid, _ -> new CopyOnWriteArrayList<>()).add(tickable);
  }

  /**
   * Stop ticking everything registered for this player.
   */
  public void unregister(UUID uuid) {
    perPlayerTickable.remove(uuid);
  }

  private void tickAll() {
    globalTickable.forEach(this::safeTick);
    perPlayerTickable.values().forEach(list -> list.forEach(this::safeTick));
  }

  // One failing tickable must not stop the others, or cancel the whole ticker
  private void safeTick(Tickable tickable) {
    try {
      tickable.tick();
    } catch (Throwable t) {
      plugin.getLogger().log(Level.SEVERE, "Tickable " + tickable.getClass().getSimpleName() + " failed", t);
    }
  }

}
