package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class IRItemTagsProvider extends ItemTagsProvider {
   public IRItemTagsProvider(
      PackOutput output,
      CompletableFuture<Provider> lookupProvider,
      CompletableFuture<TagLookup<Block>> blockTagProvider,
      ExistingFileHelper existingFileHelper
   ) {
      super(output, lookupProvider, blockTagProvider, "industrialreforged", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.add(
         ItemTags.AXES, IRItems.BRONZE_AXE, IRItems.NANO_AXE, IRItems.QUANTUM_AXE, IRItems.SINGULAR_AXE, IRItems.CHAINSAW, IRItems.ADVANCED_CHAINSAW
      );
      this.add(ItemTags.HOES, IRItems.BRONZE_HOE, IRItems.ELECTRIC_HOE);
      this.add(ItemTags.PICKAXES, IRItems.BRONZE_PICKAXE, IRItems.NANO_PICKAXE, IRItems.QUANTUM_PICKAXE, IRItems.SINGULAR_PICKAXE);
      this.add(ItemTags.SHOVELS, IRItems.BRONZE_SHOVEL, IRItems.NANO_SHOVEL, IRItems.QUANTUM_SHOVEL, IRItems.SINGULAR_SHOVEL);
      this.add(ItemTags.SWORDS, IRItems.BRONZE_SWORD);
      this.add(
         Items.TOOLS,
         IRItems.BRONZE_AXE,
         IRItems.NANO_AXE,
         IRItems.QUANTUM_AXE,
         IRItems.SINGULAR_AXE,
         IRItems.CHAINSAW,
         IRItems.ADVANCED_CHAINSAW,
         IRItems.BRONZE_SHOVEL,
         IRItems.NANO_SHOVEL,
         IRItems.QUANTUM_SHOVEL,
         IRItems.SINGULAR_SHOVEL,
         IRItems.BRONZE_PICKAXE,
         IRItems.NANO_PICKAXE,
         IRItems.QUANTUM_PICKAXE,
         IRItems.SINGULAR_PICKAXE,
         IRItems.BRONZE_SWORD,
         IRItems.NANO_SABER,
         IRItems.QUANTUM_SABER,
         IRItems.SINGULAR_SABER,
         IRItems.BRONZE_HOE,
         IRItems.ELECTRIC_HOE,
         IRItems.NANO_BOW,
         IRItems.QUANTUM_BOW,
         IRItems.SINGULAR_BOW,
         IRItems.DRILL,
         IRItems.DIAMOND_DRILL,
         IRItems.IRIDIUM_DRILL,
         IRItems.ADVANCED_DRILL,
         IRItems.ULTIMATE_DRILL,
         IRItems.ELECTRIC_TREETAP,
         IRItems.ELECTRIC_WRENCH,
         IRItems.WIND_METER,
         IRItems.VAJRA,
         IRItems.ADVANCED_VAJRA,
         IRItems.GRAVI_TOOL
      );
      this.add(ItemTags.MINING_ENCHANTABLE, IRItems.DRILL, IRItems.DIAMOND_DRILL, IRItems.IRIDIUM_DRILL, IRItems.ADVANCED_DRILL, IRItems.ULTIMATE_DRILL);
      this.add(
         ItemTags.MINING_LOOT_ENCHANTABLE, IRItems.DRILL, IRItems.DIAMOND_DRILL, IRItems.IRIDIUM_DRILL, IRItems.ADVANCED_DRILL, IRItems.ULTIMATE_DRILL
      );
      this.add(IRItemTags.DUSTS_BRONZE, IRItems.BRONZE_DUST);
      this.add(IRItemTags.DUSTS_COAL, IRItems.COAL_DUST);
      this.add(IRItemTags.DUSTS_COPPER, IRItems.COPPER_DUST);
      this.add(IRItemTags.DUSTS_DIAMOND, IRItems.DIAMOND_DUST);
      this.add(IRItemTags.DUSTS_CLAY, IRItems.CLAY_DUST);
      this.add(IRItemTags.DUSTS_GOLD, IRItems.GOLD_DUST);
      this.add(IRItemTags.DUSTS_IRON, IRItems.IRON_DUST);
      this.add(IRItemTags.DUSTS_LAPIS, IRItems.LAPIS_DUST);
      this.add(IRItemTags.DUSTS_LEAD, IRItems.LEAD_DUST);
      this.add(IRItemTags.DUSTS_LITHIUM, IRItems.LITHIUM_DUST);
      this.add(IRItemTags.DUSTS_NICKEL, IRItems.NICKEL_DUST);
      this.add(IRItemTags.DUSTS_OBSIDIAN, IRItems.OBSIDIAN_DUST);
      this.add(IRItemTags.DUSTS_SILVER, IRItems.SILVER_DUST);
      this.add(IRItemTags.DUSTS_STEEL, IRItems.STEEL_DUST);
      this.add(IRItemTags.DUSTS_STONE, IRItems.STONE_DUST);
      this.add(IRItemTags.DUSTS_SULFUR, IRItems.SULFUR_DUST);
      this.add(IRItemTags.DUSTS_TIN, IRItems.TIN_DUST);
      this.add(IRItemTags.DUSTS_TITANIUM, IRItems.TITANIUM_DUST);
      this.add(IRItemTags.DUSTS_TOXIC, IRItems.TOXIC_DUST);
      this.add(IRItemTags.DUSTS_TUNGSTEN, IRItems.TUNGSTEN_DUST);
      this.tag(Items.DUSTS)
         .addTags(
            new TagKey[]{
               IRItemTags.DUSTS_BRONZE,
               IRItemTags.DUSTS_COAL,
               IRItemTags.DUSTS_COPPER,
               IRItemTags.DUSTS_DIAMOND,
               IRItemTags.DUSTS_CLAY,
               IRItemTags.DUSTS_GOLD,
               IRItemTags.DUSTS_IRON,
               IRItemTags.DUSTS_LAPIS,
               IRItemTags.DUSTS_LEAD,
               IRItemTags.DUSTS_LITHIUM,
               IRItemTags.DUSTS_NICKEL,
               IRItemTags.DUSTS_OBSIDIAN,
               IRItemTags.DUSTS_SILVER,
               IRItemTags.DUSTS_STEEL,
               IRItemTags.DUSTS_STONE,
               IRItemTags.DUSTS_SULFUR,
               IRItemTags.DUSTS_TIN,
               IRItemTags.DUSTS_TITANIUM,
               IRItemTags.DUSTS_TOXIC,
               IRItemTags.DUSTS_TUNGSTEN
            }
         );
      this.add(Items.GEMS_DIAMOND, IRItems.INDUSTRIAL_DIAMOND);
      this.add(IRItemTags.INGOTS_BRONZE, IRItems.BRONZE_INGOT);
      this.add(IRItemTags.INGOTS_IRIDIUM, IRItems.IRIDIUM_INGOT);
      this.add(IRItemTags.INGOTS_LEAD, IRItems.LEAD_INGOT);
      this.add(IRItemTags.INGOTS_NICKEL, IRItems.NICKEL_INGOT);
      this.add(IRItemTags.INGOTS_SILVER, IRItems.SILVER_INGOT);
      this.add(IRItemTags.INGOTS_STEEL, IRItems.STEEL_INGOT);
      this.add(IRItemTags.INGOTS_TIN, IRItems.TIN_INGOT);
      this.add(IRItemTags.INGOTS_TITANIUM, IRItems.TITANIUM_INGOT);
      this.add(IRItemTags.INGOTS_TOXIC, IRItems.TOXIC_INGOT);
      this.add(IRItemTags.INGOTS_TUNGSTEN, IRItems.TUNGSTEN_INGOT);
      this.add(IRItemTags.INGOTS_URANIUM, IRItems.URANIUM_INGOT);
      this.tag(Items.INGOTS)
         .addTags(
            new TagKey[]{
               IRItemTags.INGOTS_BRONZE,
               IRItemTags.INGOTS_IRIDIUM,
               IRItemTags.INGOTS_LEAD,
               IRItemTags.INGOTS_NICKEL,
               IRItemTags.INGOTS_SILVER,
               IRItemTags.INGOTS_STEEL,
               IRItemTags.INGOTS_TIN,
               IRItemTags.INGOTS_TITANIUM,
               IRItemTags.INGOTS_TOXIC,
               IRItemTags.INGOTS_TUNGSTEN,
               IRItemTags.INGOTS_URANIUM
            }
         );
      this.add(IRItemTags.NUGGETS_IRIDIUM, IRItems.IRIDIUM_SHARD);
      this.add(IRItemTags.ORES_LEAD, IRItems.LEAD_ORE, IRItems.DEEPSLATE_LEAD_ORE);
      this.add(IRItemTags.ORES_NICKEL, IRItems.NICKEL_ORE, IRItems.DEEPSLATE_NICKEL_ORE);
      this.add(IRItemTags.ORES_SILVER, IRItems.SILVER_ORE, IRItems.DEEPSLATE_SILVER_ORE);
      this.add(IRItemTags.ORES_TIN, IRItems.TIN_ORE, IRItems.DEEPSLATE_TIN_ORE);
      this.add(IRItemTags.ORES_TITANIUM, IRItems.TITANIUM_ORE, IRItems.DEEPSLATE_TITANIUM_ORE);
      this.add(IRItemTags.ORES_TOXIC, IRItems.TOXIC_ORE);
      this.add(IRItemTags.ORES_TUNGSTEN, IRItems.TUNGSTEN_ORE, IRItems.DEEPSLATE_TUNGSTEN_ORE);
      this.add(IRItemTags.ORES_URANIUM, IRItems.URANIUM_ORE, IRItems.DEEPSLATE_URANIUM_ORE);
      this.tag(Items.ORES)
         .addTags(
            new TagKey[]{
               IRItemTags.ORES_LEAD,
               IRItemTags.ORES_NICKEL,
               IRItemTags.ORES_SILVER,
               IRItemTags.ORES_TIN,
               IRItemTags.ORES_TITANIUM,
               IRItemTags.ORES_TOXIC,
               IRItemTags.ORES_TUNGSTEN,
               IRItemTags.ORES_URANIUM
            }
         );
      this.add(IRItemTags.PLATES_BRONZE, IRItems.BRONZE_PLATE);
      this.add(IRItemTags.PLATES_COPPER, IRItems.COPPER_PLATE);
      this.add(IRItemTags.PLATES_GOLD, IRItems.GOLD_PLATE);
      this.add(IRItemTags.PLATES_IRON, IRItems.IRON_PLATE);
      this.add(IRItemTags.PLATES_IRIDIUM, IRItems.IRIDIUM_PLATE);
      this.add(IRItemTags.PLATES_LAPIS, IRItems.LAPIS_PLATE);
      this.add(IRItemTags.PLATES_LEAD, IRItems.LEAD_PLATE);
      this.add(IRItemTags.PLATES_NICKEL, IRItems.NICKEL_PLATE);
      this.add(IRItemTags.PLATES_OBSIDIAN, IRItems.OBSIDIAN_PLATE);
      this.add(IRItemTags.PLATES_MANASTEEL, IRItems.MANASTEEL_PLATE);
      this.add(IRItemTags.PLATES_ELEMENTIUM, IRItems.ELEMENTIUM_PLATE);
      this.add(IRItemTags.PLATES_TERRASTEEL, IRItems.TERRASTEEL_PLATE);
      this.add(IRItemTags.PLATES_SILVER, IRItems.SILVER_PLATE);
      this.add(IRItemTags.PLATES_STEEL, IRItems.STEEL_PLATE);
      this.add(IRItemTags.PLATES_TIN, IRItems.TIN_PLATE);
      this.add(IRItemTags.PLATES_TITANIUM, IRItems.TITANIUM_PLATE);
      this.add(IRItemTags.PLATES_TOXIC, IRItems.TOXIC_PLATE);
      this.add(IRItemTags.PLATES_TUNGSTEN, IRItems.TUNGSTEN_PLATE);
      this.tag(IRItemTags.PLATES)
         .addTags(
            new TagKey[]{
               IRItemTags.PLATES_BRONZE,
               IRItemTags.PLATES_COPPER,
               IRItemTags.PLATES_GOLD,
               IRItemTags.PLATES_IRON,
               IRItemTags.PLATES_IRIDIUM,
               IRItemTags.PLATES_LAPIS,
               IRItemTags.PLATES_LEAD,
               IRItemTags.PLATES_NICKEL,
               IRItemTags.PLATES_OBSIDIAN,
               IRItemTags.PLATES_MANASTEEL,
               IRItemTags.PLATES_ELEMENTIUM,
               IRItemTags.PLATES_TERRASTEEL,
               IRItemTags.PLATES_SILVER,
               IRItemTags.PLATES_STEEL,
               IRItemTags.PLATES_TIN,
               IRItemTags.PLATES_TITANIUM,
               IRItemTags.PLATES_TOXIC,
               IRItemTags.PLATES_TUNGSTEN
            }
         );
      this.add(IRItemTags.RAW_MATERIALS_LEAD, IRItems.RAW_LEAD);
      this.add(IRItemTags.RAW_MATERIALS_NICKEL, IRItems.RAW_NICKEL);
      this.add(IRItemTags.RAW_MATERIALS_SILVER, IRItems.RAW_SILVER);
      this.add(IRItemTags.RAW_MATERIALS_TIN, IRItems.RAW_TIN);
      this.add(IRItemTags.RAW_MATERIALS_TITANIUM, IRItems.RAW_TITANIUM);
      this.add(IRItemTags.RAW_MATERIALS_TOXIC, IRItems.RAW_TOXIC);
      this.add(IRItemTags.RAW_MATERIALS_TUNGSTEN, IRItems.RAW_TUNGSTEN);
      this.add(IRItemTags.RAW_MATERIALS_URANIUM, IRItems.RAW_URANIUM);
      this.tag(Items.RAW_MATERIALS)
         .addTags(
            new TagKey[]{
               IRItemTags.RAW_MATERIALS_LEAD,
               IRItemTags.RAW_MATERIALS_NICKEL,
               IRItemTags.RAW_MATERIALS_SILVER,
               IRItemTags.RAW_MATERIALS_TIN,
               IRItemTags.RAW_MATERIALS_TITANIUM,
               IRItemTags.RAW_MATERIALS_TOXIC,
               IRItemTags.RAW_MATERIALS_TUNGSTEN,
               IRItemTags.RAW_MATERIALS_URANIUM
            }
         );
      this.add(IRItemTags.STORAGE_BLOCKS_BRONZE, IRItems.BRONZE_BLOCK);
      this.add(IRItemTags.STORAGE_BLOCKS_LEAD, IRItems.LEAD_BLOCK);
      this.add(IRItemTags.STORAGE_BLOCKS_NICKEL, IRItems.NICKEL_BLOCK);
      this.add(IRItemTags.STORAGE_BLOCKS_SILVER, IRItems.SILVER_BLOCK);
      this.add(IRItemTags.STORAGE_BLOCKS_STEEL, IRItems.STEEL_BLOCK);
      this.add(IRItemTags.STORAGE_BLOCKS_TIN, IRItems.TIN_BLOCK);
      this.add(IRItemTags.STORAGE_BLOCKS_URANIUM, IRItems.URANIUM_BLOCK);
      this.tag(Items.STORAGE_BLOCKS)
         .addTags(
            new TagKey[]{
               IRItemTags.STORAGE_BLOCKS_BRONZE,
               IRItemTags.STORAGE_BLOCKS_LEAD,
               IRItemTags.STORAGE_BLOCKS_NICKEL,
               IRItemTags.STORAGE_BLOCKS_SILVER,
               IRItemTags.STORAGE_BLOCKS_STEEL,
               IRItemTags.STORAGE_BLOCKS_TIN,
               IRItemTags.STORAGE_BLOCKS_URANIUM
            }
         );
      this.add(Items.TOOLS_BOW, IRItems.NANO_BOW, IRItems.QUANTUM_BOW, IRItems.SINGULAR_BOW);
      this.add(ItemTags.BEACON_PAYMENT_ITEMS, IRItems.SILVER_INGOT, IRItems.NICKEL_INGOT);
   }

   @SafeVarargs
   private void add(TagKey<Item> key, Supplier<? extends Item>... suppliers) {
      this.tag(key).add(Arrays.stream(suppliers).map(Supplier::get).toArray(Item[]::new));
   }
}
