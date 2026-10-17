package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.BlockCutterBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

public class BlockCutterGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      BlockCutterBuilder.cutter(Items.ACACIA_LOG, 1, Items.ACACIA_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/acacia_log"));
      BlockCutterBuilder.cutter(ItemTags.PLANKS, 1, Items.STICK, 6, 1).save(consumer, IR.location("block_cutter/planks"));
      BlockCutterBuilder.cutter(Items.BIRCH_LOG, 1, Items.BIRCH_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/birch_log"));
      BlockCutterBuilder.cutter(IRItemTags.STORAGE_BLOCKS_BRONZE, 1, IRItems.BRONZE_PLATE, 9, 2).save(consumer, IR.location("block_cutter/bronze"));
      BlockCutterBuilder.cutter(net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS_COPPER, 1, IRItems.COPPER_PLATE, 9, 2)
         .save(consumer, IR.location("block_cutter/copper"));
      BlockCutterBuilder.cutter(net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS_GOLD, 1, IRItems.GOLD_PLATE, 9, 2)
         .save(consumer, IR.location("block_cutter/gold"));
      BlockCutterBuilder.cutter(net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS_IRON, 1, IRItems.IRON_PLATE, 9, 5)
         .save(consumer, IR.location("block_cutter/iron"));
      BlockCutterBuilder.cutter(net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS_LAPIS, 1, IRItems.LAPIS_PLATE, 9, 5)
         .save(consumer, IR.location("block_cutter/lapis"));
      BlockCutterBuilder.cutter(IRItemTags.STORAGE_BLOCKS_LEAD, 1, IRItems.LEAD_PLATE, 9, 2).save(consumer, IR.location("block_cutter/lead"));
      BlockCutterBuilder.cutter(IRItemTags.STORAGE_BLOCKS_NICKEL, 1, IRItems.NICKEL_PLATE, 9, 2).save(consumer, IR.location("block_cutter/nickel"));
      BlockCutterBuilder.cutter(IRItemTags.STORAGE_BLOCKS_STEEL, 1, IRItems.STEEL_PLATE, 9, 8).save(consumer, IR.location("block_cutter/steel"));
      BlockCutterBuilder.cutter(IRItemTags.STORAGE_BLOCKS_TIN, 1, IRItems.TIN_PLATE, 9, 2).save(consumer, IR.location("block_cutter/tin"));
      BlockCutterBuilder.cutter(Items.CHERRY_LOG, 1, Items.CHERRY_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/cherry_log"));
      BlockCutterBuilder.cutter(Items.DARK_OAK_LOG, 1, Items.DARK_OAK_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/dark_oak_log"));
      BlockCutterBuilder.cutter(Items.JUNGLE_LOG, 1, Items.JUNGLE_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/jungle_log"));
      BlockCutterBuilder.cutter(Items.MANGROVE_LOG, 1, Items.MANGROVE_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/mangrove_log"));
      BlockCutterBuilder.cutter(Items.OAK_LOG, 1, Items.OAK_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/oak_log"));
      BlockCutterBuilder.cutter(Items.OBSIDIAN, 1, IRItems.OBSIDIAN_PLATE, 4, 8).save(consumer, IR.location("block_cutter/obsidian"));
      BlockCutterBuilder.cutter(IRItems.RUBBER_LOG, 1, Items.JUNGLE_PLANKS, 4, 1).save(consumer, IR.location("block_cutter/rubber_log"));
      BlockCutterBuilder.cutter(Items.SPRUCE_LOG, 1, Items.SPRUCE_PLANKS, 6, 1).save(consumer, IR.location("block_cutter/spruce_log"));
   }
}
