package com.github.manu585.fourelements.bukkit.listeners.system;

import com.github.manu585.fourelements.core.system.PluginSystem;
import java.util.List;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ListenerSystem implements PluginSystem {

  private final Plugin plugin;
  private final List<Listener> listeners;

  public ListenerSystem(Plugin plugin, List<Listener> listeners) {
    this.plugin = plugin;
    this.listeners = listeners;
  }

  @Override
  public void enable() {
    for (Listener listener : listeners) {
      plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
  }

}
