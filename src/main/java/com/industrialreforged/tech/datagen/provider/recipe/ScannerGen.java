package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.ScannerRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class ScannerGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      ScannerRecipeBuilder.scan(Blocks.IRON_ORE, 171).save(consumer, IR.location("scanner/iron_ore"));
      ScannerRecipeBuilder.scan(Blocks.GOLD_ORE, 1649).save(consumer, IR.location("scanner/gold_ore"));
      ScannerRecipeBuilder.scan(Items.COPPER_ORE, 141).save(consumer, IR.location("scanner/copper_ore"));
      ScannerRecipeBuilder.scan(IRBlocks.TIN_ORE, 174).save(consumer, IR.location("scanner/tin_ore"));
      ScannerRecipeBuilder.scan((ItemLike)IRBlocks.TITANIUM_ORE.get(), 1000).save(consumer, IR.location("scanner/titanium_ore"));
      ScannerRecipeBuilder.scan((ItemLike)IRBlocks.TUNGSTEN_ORE.get(), 1500).save(consumer, IR.location("scanner/tungsten_ore"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.TOXIC_ORE.get(), 2000).save(consumer, IR.location("scanner/toxic_ore"));
      ScannerRecipeBuilder.scan(IRBlocks.LEAD_ORE, 1114).save(consumer, IR.location("scanner/lead_ore"));
      ScannerRecipeBuilder.scan(IRBlocks.SILVER_ORE, 1525).save(consumer, IR.location("scanner/silver_ore"));
      ScannerRecipeBuilder.scan((ItemLike)IRBlocks.URANIUM_ORE.get(), 2226).save(consumer, IR.location("scanner/uranium_ore"));
      ScannerRecipeBuilder.scan(IRBlocks.NICKEL_ORE, 1014).save(consumer, IR.location("scanner/nickel_ore"));
      ScannerRecipeBuilder.scan(Items.DIAMOND, 4441).save(consumer, IR.location("scanner/diamond"));
      ScannerRecipeBuilder.scan(Items.REDSTONE, 122).save(consumer, IR.location("scanner/redstone"));
      ScannerRecipeBuilder.scan(Items.GLOWSTONE_DUST, 137).save(consumer, IR.location("scanner/glowstone_dust"));
      ScannerRecipeBuilder.scan(Items.COAL, 91).save(consumer, IR.location("scanner/coal"));
      ScannerRecipeBuilder.scan(Items.LAPIS_LAZULI, 663).save(consumer, IR.location("scanner/lapis_lazuli"));
      ScannerRecipeBuilder.scan(Blocks.GRASS_BLOCK, 2635).save(consumer, IR.location("scanner/grass_block"));
      // 62 UU = 2.5M FE in the replicator (40k FE per UU: 4M per mB of matter, 100 UU per mB, plus 51.2 FE of machine time)
      for (net.minecraft.world.level.block.Block wool : new net.minecraft.world.level.block.Block[] {Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIME_WOOL,
         Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL,
         Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL}) {
         ScannerRecipeBuilder.scan(wool, 62).save(consumer, IR.location("scanner/" + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(wool).getPath()));
      }
      ScannerRecipeBuilder.scan(Blocks.DIRT, 14).save(consumer, IR.location("scanner/dirt"));
      ScannerRecipeBuilder.scan(Blocks.COBBLESTONE, 1).save(consumer, IR.location("scanner/cobblestone"));
      ScannerRecipeBuilder.scan(Blocks.NETHERRACK, 11).save(consumer, IR.location("scanner/netherrack"));
      ScannerRecipeBuilder.scan(Blocks.END_STONE, 15).save(consumer, IR.location("scanner/end_stone"));
      ScannerRecipeBuilder.scan(Blocks.SOUL_SAND, 30).save(consumer, IR.location("scanner/soul_sand"));
      ScannerRecipeBuilder.scan(Items.EMERALD, 40170).save(consumer, IR.location("scanner/emerald"));
      ScannerRecipeBuilder.scan(Items.QUARTZ, 3500).save(consumer, IR.location("scanner/quartz"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.PLUTONIUM.get(), 29130).save(consumer, IR.location("scanner/plutonium"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.IRIDIUM_ORE.get(), 12000).save(consumer, IR.location("scanner/iridium_ore"));
      ScannerRecipeBuilder.scan(Items.CLAY_BALL, 2308).save(consumer, IR.location("scanner/clay_ball"));
      ScannerRecipeBuilder.scan(IRItems.STEEL_INGOT, 213).save(consumer, IR.location("scanner/steel_ingot"));
      ScannerRecipeBuilder.scan(Items.IRON_INGOT, 106).save(consumer, IR.location("scanner/iron_ingot"));
      ScannerRecipeBuilder.scan(Items.GOLD_INGOT, 845).save(consumer, IR.location("scanner/gold_ingot"));
      ScannerRecipeBuilder.scan(Items.COPPER_INGOT, 91).save(consumer, IR.location("scanner/copper_ingot"));
      ScannerRecipeBuilder.scan(IRItems.TIN_INGOT, 108).save(consumer, IR.location("scanner/tin_ingot"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.TITANIUM_INGOT.get(), 505).save(consumer, IR.location("scanner/titanium_ingot"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.TUNGSTEN_INGOT.get(), 750).save(consumer, IR.location("scanner/tungsten_ingot"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.TOXIC_INGOT.get(), 1000).save(consumer, IR.location("scanner/toxic_ingot"));
      ScannerRecipeBuilder.scan(IRItems.LEAD_INGOT, 557).save(consumer, IR.location("scanner/lead_ingot"));
      ScannerRecipeBuilder.scan(IRItems.SILVER_INGOT, 762).save(consumer, IR.location("scanner/silver_ingot"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.URANIUM_INGOT.get(), 1113).save(consumer, IR.location("scanner/uranium_ingot"));
      ScannerRecipeBuilder.scan(IRItems.NICKEL_INGOT, 507).save(consumer, IR.location("scanner/nickel_ingot"));
      ScannerRecipeBuilder.scan(Items.NETHER_STAR, 30000).save(consumer, IR.location("scanner/nether_star"));
      ScannerRecipeBuilder.scan((ItemLike)IRItems.RUBBER.get(), 140).save(consumer, IR.location("scanner/rubber"));
   }
}
