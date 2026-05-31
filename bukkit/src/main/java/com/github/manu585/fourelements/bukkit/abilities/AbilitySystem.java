package com.github.manu585.fourelements.bukkit.abilities;

import com.github.manu585.fourelements.api.bending.ability.ActiveAbility;
import com.github.manu585.fourelements.core.registry.AbilityRegistry;
import com.github.manu585.fourelements.core.system.PluginSystem;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

// TODO: Improve by a lot
public class AbilitySystem implements PluginSystem {

  private final Plugin plugin;
  private final AbilityRegistry abilityRegistry;
  private BukkitTask tickTask;

  private final Map<UUID, ActiveAbility> activeAbilities = new ConcurrentHashMap<>();

  public AbilitySystem(Plugin plugin, AbilityRegistry abilityRegistry) {
    this.plugin = plugin;
    this.abilityRegistry = abilityRegistry;
  }

  @Override
  public void enable() {
    tickTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
  }

  private void tick() {
    activeAbilities.values().forEach(ActiveAbility::tick);
  }

}
