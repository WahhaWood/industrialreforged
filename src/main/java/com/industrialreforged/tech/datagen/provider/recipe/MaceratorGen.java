package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.MaceratorRecipe;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.StandartMachineBuilder;
import java.util.function.BiFunction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class MaceratorGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      BiFunction<CountIngredient, ItemStack, Recipe<?>> factory = MaceratorRecipe::new;
      StandartMachineBuilder.general(Items.CACTUS, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/cactus"));
      StandartMachineBuilder.general(Items.CARROT, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/carrot"));
      StandartMachineBuilder.general(ItemTags.LEAVES, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/leaves"));
      StandartMachineBuilder.general(Items.MELON_SLICE, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/melon_slice"));
      StandartMachineBuilder.general(Items.MELON_SEEDS, 16, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/melon_seeds"));
      StandartMachineBuilder.general(IRItems.PLANT_BALL, IRItems.BIO_CHAFF, factory).save(consumer, IR.location("macerator/plant_ball"));
      StandartMachineBuilder.general(Items.POTATO, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/potato"));
      StandartMachineBuilder.general(Items.PUMPKIN, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/pumpkin"));
      StandartMachineBuilder.general(Items.PUMPKIN_SEEDS, 16, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/pumpkin_seeds"));
      StandartMachineBuilder.general(ItemTags.SAPLINGS, 4, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/saplings"));
      StandartMachineBuilder.general(Items.SUGAR_CANE, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/sugar_cane"));
      StandartMachineBuilder.general(Items.TALL_GRASS, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/tall_grass"));
      StandartMachineBuilder.general(IRItems.WEED, 32, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/weed"));
      StandartMachineBuilder.general(Items.WHEAT, 8, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/wheat"));
      StandartMachineBuilder.general(Items.WHEAT_SEEDS, 16, IRItems.BIO_CHAFF, 1, factory).save(consumer, IR.location("macerator/wheat_seeds"));
      StandartMachineBuilder.general(IRItems.BIO_CHAFF, Items.DIRT, factory).save(consumer, IR.location("macerator/bio_chaff"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.ORES_IRON, 1, IRItems.CRUSHED_IRON, 2, factory)
         .save(consumer, IR.location("macerator/iron_ore"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.ORES_GOLD, 1, IRItems.CRUSHED_GOLD, 2, factory)
         .save(consumer, IR.location("macerator/gold_ore"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.ORES_COPPER, 1, IRItems.CRUSHED_COPPER, 2, factory)
         .save(consumer, IR.location("macerator/copper_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_TIN, 1, IRItems.CRUSHED_TIN, 2, factory).save(consumer, IR.location("macerator/tin_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_LEAD, 1, IRItems.CRUSHED_LEAD, 2, factory).save(consumer, IR.location("macerator/lead_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_SILVER, 1, IRItems.CRUSHED_SILVER, 2, factory).save(consumer, IR.location("macerator/silver_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_NICKEL, 1, IRItems.CRUSHED_NICKEL, 2, factory).save(consumer, IR.location("macerator/nickel_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_TITANIUM, 1, IRItems.CRUSHED_TITANIUM, 2, factory)
         .save(consumer, IR.location("macerator/titanium_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_TUNGSTEN, 1, IRItems.CRUSHED_TUNGSTEN, 2, factory)
         .save(consumer, IR.location("macerator/tungsten_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_TOXIC, 1, IRItems.CRUSHED_TOXIC, 2, factory).save(consumer, IR.location("macerator/toxic_ore"));
      StandartMachineBuilder.general(IRItemTags.ORES_URANIUM, 1, IRItems.CRUSHED_URANIUM, 2, factory).save(consumer, IR.location("macerator/uranium_ore"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.ORES_DIAMOND, 1, Items.DIAMOND, 2, factory)
         .save(consumer, IR.location("macerator/diamond_ore"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.ORES_LAPIS, 1, Items.LAPIS_LAZULI, 8, factory)
         .save(consumer, IR.location("macerator/lapis_ore"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.ORES_REDSTONE, 1, Items.REDSTONE, 8, factory)
         .save(consumer, IR.location("macerator/redstone_ore"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.ORES_QUARTZ, 1, Items.QUARTZ, 3, factory)
         .save(consumer, IR.location("macerator/quartz_ore"));
      StandartMachineBuilder.general(IRItems.IRIDIUM_ORE, 1, IRItems.IRIDIUM_SHARD, 9, factory).save(consumer, IR.location("macerator/iridium_ore"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.RAW_MATERIALS_IRON, 1, IRItems.CRUSHED_IRON, 2, factory)
         .save(consumer, IR.location("macerator/raw_iron"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.RAW_MATERIALS_GOLD, 1, IRItems.CRUSHED_GOLD, 2, factory)
         .save(consumer, IR.location("macerator/raw_gold"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.RAW_MATERIALS_COPPER, 1, IRItems.CRUSHED_COPPER, 2, factory)
         .save(consumer, IR.location("macerator/raw_copper"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_TIN, 1, IRItems.CRUSHED_TIN, 2, factory).save(consumer, IR.location("macerator/raw_tin"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_LEAD, 1, IRItems.CRUSHED_LEAD, 2, factory).save(consumer, IR.location("macerator/raw_lead"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_SILVER, 1, IRItems.CRUSHED_SILVER, 2, factory)
         .save(consumer, IR.location("macerator/raw_silver"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_NICKEL, 1, IRItems.CRUSHED_NICKEL, 2, factory)
         .save(consumer, IR.location("macerator/raw_nickel"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_TITANIUM, 1, IRItems.CRUSHED_TITANIUM, 2, factory)
         .save(consumer, IR.location("macerator/raw_titanium"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_TOXIC, 1, IRItems.CRUSHED_TOXIC, 2, factory)
         .save(consumer, IR.location("macerator/raw_toxic"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_TUNGSTEN, 1, IRItems.CRUSHED_TUNGSTEN, 2, factory)
         .save(consumer, IR.location("macerator/raw_tungsten"));
      StandartMachineBuilder.general(IRItemTags.RAW_MATERIALS_URANIUM, 1, IRItems.CRUSHED_URANIUM, 2, factory)
         .save(consumer, IR.location("macerator/raw_uranium"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.INGOTS_IRON, IRItems.IRON_DUST, factory)
         .save(consumer, IR.location("macerator/iron_ingot"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.INGOTS_GOLD, IRItems.GOLD_DUST, factory)
         .save(consumer, IR.location("macerator/gold_ingot"));
      StandartMachineBuilder.general(net.neoforged.neoforge.common.Tags.Items.INGOTS_COPPER, IRItems.COPPER_DUST, factory)
         .save(consumer, IR.location("macerator/copper_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_TIN, IRItems.TIN_DUST, factory).save(consumer, IR.location("macerator/tin_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_LEAD, IRItems.LEAD_DUST, factory).save(consumer, IR.location("macerator/lead_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_SILVER, IRItems.SILVER_DUST, factory).save(consumer, IR.location("macerator/silver_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_NICKEL, IRItems.NICKEL_DUST, factory).save(consumer, IR.location("macerator/nickel_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_BRONZE, IRItems.BRONZE_DUST, factory).save(consumer, IR.location("macerator/bronze_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_STEEL, IRItems.STEEL_DUST, factory).save(consumer, IR.location("macerator/steel_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_TITANIUM, IRItems.TITANIUM_DUST, factory).save(consumer, IR.location("macerator/titanium_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_TUNGSTEN, IRItems.TUNGSTEN_DUST, factory).save(consumer, IR.location("macerator/tungsten_ingot"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_TOXIC, IRItems.TOXIC_DUST, factory).save(consumer, IR.location("macerator/toxic_ingot"));
      StandartMachineBuilder.general(IRItems.CRUSHED_IRON, IRItems.IRON_DUST, factory).save(consumer, IR.location("macerator/crushed_iron"));
      StandartMachineBuilder.general(IRItems.CRUSHED_GOLD, IRItems.GOLD_DUST, factory).save(consumer, IR.location("macerator/crushed_gold"));
      StandartMachineBuilder.general(IRItems.CRUSHED_COPPER, IRItems.COPPER_DUST, factory).save(consumer, IR.location("macerator/crushed_copper"));
      StandartMachineBuilder.general(IRItems.CRUSHED_TIN, IRItems.TIN_DUST, factory).save(consumer, IR.location("macerator/crushed_tin"));
      StandartMachineBuilder.general(IRItems.CRUSHED_LEAD, IRItems.LEAD_DUST, factory).save(consumer, IR.location("macerator/crushed_lead"));
      StandartMachineBuilder.general(IRItems.CRUSHED_SILVER, IRItems.SILVER_DUST, factory).save(consumer, IR.location("macerator/crushed_silver"));
      StandartMachineBuilder.general(IRItems.CRUSHED_NICKEL, IRItems.NICKEL_DUST, factory).save(consumer, IR.location("macerator/crushed_nickel"));
      StandartMachineBuilder.general(IRItems.CRUSHED_TITANIUM, IRItems.TITANIUM_DUST, factory).save(consumer, IR.location("macerator/crushed_titanium"));
      StandartMachineBuilder.general(IRItems.CRUSHED_TUNGSTEN, IRItems.TUNGSTEN_DUST, factory).save(consumer, IR.location("macerator/crushed_tungsten"));
      StandartMachineBuilder.general(IRItems.CRUSHED_TOXIC, IRItems.TOXIC_DUST, factory).save(consumer, IR.location("macerator/crushed_toxic"));
      StandartMachineBuilder.general(IRItems.PURIFIED_IRON, IRItems.IRON_DUST, factory).save(consumer, IR.location("macerator/purified_iron"));
      StandartMachineBuilder.general(IRItems.PURIFIED_GOLD, IRItems.GOLD_DUST, factory).save(consumer, IR.location("macerator/purified_gold"));
      StandartMachineBuilder.general(IRItems.PURIFIED_COPPER, IRItems.COPPER_DUST, factory).save(consumer, IR.location("macerator/purified_copper"));
      StandartMachineBuilder.general(IRItems.PURIFIED_TIN, IRItems.TIN_DUST, factory).save(consumer, IR.location("macerator/purified_tin"));
      StandartMachineBuilder.general(IRItems.PURIFIED_LEAD, IRItems.LEAD_DUST, factory).save(consumer, IR.location("macerator/purified_lead"));
      StandartMachineBuilder.general(IRItems.PURIFIED_SILVER, IRItems.SILVER_DUST, factory).save(consumer, IR.location("macerator/purified_silver"));
      StandartMachineBuilder.general(IRItems.PURIFIED_NICKEL, IRItems.NICKEL_DUST, factory).save(consumer, IR.location("macerator/purified_nickel"));
      StandartMachineBuilder.general(IRItems.PURIFIED_TITANIUM, IRItems.TITANIUM_DUST, factory).save(consumer, IR.location("macerator/purified_titanium"));
      StandartMachineBuilder.general(IRItems.PURIFIED_TUNGSTEN, IRItems.TUNGSTEN_DUST, factory).save(consumer, IR.location("macerator/purified_tungsten"));
      StandartMachineBuilder.general(IRItems.PURIFIED_TOXIC, IRItems.TOXIC_DUST, factory).save(consumer, IR.location("macerator/purified_toxic"));
      StandartMachineBuilder.general(IRItemTags.PLATES_IRON, 1, IRItems.SMALL_IRON_DUST, 8, factory).save(consumer, IR.location("macerator/iron_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_GOLD, 1, IRItems.SMALL_GOLD_DUST, 8, factory).save(consumer, IR.location("macerator/gold_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_COPPER, 1, IRItems.SMALL_COPPER_DUST, 8, factory)
         .save(consumer, IR.location("macerator/copper_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_TIN, 1, IRItems.SMALL_TIN_DUST, 8, factory).save(consumer, IR.location("macerator/tin_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_LEAD, 1, IRItems.SMALL_LEAD_DUST, 8, factory).save(consumer, IR.location("macerator/lead_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_SILVER, 1, IRItems.SMALL_SILVER_DUST, 8, factory)
         .save(consumer, IR.location("macerator/silver_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_BRONZE, 1, IRItems.SMALL_BRONZE_DUST, 8, factory)
         .save(consumer, IR.location("macerator/bronze_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_LAPIS, 1, IRItems.SMALL_LAPIS_DUST, 8, factory).save(consumer, IR.location("macerator/lapis_plate"));
      StandartMachineBuilder.general(IRItemTags.PLATES_OBSIDIAN, 1, IRItems.SMALL_OBSIDIAN_DUST, 8, factory)
         .save(consumer, IR.location("macerator/obsidian_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_IRON_PLATE, 1, IRItems.IRON_DUST, 9, factory).save(consumer, IR.location("macerator/dense_iron_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_GOLD_PLATE, 1, IRItems.GOLD_DUST, 9, factory).save(consumer, IR.location("macerator/dense_gold_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_COPPER_PLATE, 1, IRItems.COPPER_DUST, 9, factory)
         .save(consumer, IR.location("macerator/dense_copper_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_TIN_PLATE, 1, IRItems.TIN_DUST, 9, factory).save(consumer, IR.location("macerator/dense_tin_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_LEAD_PLATE, 1, IRItems.LEAD_DUST, 9, factory).save(consumer, IR.location("macerator/dense_lead_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_BRONZE_PLATE, 1, IRItems.BRONZE_DUST, 9, factory)
         .save(consumer, IR.location("macerator/dense_bronze_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_LAPIS_PLATE, 1, IRItems.LAPIS_DUST, 9, factory)
         .save(consumer, IR.location("macerator/dense_lapis_plate"));
      StandartMachineBuilder.general(IRItems.DENSE_OBSIDIAN_PLATE, 1, IRItems.OBSIDIAN_DUST, 9, factory)
         .save(consumer, IR.location("macerator/dense_obsidian_plate"));
      StandartMachineBuilder.general(Items.CLAY, 1, IRItems.CLAY_DUST, 2, factory).save(consumer, IR.location("macerator/clay"));
      StandartMachineBuilder.general(Items.COAL, IRItems.COAL_DUST, factory).save(consumer, IR.location("macerator/coal"));
      StandartMachineBuilder.general(Items.DIAMOND, IRItems.DIAMOND_DUST, factory).save(consumer, IR.location("macerator/diamond"));
      StandartMachineBuilder.general(Items.LAPIS_LAZULI, IRItems.LAPIS_DUST, factory).save(consumer, IR.location("macerator/lapis_lazuli"));
      StandartMachineBuilder.general(Items.OBSIDIAN, 1, IRItems.OBSIDIAN_DUST, 4, factory).save(consumer, IR.location("macerator/obsidian"));
      StandartMachineBuilder.general(Items.NETHERRACK, IRItems.NETHERRACK_DUST, factory).save(consumer, IR.location("macerator/netherrack"));
      StandartMachineBuilder.general(IRItems.ENERGY_CRYSTAL, 1, IRItems.ENERGIUM_DUST, 9, factory).save(consumer, IR.location("macerator/energy_crystal"));
      StandartMachineBuilder.general(IRItems.FUEL_ROD, IRItems.IRON_DUST, factory).save(consumer, IR.location("macerator/fuel_rod"));
      StandartMachineBuilder.general(IRItems.TIN_CAN, 2, IRItems.TIN_DUST, 1, factory).save(consumer, IR.location("macerator/tin_can"));
      StandartMachineBuilder.general(Items.SPIDER_EYE, 1, IRItems.TOXIC_DUST, 2, factory).save(consumer, IR.location("macerator/spider_eye"));
      StandartMachineBuilder.general(Items.POISONOUS_POTATO, IRItems.TOXIC_DUST, factory).save(consumer, IR.location("macerator/poisonous_potato"));
      StandartMachineBuilder.general(Items.BLAZE_ROD, 1, Items.BLAZE_POWDER, 5, factory).save(consumer, IR.location("macerator/blaze_rod"));
      StandartMachineBuilder.general(Items.BONE, 1, Items.BONE_MEAL, 4, factory).save(consumer, IR.location("macerator/bone"));
      StandartMachineBuilder.general(IRItems.COFFEE_BEANS, 1, IRItems.COFFEE_POWDER, 3, factory).save(consumer, IR.location("macerator/coffee_beans"));
      StandartMachineBuilder.general(Items.COAL_BLOCK, 1, IRItems.COAL_DUST, 9, factory).save(consumer, IR.location("macerator/coal_block"));
      StandartMachineBuilder.general(Items.LAPIS_BLOCK, 1, IRItems.LAPIS_DUST, 9, factory).save(consumer, IR.location("macerator/lapis_block"));
      StandartMachineBuilder.general(Items.REDSTONE_BLOCK, 1, Items.REDSTONE, 9, factory).save(consumer, IR.location("macerator/redstone_block"));
      StandartMachineBuilder.general(Items.QUARTZ_BLOCK, 1, Items.QUARTZ, 4, factory).save(consumer, IR.location("macerator/quartz_block"));
      StandartMachineBuilder.general(Items.QUARTZ_STAIRS, 1, Items.QUARTZ, 6, factory).save(consumer, IR.location("macerator/quartz_stairs"));
      StandartMachineBuilder.general(Items.GLOWSTONE, 1, Items.GLOWSTONE_DUST, 4, factory).save(consumer, IR.location("macerator/glowstone"));
      StandartMachineBuilder.general(Items.GRAVEL, Items.FLINT, factory).save(consumer, IR.location("macerator/gravel"));
      StandartMachineBuilder.general(Items.ICE, Items.SNOWBALL, factory).save(consumer, IR.location("macerator/ice"));
      StandartMachineBuilder.general(Items.COBBLESTONE, Items.SAND, factory).save(consumer, IR.location("macerator/cobblestone"));
      StandartMachineBuilder.general(Items.SANDSTONE, Items.SAND, factory).save(consumer, IR.location("macerator/sandstone"));
      StandartMachineBuilder.general(Items.STONE, Items.COBBLESTONE, factory).save(consumer, IR.location("macerator/stone"));
      StandartMachineBuilder.general(ItemTags.WOOL, 1, Items.STRING, 2, factory).save(consumer, IR.location("macerator/wool"));
   }
}
