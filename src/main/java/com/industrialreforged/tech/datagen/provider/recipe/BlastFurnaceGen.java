package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.BlastFurnaceBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.Tags.Items;

public class BlastFurnaceGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      BlastFurnaceBuilder.blast(IRItems.CRUSHED_IRON, 1)
         .result(IRItems.STEEL_INGOT)
         .result(IRItems.SLAG)
         .save(consumer, IR.location("blast_furnace/crushed_iron"));
      BlastFurnaceBuilder.blast(IRItemTags.DUSTS_IRON, 1)
         .result(IRItems.STEEL_INGOT)
         .result(IRItems.SLAG)
         .save(consumer, IR.location("blast_furnace/iron_dust"));
      BlastFurnaceBuilder.blast(Items.INGOTS_IRON, 1)
         .result(IRItems.STEEL_INGOT)
         .result(IRItems.SLAG)
         .save(consumer, IR.location("blast_furnace/iron_ingot"));
      BlastFurnaceBuilder.blast(Items.ORES_IRON, 1).result(IRItems.STEEL_INGOT).result(IRItems.SLAG).save(consumer, IR.location("blast_furnace/iron_ore"));
      BlastFurnaceBuilder.blast(IRItems.PURIFIED_IRON, 1)
         .result(IRItems.STEEL_INGOT)
         .result(IRItems.SLAG)
         .save(consumer, IR.location("blast_furnace/purified_iron"));
   }
}
