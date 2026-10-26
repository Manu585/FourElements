package com.github.manu585.fourelements.bukkit.bstats;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.plugin.Plugin;

/**
 * Anonymous usage statistics sent to bStats. Only aggregate counts leave the server, never
 * player names or UUIDs.
 */
public final class BstatsMetrics {

  private static final int PLUGIN_ID = 34941;

  public BstatsMetrics(Plugin plugin, BenderManager benderManager) {
    Metrics metrics = new Metrics(plugin, PLUGIN_ID);
    metrics.addCustomChart(new AdvancedPie("elements", () -> elementCounts(benderManager)));
  }

  private static Map<String, Integer> elementCounts(BenderManager benderManager) {
    Map<Element, Integer> counts = new EnumMap<>(Element.class);
    benderManager.online().forEach(bender -> bender.element().ifPresent(element -> counts.merge(element, 1, Integer::sum)));

    Map<String, Integer> pie = new HashMap<>();
    counts.forEach((element, count) -> pie.put(element.displayName(), count));
    return pie;
  }

}
