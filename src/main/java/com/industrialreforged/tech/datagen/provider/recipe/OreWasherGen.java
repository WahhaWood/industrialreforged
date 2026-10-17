package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.OreWasherBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class OreWasherGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      ((OreWasherBuilder)OreWasherBuilder.washer(IRItems.CRUSHED_ALUMINUM, 1, 1000).modId(new String[]{"immersiveengineering"}))
         .result(IRItems.PURIFIED_ALUMINUM)
         .result(IRItems.SMALL_TIN_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_aluminum"));
      OreWasherBuilder.washer(IRItems.CRUSHED_COPPER, 1, 1000)
         .result(IRItems.PURIFIED_COPPER)
         .result(IRItems.SMALL_COPPER_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_copper"));
      OreWasherBuilder.washer(IRItems.CRUSHED_GOLD, 1, 1000)
         .result(IRItems.PURIFIED_GOLD)
         .result(IRItems.SMALL_GOLD_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_gold"));
      OreWasherBuilder.washer(IRItems.CRUSHED_IRON, 1, 1000)
         .result(IRItems.PURIFIED_IRON)
         .result(IRItems.SMALL_IRON_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_iron"));
      OreWasherBuilder.washer(IRItems.CRUSHED_LEAD, 1, 1000)
         .result(IRItems.PURIFIED_LEAD)
         .result(IRItems.SMALL_SULFUR_DUST, 3)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_lead"));
      OreWasherBuilder.washer(IRItems.CRUSHED_NICKEL, 1, 1000)
         .result(IRItems.PURIFIED_NICKEL)
         .result(IRItems.SMALL_COPPER_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_nickel"));
      OreWasherBuilder.washer(IRItems.CRUSHED_OSMIUM, 1, 1000)
         .result(IRItems.PURIFIED_OSMIUM)
         .result(IRItems.SMALL_LEAD_DUST, 3)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_osmium"));
      OreWasherBuilder.washer(IRItems.CRUSHED_SILVER, 1, 1000)
         .result(IRItems.PURIFIED_SILVER)
         .result(IRItems.SMALL_SILVER_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_silver"));
      OreWasherBuilder.washer(IRItems.CRUSHED_TIN, 1, 1000)
         .result(IRItems.PURIFIED_TIN)
         .result(IRItems.SMALL_TIN_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_tin"));
      OreWasherBuilder.washer(IRItems.CRUSHED_TITANIUM, 1, 1000)
         .result(IRItems.PURIFIED_TITANIUM)
         .result(IRItems.SMALL_SILVER_DUST, 4)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_titanium"));
      OreWasherBuilder.washer(IRItems.CRUSHED_TOXIC, 1, 1000)
         .result(IRItems.PURIFIED_TOXIC)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_toxic"));
      OreWasherBuilder.washer(IRItems.CRUSHED_TUNGSTEN, 1, 1000)
         .result(IRItems.PURIFIED_TUNGSTEN)
         .result(IRItems.SMALL_LEAD_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_tungsten"));
      OreWasherBuilder.washer(IRItems.CRUSHED_URANIUM, 1, 1000)
         .result(IRItems.PURIFIED_URANIUM)
         .result(IRItems.SMALL_LEAD_DUST, 2)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("ore_washer/crushed_uranium"));
      OreWasherBuilder.washer(Items.GRAVEL, 1, 1000).result(IRItems.STONE_DUST).save(consumer, IR.location("ore_washer/gravel"));
   }
}
