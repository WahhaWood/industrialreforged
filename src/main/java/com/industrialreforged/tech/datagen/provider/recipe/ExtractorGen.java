package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.ExtractorRecipe;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.StandartMachineBuilder;
import java.util.function.BiFunction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class ExtractorGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      BiFunction<CountIngredient, ItemStack, Recipe<?>> factory = ExtractorRecipe::new;
      StandartMachineBuilder.general(IRItems.COMPRESSED_AIR_CELL, IRItems.CELL, factory).save(consumer, IR.location("extractor/compressed_air_cell"));
      StandartMachineBuilder.general(Items.BRICKS, 1, Items.BRICK, 4, factory).save(consumer, IR.location("extractor/bricks"));
      StandartMachineBuilder.general(Items.CLAY, 1, Items.CLAY_BALL, 4, factory).save(consumer, IR.location("extractor/clay"));
      StandartMachineBuilder.general(IRItems.FILLED_TIN_CAN, IRItems.TIN_CAN, factory).save(consumer, IR.location("extractor/filled_tin_can"));
      StandartMachineBuilder.general(Items.GUNPOWDER, IRItems.SULFUR_DUST, factory).save(consumer, IR.location("extractor/gunpowder"));
      StandartMachineBuilder.general(IRItems.HYDRATED_TIN_DUST, IRItems.IODINE, factory).save(consumer, IR.location("extractor/hydrated_tin_dust"));
      StandartMachineBuilder.general(Items.NETHERRACK, IRItems.SMALL_SULFUR_DUST, factory).save(consumer, IR.location("extractor/netherrack"));
      StandartMachineBuilder.general(Items.NETHER_BRICKS, 1, Items.NETHER_BRICK, 4, factory).save(consumer, IR.location("extractor/nether_bricks"));
      StandartMachineBuilder.general(IRItems.RESIN, 1, IRItems.RUBBER, 3, factory).save(consumer, IR.location("extractor/resin"));
      StandartMachineBuilder.general(IRItems.RUBBER_LOG, IRItems.RUBBER, factory).save(consumer, IR.location("extractor/rubber_log"));
      StandartMachineBuilder.general(IRItems.RUBBER_SAPLING, IRItems.RUBBER, factory).save(consumer, IR.location("extractor/rubber_sapling"));
      StandartMachineBuilder.general(Items.SNOW_BLOCK, 1, Items.SNOWBALL, 4, factory).save(consumer, IR.location("extractor/snow_block"));
   }
}
