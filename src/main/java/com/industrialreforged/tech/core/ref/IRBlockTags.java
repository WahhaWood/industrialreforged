package com.industrialreforged.tech.core.ref;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class IRBlockTags {
   public static final TagKey<Block> ORES_LEAD = createKey("ores/lead");
   public static final TagKey<Block> ORES_SILVER = createKey("ores/silver");
   public static final TagKey<Block> ORES_TIN = createKey("ores/tin");
   public static final TagKey<Block> ORES_NICKEL = createKey("ores/nickel");
   public static final TagKey<Block> ORES_URANIUM = createKey("ores/uranium");
   public static final TagKey<Block> ORES_TITANIUM = createKey("ores/titanium");
   public static final TagKey<Block> ORES_TUNGSTEN = createKey("ores/tungsten");
   public static final TagKey<Block> ORES_TOXIC = createKey("ores/toxic");
   public static final TagKey<Block> STORAGE_BLOCKS_LEAD = createKey("storage_blocks/lead");
   public static final TagKey<Block> STORAGE_BLOCKS_SILVER = createKey("storage_blocks/silver");
   public static final TagKey<Block> STORAGE_BLOCKS_TIN = createKey("storage_blocks/tin");
   public static final TagKey<Block> STORAGE_BLOCKS_NICKEL = createKey("storage_blocks/nickel");
   public static final TagKey<Block> STORAGE_BLOCKS_URANIUM = createKey("storage_blocks/uranium");
   public static final TagKey<Block> STORAGE_BLOCKS_BRONZE = createKey("storage_blocks/bronze");
   public static final TagKey<Block> STORAGE_BLOCKS_STEEL = createKey("storage_blocks/steel");

   private static TagKey<Block> createKey(String path) {
      return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
   }
}
