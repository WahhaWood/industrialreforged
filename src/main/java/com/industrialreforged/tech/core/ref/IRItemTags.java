package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.core.IR;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class IRItemTags {
   public static final TagKey<Item> INGOTS_TIN = createKey("ingots/tin");
   public static final TagKey<Item> INGOTS_TITANIUM = createKey("ingots/titanium");
   public static final TagKey<Item> INGOTS_TUNGSTEN = createKey("ingots/tungsten");
   public static final TagKey<Item> INGOTS_TOXIC = createKey("ingots/toxic");
   public static final TagKey<Item> INGOTS_LEAD = createKey("ingots/lead");
   public static final TagKey<Item> INGOTS_NICKEL = createKey("ingots/nickel");
   public static final TagKey<Item> INGOTS_SILVER = createKey("ingots/silver");
   public static final TagKey<Item> INGOTS_BRONZE = createKey("ingots/bronze");
   public static final TagKey<Item> INGOTS_STEEL = createKey("ingots/steel");
   public static final TagKey<Item> INGOTS_IRIDIUM = createKey("ingots/iridium");
   public static final TagKey<Item> INGOTS_URANIUM = createKey("ingots/uranium");
   public static final TagKey<Item> NUGGETS_IRIDIUM = createKey("nuggets/iridium");
   public static final TagKey<Item> DUSTS_COAL = createKey("dusts/coal");
   public static final TagKey<Item> DUSTS_LAPIS = createKey("dusts/lapis");
   public static final TagKey<Item> DUSTS_OBSIDIAN = createKey("dusts/obsidian");
   public static final TagKey<Item> DUSTS_DIAMOND = createKey("dusts/diamond");
   public static final TagKey<Item> DUSTS_CLAY = createKey("dusts/clay");
   public static final TagKey<Item> DUSTS_IRON = createKey("dusts/iron");
   public static final TagKey<Item> DUSTS_GOLD = createKey("dusts/gold");
   public static final TagKey<Item> DUSTS_COPPER = createKey("dusts/copper");
   public static final TagKey<Item> DUSTS_TIN = createKey("dusts/tin");
   public static final TagKey<Item> DUSTS_TITANIUM = createKey("dusts/titanium");
   public static final TagKey<Item> DUSTS_TUNGSTEN = createKey("dusts/tungsten");
   public static final TagKey<Item> DUSTS_TOXIC = createKey("dusts/toxic");
   public static final TagKey<Item> DUSTS_LEAD = createKey("dusts/lead");
   public static final TagKey<Item> DUSTS_NICKEL = createKey("dusts/nickel");
   public static final TagKey<Item> DUSTS_SILVER = createKey("dusts/silver");
   public static final TagKey<Item> DUSTS_BRONZE = createKey("dusts/bronze");
   public static final TagKey<Item> DUSTS_STEEL = createKey("dusts/steel");
   public static final TagKey<Item> DUSTS_STONE = createKey("dusts/stone");
   public static final TagKey<Item> DUSTS_LITHIUM = createKey("dusts/lithium");
   public static final TagKey<Item> DUSTS_SULFUR = createKey("dusts/sulfur");
   public static final TagKey<Item> PLATES = createKey("plates");
   public static final TagKey<Item> PLATES_IRON = createKey("plates/iron");
   public static final TagKey<Item> PLATES_GOLD = createKey("plates/gold");
   public static final TagKey<Item> PLATES_COPPER = createKey("plates/copper");
   public static final TagKey<Item> PLATES_TIN = createKey("plates/tin");
   public static final TagKey<Item> PLATES_TITANIUM = createKey("plates/titanium");
   public static final TagKey<Item> PLATES_TOXIC = createKey("plates/toxic");
   public static final TagKey<Item> PLATES_TUNGSTEN = createKey("plates/tungsten");
   public static final TagKey<Item> PLATES_LEAD = createKey("plates/lead");
   public static final TagKey<Item> PLATES_NICKEL = createKey("plates/nickel");
   public static final TagKey<Item> PLATES_SILVER = createKey("plates/silver");
   public static final TagKey<Item> PLATES_BRONZE = createKey("plates/bronze");
   public static final TagKey<Item> PLATES_IRIDIUM = createKey("plates/iridium");
   public static final TagKey<Item> PLATES_STEEL = createKey("plates/steel");
   public static final TagKey<Item> PLATES_LAPIS = createKey("plates/lapis");
   public static final TagKey<Item> PLATES_OBSIDIAN = createKey("plates/obsidian");
   public static final TagKey<Item> PLATES_MANASTEEL = createKey("plates/manasteel");
   public static final TagKey<Item> PLATES_ELEMENTIUM = createKey("plates/elementium");
   public static final TagKey<Item> PLATES_TERRASTEEL = createKey("plates/terrasteel");
   public static final TagKey<Item> ORES_LEAD = createKey("ores/lead");
   public static final TagKey<Item> ORES_SILVER = createKey("ores/silver");
   public static final TagKey<Item> ORES_TIN = createKey("ores/tin");
   public static final TagKey<Item> ORES_NICKEL = createKey("ores/nickel");
   public static final TagKey<Item> ORES_URANIUM = createKey("ores/uranium");
   public static final TagKey<Item> ORES_TITANIUM = createKey("ores/titanium");
   public static final TagKey<Item> ORES_TUNGSTEN = createKey("ores/tungsten");
   public static final TagKey<Item> ORES_TOXIC = createKey("ores/toxic");
   public static final TagKey<Item> RAW_MATERIALS_LEAD = createKey("raw_materials/lead");
   public static final TagKey<Item> RAW_MATERIALS_SILVER = createKey("raw_materials/silver");
   public static final TagKey<Item> RAW_MATERIALS_TIN = createKey("raw_materials/tin");
   public static final TagKey<Item> RAW_MATERIALS_NICKEL = createKey("raw_materials/nickel");
   public static final TagKey<Item> RAW_MATERIALS_URANIUM = createKey("raw_materials/uranium");
   public static final TagKey<Item> RAW_MATERIALS_TITANIUM = createKey("raw_materials/titanium");
   public static final TagKey<Item> RAW_MATERIALS_TUNGSTEN = createKey("raw_materials/tungsten");
   public static final TagKey<Item> RAW_MATERIALS_TOXIC = createKey("raw_materials/toxic");
   public static final TagKey<Item> STORAGE_BLOCKS_LEAD = createKey("storage_blocks/lead");
   public static final TagKey<Item> STORAGE_BLOCKS_SILVER = createKey("storage_blocks/silver");
   public static final TagKey<Item> STORAGE_BLOCKS_TIN = createKey("storage_blocks/tin");
   public static final TagKey<Item> STORAGE_BLOCKS_NICKEL = createKey("storage_blocks/nickel");
   public static final TagKey<Item> STORAGE_BLOCKS_URANIUM = createKey("storage_blocks/uranium");
   public static final TagKey<Item> STORAGE_BLOCKS_BRONZE = createKey("storage_blocks/bronze");
   public static final TagKey<Item> STORAGE_BLOCKS_STEEL = createKey("storage_blocks/steel");

   /** Items the recycler (and the ME recycler) refuses to take. */
   public static final TagKey<Item> RECYCLER_BLACKLIST = createIrKey("recycler_blacklist");

   private static TagKey<Item> createKey(String path) {
      return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
   }

   private static TagKey<Item> createIrKey(String path) {
      return TagKey.create(Registries.ITEM, IR.location(path));
   }
}
