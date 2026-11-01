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

  private static final Set<Tag<Material>> EARTH_TAGS = Set.of(
      Tag.BASE_STONE_OVERWORLD,
      Tag.DIRT,
      Tag.TERRACOTTA);

  private static final Set<Material> EXTRA_EARTH = EnumSet.of(
      Material.GRASS_BLOCK,
      Material.DIRT,
      Material.COARSE_DIRT,
      Material.ROOTED_DIRT,
      Material.PODZOL,
      Material.MYCELIUM,
      Material.DIRT_PATH,
      Material.MUD,
      Material.MOSS_BLOCK,
      Material.GRAVEL,
      Material.CLAY,
      Material.COBBLESTONE,
      Material.MOSSY_COBBLESTONE,
      Material.DRIPSTONE_BLOCK,
      Material.CALCITE,
      Material.BLACKSTONE,
      Material.SMOOTH_BASALT);

  private IdentifyUtil() {
    throw new AssertionError("No instances.");
  }

  public static boolean isEarthBendable(Block block) {
    return isEarthBendable(block.getType());
  }

  public static boolean isEarthBendable(Material type) {
    return EXTRA_EARTH.contains(type) || EARTH_TAGS.stream().anyMatch(tag -> tag.isTagged(type));
  }

  public static boolean isPlant(Block block) {
    Material type = block.getType();
    return EXTRA_PLANTS.contains(type) || PLANT_TAGS.stream().anyMatch(tag -> tag.isTagged(type));
  }

}
