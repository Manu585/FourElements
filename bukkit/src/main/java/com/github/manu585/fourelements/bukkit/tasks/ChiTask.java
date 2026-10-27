package com.github.manu585.fourelements.bukkit.tasks;

import com.github.manu585.fourelements.bukkit.manager.ChiManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ChiTask implements Task{

  private final Plugin plugin;
  private final ChiManager chiManager;

  private BukkitTask bukkitTask;

  public ChiTask(Plugin plugin, ChiManager chiManager) {
    this.plugin = plugin;
    this.chiManager = chiManager;
  }

  @Override
  public void start() {
    this.bukkitTask = new BukkitRunnable() {
      @Override
      public void run() {
        chiManager.sendChiActionBar();
      }
    }.runTaskTimer(plugin, 0, 1);
  }

  @Override
  public void stop() {
    if (bukkitTask != null) {
      bukkitTask.cancel();
    }
  }

}
