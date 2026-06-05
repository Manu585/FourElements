package com.github.manu585.fourelements.bukkit.abilities;

import com.github.manu585.fourelements.core.system.PluginSystem;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

// TODO: Improve by a lot
public class AbilitySystem implements PluginSystem {

  private final Plugin plugin;
  private BukkitTask tickTask;

  public AbilitySystem(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void enable() {
    tickTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
  }

  private void tick() {

  }

}
