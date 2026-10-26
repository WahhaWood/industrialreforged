package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.MolecularTransformerBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class MolecularTransformerGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      MolecularTransformerBuilder.transform(Items.DIRT, Items.CLAY, 200000L).save(consumer, IR.location("molecular_transformer/clay"));
      MolecularTransformerBuilder.transform(Items.CHARCOAL, Items.COAL, 200000L).save(consumer, IR.location("molecular_transformer/coal"));
      MolecularTransformerBuilder.transform(Items.SKELETON_SKULL, Items.CREEPER_HEAD, 200000L)
         .save(consumer, IR.location("molecular_transformer/creeper_head"));
      MolecularTransformerBuilder.transform(IRItems.INDUSTRIAL_DIAMOND, Items.DIAMOND, 4000000L).save(consumer, IR.location("molecular_transformer/diamond"));
      MolecularTransformerBuilder.transform(Items.DIAMOND, Items.EMERALD, 50000000L).save(consumer, IR.location("molecular_transformer/emerald"));
      MolecularTransformerBuilder.transform(Items.LIME_WOOL, Items.EMERALD, 20000000L).save(consumer, IR.location("molecular_transformer/emerald_alt"));
      MolecularTransformerBuilder.transform(Items.END_STONE, Items.ENDER_PEARL, 500000L).save(consumer, IR.location("molecular_transformer/ender_pearl"));
      MolecularTransformerBuilder.transform(Items.YELLOW_WOOL, Items.GLOWSTONE, 2000000L).save(consumer, IR.location("molecular_transformer/glowstone"));
      MolecularTransformerBuilder.transform(IRItems.SILVER_INGOT, Items.GOLD_INGOT, 2000000L).save(consumer, IR.location("molecular_transformer/gold_ingot"));
      MolecularTransformerBuilder.transform(Items.SAND, Items.GRAVEL, 200000L).save(consumer, IR.location("molecular_transformer/gravel"));
      MolecularTransformerBuilder.transform(Items.NETHERRACK, 1, Items.GUNPOWDER, 2, 280000L).save(consumer, IR.location("molecular_transformer/gunpowder"));
      MolecularTransformerBuilder.transform(Items.COAL, IRItems.INDUSTRIAL_DIAMOND, 36000000L)
         .save(consumer, IR.location("molecular_transformer/industrial_diamond"));
      MolecularTransformerBuilder.transform(Items.IRON_INGOT, IRItems.IRIDIUM_ORE, 320000000L)
         .save(consumer, IR.location("molecular_transformer/iridium_ore"));
      MolecularTransformerBuilder.transform(Items.BLUE_WOOL, Items.LAPIS_BLOCK, 2000000L).save(consumer, IR.location("molecular_transformer/lapis_block"));
      MolecularTransformerBuilder.transform(Items.ROTTEN_FLESH, Items.LEATHER, 40000L).save(consumer, IR.location("molecular_transformer/leather"));
      MolecularTransformerBuilder.transform(Items.WITHER_SKELETON_SKULL, Items.NETHER_STAR, 1000000000L)
         .save(consumer, IR.location("molecular_transformer/nether_star"));
      MolecularTransformerBuilder.transform(Items.COPPER_INGOT, IRItems.NICKEL_INGOT, 400000L)
         .save(consumer, IR.location("molecular_transformer/nickel_ingot"));
      MolecularTransformerBuilder.transform(Items.NETHER_BRICK, Items.QUARTZ, 220000L).save(consumer, IR.location("molecular_transformer/quartz"));
      MolecularTransformerBuilder.transform(Items.RED_WOOL, Items.REDSTONE_BLOCK, 2000000L)
         .save(consumer, IR.location("molecular_transformer/redstone_block"));
      MolecularTransformerBuilder.transform(IRItemTags.INGOTS_TIN, IRItems.SILVER_INGOT, 2000000L)
         .save(consumer, IR.location("molecular_transformer/silver_ingot"));
      MolecularTransformerBuilder.transform(Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, 200000L)
         .save(consumer, IR.location("molecular_transformer/skeleton_skull"));
      MolecularTransformerBuilder.transform(Items.GLOWSTONE, IRItems.SUNNARIUM, 36000000L).save(consumer, IR.location("molecular_transformer/sunnarium"));
      MolecularTransformerBuilder.transform(Items.GLOWSTONE_DUST, IRItems.SUNNARIUM_PART, 4000000L)
         .save(consumer, IR.location("molecular_transformer/sunnarium_part"));
      MolecularTransformerBuilder.transform(IRItems.NICKEL_INGOT, IRItems.TITANIUM_INGOT, 2000000L)
         .save(consumer, IR.location("molecular_transformer/titanium_ingot"));
      MolecularTransformerBuilder.transform(IRItems.TITANIUM_INGOT, IRItems.TUNGSTEN_INGOT, 2000000L)
         .save(consumer, IR.location("molecular_transformer/tungsten_ingot"));
      MolecularTransformerBuilder.transform(Items.LEATHER, Items.ZOMBIE_HEAD, 200000L).save(consumer, IR.location("molecular_transformer/zombie_head"));
   }
}
