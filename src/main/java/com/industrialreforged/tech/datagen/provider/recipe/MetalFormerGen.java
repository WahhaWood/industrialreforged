package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.StandartMachineBuilder;
import java.util.function.BiFunction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags.Items;

public class MetalFormerGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      BiFunction<CountIngredient, ItemStack, Recipe<?>> factory = (input, result) -> new MetalFormerRecipe(input, result, MetalFormerRecipe.Mode.CUTTING);
      StandartMachineBuilder.general(IRItemTags.PLATES_COPPER, 1, com.industrialreforged.transfer.init.GTItems.WIRE_2_LEVEL, 2, factory)
         .save(consumer, IR.location("metal_former/cutting/copper_cable"));
      StandartMachineBuilder.general(IRItemTags.PLATES_GOLD, 1, com.industrialreforged.transfer.init.GTItems.WIRE_3_LEVEL, 4, factory)
         .save(consumer, IR.location("metal_former/cutting/gold_cable"));
      StandartMachineBuilder.general(IRItemTags.PLATES_IRON, 1, com.industrialreforged.transfer.init.GTItems.WIRE_4_LEVEL, 4, factory)
         .save(consumer, IR.location("metal_former/cutting/iron_cable"));
      StandartMachineBuilder.general(IRItemTags.PLATES_TIN, 1, com.industrialreforged.transfer.init.GTItems.WIRE_1_LEVEL, 3, factory).save(consumer, IR.location("metal_former/cutting/tin_cable"));
      factory = (input, result) -> new MetalFormerRecipe(input, result, MetalFormerRecipe.Mode.EXTRUDING);
      StandartMachineBuilder.general(Items.INGOTS_COPPER, 1, com.industrialreforged.transfer.init.GTItems.WIRE_2_LEVEL, 3, factory)
         .save(consumer, IR.location("metal_former/extruding/copper_cable"));
      StandartMachineBuilder.general(Items.INGOTS_GOLD, 1, com.industrialreforged.transfer.init.GTItems.WIRE_3_LEVEL, 4, factory).save(consumer, IR.location("metal_former/extruding/gold_cable"));
      StandartMachineBuilder.general(Items.INGOTS_IRON, 1, com.industrialreforged.transfer.init.GTItems.WIRE_4_LEVEL, 4, factory).save(consumer, IR.location("metal_former/extruding/iron_cable"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_TIN, 1, com.industrialreforged.transfer.init.GTItems.WIRE_1_LEVEL, 3, factory)
         .save(consumer, IR.location("metal_former/extruding/tin_cable"));
      StandartMachineBuilder.general(IRItemTags.PLATES_TIN, 1, IRItems.CELL, 3, factory).save(consumer, IR.location("metal_former/extruding/cell"));
      StandartMachineBuilder.general(IRItemTags.PLATES_IRON, IRItems.FUEL_ROD, factory).save(consumer, IR.location("metal_former/extruding/fuel_rod"));
      StandartMachineBuilder.general(IRItems.IRON_CASING, IRItems.IRON_FENCE, factory).save(consumer, IR.location("metal_former/extruding/iron_fence"));
      StandartMachineBuilder.general(IRItemTags.STORAGE_BLOCKS_BRONZE, IRItems.BRONZE_SHAFT, factory)
         .save(consumer, IR.location("metal_former/extruding/bronze_shaft"));
      StandartMachineBuilder.general(Items.STORAGE_BLOCKS_IRON, IRItems.IRON_SHAFT, factory).save(consumer, IR.location("metal_former/extruding/iron_shaft"));
      StandartMachineBuilder.general(IRItemTags.STORAGE_BLOCKS_STEEL, IRItems.STEEL_SHAFT, factory)
         .save(consumer, IR.location("metal_former/extruding/steel_shaft"));
      StandartMachineBuilder.general(IRItems.TIN_CASING, IRItems.TIN_CAN, factory).save(consumer, IR.location("metal_former/extruding/tin_can"));
      factory = (input, result) -> new MetalFormerRecipe(input, result, MetalFormerRecipe.Mode.ROLLING);
      StandartMachineBuilder.general(IRItemTags.PLATES_BRONZE, 1, IRItems.BRONZE_CASING, 2, factory)
         .save(consumer, IR.location("metal_former/rolling/bronze_casing"));
      StandartMachineBuilder.general(IRItemTags.PLATES_COPPER, 1, IRItems.COPPER_CASING, 2, factory)
         .save(consumer, IR.location("metal_former/rolling/copper_casing"));
      StandartMachineBuilder.general(IRItemTags.PLATES_GOLD, 1, IRItems.GOLD_CASING, 2, factory)
         .save(consumer, IR.location("metal_former/rolling/gold_casing"));
      StandartMachineBuilder.general(IRItemTags.PLATES_IRON, 1, IRItems.IRON_CASING, 2, factory)
         .save(consumer, IR.location("metal_former/rolling/iron_casing"));
      StandartMachineBuilder.general(IRItemTags.PLATES_LEAD, 1, IRItems.LEAD_CASING, 2, factory)
         .save(consumer, IR.location("metal_former/rolling/lead_casing"));
      StandartMachineBuilder.general(IRItemTags.PLATES_STEEL, 1, IRItems.STEEL_CASING, 2, factory)
         .save(consumer, IR.location("metal_former/rolling/steel_casing"));
      StandartMachineBuilder.general(IRItemTags.PLATES_TIN, 1, IRItems.TIN_CASING, 2, factory)
         .save(consumer, IR.location("metal_former/rolling/tin_casing"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_BRONZE, IRItems.BRONZE_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/bronze_plate"));
      StandartMachineBuilder.general(net.minecraft.world.item.Items.COPPER_INGOT, IRItems.COPPER_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/copper_plate"));
      StandartMachineBuilder.general(net.minecraft.world.item.Items.GOLD_INGOT, IRItems.GOLD_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/gold_plate"));
      StandartMachineBuilder.general(net.minecraft.world.item.Items.IRON_INGOT, IRItems.IRON_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/iron_plate"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_LEAD, IRItems.LEAD_PLATE, factory).save(consumer, IR.location("metal_former/rolling/lead_plate"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_NICKEL, IRItems.NICKEL_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/nickel_plate"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_SILVER, IRItems.SILVER_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/silver_plate"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_STEEL, IRItems.STEEL_PLATE, factory).save(consumer, IR.location("metal_former/rolling/steel_plate"));
      StandartMachineBuilder.general(IRItemTags.INGOTS_TIN, IRItems.TIN_PLATE, factory).save(consumer, IR.location("metal_former/rolling/tin_plate"));
      StandartMachineBuilder.general(IRItems.TITANIUM_INGOT, IRItems.TITANIUM_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/titanium_plate"));
      StandartMachineBuilder.general(IRItems.TOXIC_INGOT, IRItems.TOXIC_PLATE, factory).save(consumer, IR.location("metal_former/rolling/toxic_plate"));
      StandartMachineBuilder.general(IRItems.TUNGSTEN_INGOT, IRItems.TUNGSTEN_PLATE, factory)
         .save(consumer, IR.location("metal_former/rolling/tungsten_plate"));
   }
}
