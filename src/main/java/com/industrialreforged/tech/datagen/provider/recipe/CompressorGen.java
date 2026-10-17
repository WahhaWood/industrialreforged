package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.CompressorRecipe;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.StandartMachineBuilder;
import com.industrialreforged.tech.item.ItemCell;
import java.util.function.BiFunction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluids;

public class CompressorGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      BiFunction<CountIngredient, ItemStack, Recipe<?>> factory = CompressorRecipe::new;
      StandartMachineBuilder.general(Items.BLAZE_POWDER, 5, Items.BLAZE_ROD, 1, factory).save(consumer, IR.location("compressor/blaze_powder"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_BRONZE, 9, IRItems.BRONZE_BLOCK, 1, factory).save(consumer, IR.location("compressor/bronze_ingot"));
      StandartMachineBuilder.general(Items.GOLD_INGOT, 9, Items.GOLD_BLOCK, 1, factory).save(consumer, IR.location("compressor/gold_ingot"));
      StandartMachineBuilder.general(Items.IRON_INGOT, 9, Items.IRON_BLOCK, 1, factory).save(consumer, IR.location("compressor/iron_ingot"));
      StandartMachineBuilder.general(Items.LAPIS_LAZULI, 9, Items.LAPIS_BLOCK, 1, factory).save(consumer, IR.location("compressor/lapis_lazuli"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_LEAD, 9, IRItems.LEAD_BLOCK, 1, factory).save(consumer, IR.location("compressor/lead_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_NICKEL, 9, IRItems.NICKEL_BLOCK, 1, factory).save(consumer, IR.location("compressor/nickel_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_SILVER, 9, IRItems.SILVER_BLOCK, 1, factory).save(consumer, IR.location("compressor/silver_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_STEEL, 9, IRItems.STEEL_BLOCK, 1, factory).save(consumer, IR.location("compressor/steel_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_TIN, 9, IRItems.TIN_BLOCK, 1, factory).save(consumer, IR.location("compressor/tin_ingot"));
      StandartMachineBuilder.general(Items.BRICK, 4, Items.BRICKS, 1, factory).save(consumer, IR.location("compressor/brick"));
      StandartMachineBuilder.general(IRItems.CARBON_MESH, IRItems.CARBON_PLATE, factory).save(consumer, IR.location("compressor/carbon_mesh"));
      StandartMachineBuilder.general(Items.CLAY_BALL, 4, Items.CLAY, 1, factory).save(consumer, IR.location("compressor/clay_ball"));
      StandartMachineBuilder.general(IRItems.COAL_BALL, IRItems.COAL_BLOCK, factory).save(consumer, IR.location("compressor/coal_ball"));
      StandartMachineBuilder.general(IRItems.COAL_CHUNK, Items.DIAMOND, factory).save(consumer, IR.location("compressor/coal_chunk"));
      StandartMachineBuilder.general(Items.COPPER_INGOT, 9, Items.COPPER_BLOCK, 1, factory).save(consumer, IR.location("compressor/copper_ingot"));
      StandartMachineBuilder.general(IRItems.CRUSHED_URANIUM, IRItems.URANIUM_INGOT, factory).save(consumer, IR.location("compressor/crushed_uranium"));
      StandartMachineBuilder.general(IRItemTags.PLATES_BRONZE, 9, IRItems.DENSE_BRONZE_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/bronze_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_COPPER, 9, IRItems.DENSE_COPPER_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/copper_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_GOLD, 9, IRItems.DENSE_GOLD_PLATE, 1, factory).save(consumer, IR.location("compressor/gold_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_IRON, 9, IRItems.DENSE_IRON_PLATE, 1, factory).save(consumer, IR.location("compressor/iron_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_LAPIS, 9, IRItems.DENSE_LAPIS_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/lapis_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_LEAD, 9, IRItems.DENSE_LEAD_PLATE, 1, factory).save(consumer, IR.location("compressor/lead_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_NICKEL, 9, IRItems.DENSE_NICKEL_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/nickel_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_OBSIDIAN, 9, IRItems.DENSE_OBSIDIAN_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/obsidian_plate"));
      StandartMachineBuilder.general(IRItems.OSMIUM_PLATE, 9, IRItems.DENSE_OSMIUM_PLATE, 1, factory).save(consumer, IR.location("compressor/osmium_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_STEEL, 9, IRItems.DENSE_STEEL_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/steel_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_TIN, 9, IRItems.DENSE_TIN_PLATE, 1, factory).save(consumer, IR.location("compressor/tin_plate"));
      StandartMachineBuilder.general(IRItemTags.DUSTS_LAPIS, IRItems.LAPIS_PLATE, factory).save(consumer, IR.location("compressor/lapis_dust"));
      StandartMachineBuilder.general(IRItemTags.DUSTS_OBSIDIAN, IRItems.OBSIDIAN_PLATE, factory).save(consumer, IR.location("compressor/obsidian_dust"));
      StandartMachineBuilder.general(ItemCell.getEmptyCellIngredient(), 1, IRItems.COMPRESSED_AIR_CELL.toStack(), factory)
         .save(consumer, IR.location("compressor/empty_cell"));
      StandartMachineBuilder.general(IRItems.ENERGIUM_DUST, 9, IRItems.ENERGY_CRYSTAL, 1, factory).save(consumer, IR.location("compressor/energium_dust"));
      StandartMachineBuilder.general(Items.GLOWSTONE_DUST, 4, Items.GLOWSTONE, 1, factory).save(consumer, IR.location("compressor/glowstone_dust"));
      StandartMachineBuilder.general(Items.ICE, 2, Items.PACKED_ICE, 1, factory).save(consumer, IR.location("compressor/ice"));
      StandartMachineBuilder.general(IRItems.IRIDIUM_ORE, IRItems.IRIDIUM_INGOT, factory).save(consumer, IR.location("compressor/iridium_ore"));
      StandartMachineBuilder.general(IRItems.IRIDIUM_SHARD, 9, IRItems.IRIDIUM_ORE, 1, factory).save(consumer, IR.location("compressor/iridium_shard"));
      StandartMachineBuilder.general(IRItems.MIXED_METAL_INGOT, IRItems.ALLOY, factory).save(consumer, IR.location("compressor/mixed_metal_ingot"));
      StandartMachineBuilder.general(Items.NETHER_BRICK, 4, Items.NETHER_BRICKS, 1, factory).save(consumer, IR.location("compressor/nether_brick"));
      StandartMachineBuilder.general(IRItems.PURIFIED_URANIUM, IRItems.URANIUM_INGOT, factory).save(consumer, IR.location("compressor/purified_uranium"));
      StandartMachineBuilder.general(IRItems.RAW_URANIUM, IRItems.URANIUM_INGOT, factory).save(consumer, IR.location("compressor/raw_uranium"));
      StandartMachineBuilder.general(Items.REDSTONE, 9, Items.REDSTONE_BLOCK, 1, factory).save(consumer, IR.location("compressor/redstone"));
      StandartMachineBuilder.general(Items.SAND, 4, Items.SANDSTONE, 1, factory).save(consumer, IR.location("compressor/sand"));
      StandartMachineBuilder.general(IRItems.SMALL_BRONZE_DUST, 9, IRItems.BRONZE_DUST, 1, factory)
         .save(consumer, IR.location("compressor/small_bronze_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_COPPER_DUST, 9, IRItems.COPPER_DUST, 1, factory)
         .save(consumer, IR.location("compressor/small_copper_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_GOLD_DUST, 9, IRItems.GOLD_DUST, 1, factory).save(consumer, IR.location("compressor/small_gold_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_IRON_DUST, 9, IRItems.IRON_DUST, 1, factory).save(consumer, IR.location("compressor/small_iron_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_LAPIS_DUST, 9, IRItems.LAPIS_DUST, 1, factory).save(consumer, IR.location("compressor/small_lapis_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_LEAD_DUST, 9, IRItems.LEAD_DUST, 1, factory).save(consumer, IR.location("compressor/small_lead_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_LITHIUM_DUST, 9, IRItems.LITHIUM_DUST, 1, factory)
         .save(consumer, IR.location("compressor/small_lithium_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_OBSIDIAN_DUST, 9, IRItems.OBSIDIAN_DUST, 1, factory)
         .save(consumer, IR.location("compressor/small_obsidian_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_PLUTONIUM, 9, IRItems.PLUTONIUM, 1, factory).save(consumer, IR.location("compressor/small_plutonium"));
      StandartMachineBuilder.general(IRItems.SMALL_SILVER_DUST, 9, IRItems.SILVER_DUST, 1, factory)
         .save(consumer, IR.location("compressor/small_silver_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_SULFUR_DUST, 9, IRItems.SULFUR_DUST, 1, factory)
         .save(consumer, IR.location("compressor/small_sulfur_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_TIN_DUST, 9, IRItems.TIN_DUST, 1, factory).save(consumer, IR.location("compressor/small_tin_dust"));
      StandartMachineBuilder.general(IRItems.SMALL_URANIUM_235, 9, IRItems.URANIUM_235, 1, factory)
         .save(consumer, IR.location("compressor/small_uranium_235"));
      StandartMachineBuilder.general(Items.SNOWBALL, 4, Items.SNOW_BLOCK, 1, factory).save(consumer, IR.location("compressor/snowball"));
      StandartMachineBuilder.general(Items.SNOW_BLOCK, Items.ICE, factory).save(consumer, IR.location("compressor/snow_block"));
      StandartMachineBuilder.general(IRItems.TITANIUM_PLATE, 9, IRItems.DENSE_TITANIUM_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/titanium_plate"));
      StandartMachineBuilder.general(IRItems.TOXIC_PLATE, 9, IRItems.DENSE_TOXIC_PLATE, 1, factory).save(consumer, IR.location("compressor/toxic_plate"));
      StandartMachineBuilder.general(IRItems.TUNGSTEN_PLATE, 9, IRItems.DENSE_TUNGSTEN_PLATE, 1, factory)
         .save(consumer, IR.location("compressor/tungsten_plate"));
      StandartMachineBuilder.general(IRItems.SMALL_URANIUM_238, 9, IRItems.URANIUM_238, 1, factory)
         .save(consumer, IR.location("compressor/small_uranium_238"));
      StandartMachineBuilder.general(IRItemTags.ORES_URANIUM, IRItems.URANIUM_INGOT, factory).save(consumer, IR.location("compressor/uranium_ore"));
      StandartMachineBuilder.general(Items.WATER_BUCKET, Items.SNOW_BLOCK, factory).save(consumer, IR.location("compressor/water_bucket"));
      StandartMachineBuilder.general(ItemCell.getCellIngredient(Fluids.WATER), 1, new ItemStack(Items.SNOW_BLOCK), factory)
         .save(consumer, IR.location("compressor/water_cell"));
   }
}
