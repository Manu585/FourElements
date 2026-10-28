package com.github.manu585.fourelements.bukkit.systems;

import com.github.manu585.fourelements.core.system.PluginSystem;
import com.github.manu585.fourelements.core.tick.Registration;
import com.github.manu585.fourelements.core.tick.TickScheduler;
import com.github.manu585.fourelements.core.tick.TickScope;
import com.github.manu585.fourelements.core.tick.Tickable;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Drives a {@link TickScheduler} from a single scheduler task, so tickables of any interval
 * need no task of their own: register them here, or through a {@link #scope()} that stops
 * them all at once.
 */
public final class TickSystem implements PluginSystem {

  private final Plugin plugin;
  private final TickScheduler scheduler;

  private BukkitTask bukkitTask;

  public TickSystem(Plugin plugin) {
    this.plugin = plugin;
    this.scheduler = new TickScheduler(this::logFailure);
  }

  @Override
  public void enable() {
    bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, scheduler::tick, 0, 1);
  }

  @Override
  public void disable() {
    if (bukkitTask != null) {
      bukkitTask.cancel();
    }
    scheduler.clear();
  }

  /**
   * Tick this every {@code interval} ticks for as long as the plugin runs, or until the returned handle is cancelled.
   */
  public Registration every(int interval, Tickable tickable) {
    return scheduler.every(interval, tickable);
  }

  /**
   * A new scope whose registrations all end when it is closed.
   */
  public TickScope scope() {
    return new TickScope(scheduler);
  }

  // One failing tickable must not stop the others, or cancel the whole ticker
  private void logFailure(Tickable tickable, Throwable t) {
    plugin.getLogger().log(Level.SEVERE, "Tickable " + tickable.getClass().getSimpleName() + " failed", t);
  }

}
