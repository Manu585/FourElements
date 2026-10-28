package com.github.manu585.fourelements.bukkit.util;

import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

public final class IdentifyUtil {

  private static final Set<Tag<Material>> PLANT_TAGS = Set.of(
      Tag.LEAVES,
      Tag.FLOWERS,
      Tag.SAPLINGS,
      Tag.CROPS);

  private static final Set<Material> EXTRA_PLANTS = EnumSet.of(
      Material.SUGAR_CANE,
      Material.CACTUS,
      Material.BAMBOO,
      Material.VINE,
      Material.LILY_PAD,
      Material.KELP,
      Material.SEAGRASS,
      Material.MELON,
      Material.PUMPKIN);

  private IdentifyUtil() {
    throw new AssertionError("No instances.");
  }

  public static boolean isPlant(Block block) {
    Material type = block.getType();
    return EXTRA_PLANTS.contains(type) || PLANT_TAGS.stream().anyMatch(tag -> tag.isTagged(type));
  }

}
