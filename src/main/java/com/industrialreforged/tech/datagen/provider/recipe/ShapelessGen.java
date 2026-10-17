package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.datagen.builder.GEShapelessRecipeBuilder;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ItemCell;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

public class ShapelessGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      GEShapelessRecipeBuilder.shapeless(IRItems.CARBON_MESH)
         .requires(IRItems.CARBON_FIBRE)
         .requires(IRItems.CARBON_FIBRE)
         .save(consumer, IR.location("shapeless/carbon_mesh"));
      GEShapelessRecipeBuilder.shapeless(IRItems.CONTAINMENT_REACTOR_PLATING)
         .requires(IRItems.REACTOR_PLATING)
         .requires(IRItems.ALLOY)
         .requires(IRItems.ALLOY)
         .save(consumer, IR.location("shapeless/containment_reactor_plating"));
      GEShapelessRecipeBuilder.shapeless(IRItems.ELECTRIC_TREETAP)
         .requires(IRItems.TREETAP)
         .requires(IRItems.SMALL_POWER_UNIT)
         .save(consumer, IR.location("shapeless/electric_treetap"));
      GEShapelessRecipeBuilder.shapeless(IRItems.ELECTRIC_WRENCH)
         .requires(IRItems.WRENCH)
         .requires(IRItems.SMALL_POWER_UNIT)
         .save(consumer, IR.location("shapeless/electric_wrench"));
      GEShapelessRecipeBuilder.shapeless(IRItems.FERTILIZER, 2)
         .requires(Items.DIRT)
         .requires(Items.BONE_MEAL)
         .save(consumer, IR.location("shapeless/fertilizer"));
      GEShapelessRecipeBuilder.shapeless(IRItems.FREQUENCY_TRANSMITTER)
         .requires(IRItems.CIRCUIT)
         .requires(com.industrialreforged.transfer.init.GTItems.WIRE_2_LEVEL)
         .save(consumer, IR.location("shapeless/frequency_transmitter"));
      GEShapelessRecipeBuilder.shapeless(ItemCell.getCell(Fluids.LAVA))
         .requires(IRItems.CELL)
         .requires(Items.LAVA_BUCKET)
         .save(consumer, IR.location("shapeless/lava_cell"));
      GEShapelessRecipeBuilder.shapeless(IRItems.MANUAL_KINETIC_GENERATOR)
         .requires(IRItems.MACHINE)
         .requires(Items.LEVER)
         .save(consumer, IR.location("shapeless/manual_kinetic_generator"));
      GEShapelessRecipeBuilder.shapeless(IRItems.REACTOR_PLATING)
         .requires(IRItemTags.PLATES_LEAD)
         .requires(IRItems.ALLOY)
         .save(consumer, IR.location("shapeless/reactor_plating"));
      GEShapelessRecipeBuilder.shapeless(IRItems.SMALL_URANIUM_235, 9)
         .requires(IRItems.URANIUM_235)
         .save(consumer, IR.location("shapeless/small_uranium_235"));
      GEShapelessRecipeBuilder.shapeless(IRItems.SMALL_URANIUM_238, 9)
         .requires(IRItems.URANIUM_238)
         .save(consumer, IR.location("shapeless/small_uranium_238"));
      GEShapelessRecipeBuilder.shapeless(ItemCell.getCell(Fluids.WATER))
         .requires(IRItems.CELL)
         .requires(Items.WATER_BUCKET)
         .save(consumer, IR.location("shapeless/water_cell"));
      GEShapelessRecipeBuilder.shapeless(IRItems.REINFORCED_STONE)
         .requires(Items.SAND, 2)
         .requires(IRItems.FOAM)
         .save(consumer, IR.location("shapeless/reinforced_stone"));
      GEShapelessRecipeBuilder.shapeless(IRItems.FOAM)
         .requires(ItemCell.getCellIngredient(Fluids.WATER))
         .requires(IRItems.CF_POWDER)
         .save(consumer, IR.location("shapeless/foam"));
      GEShapelessRecipeBuilder.shapeless(IRItems.COLD_COFFEE_MUG)
         .requires(IRItems.EMPTY_MUG)
         .requires(IRItems.COFFEE_POWDER)
         .requires(ItemCell.getCellIngredient(Fluids.WATER))
         .save(consumer, IR.location("shapeless/cold_coffee_mug"));
      GEShapelessRecipeBuilder.shapeless(IRItems.COFFEE_MUG)
         .requires(Items.MILK_BUCKET)
         .requires(IRItems.DARK_COFFEE_MUG)
         .requires(Items.SUGAR)
         .save(consumer, IR.location("shapeless/coffee_mug"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_NANO_AXE).requires(IRItems.NANO_AXE).save(consumer, IR.location("shapeless/inactive_nano_axe"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_NANO_PICKAXE)
         .requires(IRItems.NANO_PICKAXE)
         .save(consumer, IR.location("shapeless/inactive_nano_pickaxe"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_NANO_SHOVEL)
         .requires(IRItems.NANO_SHOVEL)
         .save(consumer, IR.location("shapeless/inactive_nano_shovel"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_QUANTUM_AXE)
         .requires(IRItems.QUANTUM_AXE)
         .save(consumer, IR.location("shapeless/inactive_quantum_axe"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_QUANTUM_PICKAXE)
         .requires(IRItems.QUANTUM_PICKAXE)
         .save(consumer, IR.location("shapeless/inactive_quantum_pickaxe"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_QUANTUM_SHOVEL)
         .requires(IRItems.QUANTUM_SHOVEL)
         .save(consumer, IR.location("shapeless/inactive_quantum_shovel"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_SINGULAR_AXE)
         .requires(IRItems.SINGULAR_AXE)
         .save(consumer, IR.location("shapeless/inactive_singular_axe"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_SINGULAR_PICKAXE)
         .requires(IRItems.SINGULAR_PICKAXE)
         .save(consumer, IR.location("shapeless/inactive_singular_pickaxe"));
      GEShapelessRecipeBuilder.shapeless(IRItems.INACTIVE_SINGULAR_SHOVEL)
         .requires(IRItems.SINGULAR_SHOVEL)
         .save(consumer, IR.location("shapeless/inactive_singular_shovel"));
   }
}
