package com.industrialreforged.tech.datagen.provider.recipe;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.datagen.builder.CentrifugeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class CentrifugeGen implements IRecipeGen {
   @Override
   public void build(RecipeOutput consumer) {
      CentrifugeBuilder.centrifuge(IRItems.CLAY_DUST, 4, 250).result(IRItems.SILICON_DIOXIDE_DUST).save(consumer, IR.location("centrifuge/clay_dust"));
      CentrifugeBuilder.centrifuge(Items.COBBLESTONE, 1, 100).result(IRItems.STONE_DUST).save(consumer, IR.location("centrifuge/cobblestone"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_COPPER, 1, 500)
         .result(IRItems.COPPER_DUST)
         .result(IRItems.SMALL_TIN_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_copper"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_GOLD, 1, 2000)
         .result(IRItems.GOLD_DUST)
         .result(IRItems.SMALL_SILVER_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_gold"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_IRON, 1, 1500)
         .result(IRItems.IRON_DUST)
         .result(IRItems.SMALL_GOLD_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_iron"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_LEAD, 1, 2000)
         .result(IRItems.LEAD_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_lead"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_NICKEL, 1, 2000)
         .result(IRItems.NICKEL_DUST)
         .result(IRItems.SMALL_COPPER_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_nickel"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_SILVER, 1, 2000)
         .result(IRItems.SILVER_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_silver"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_TIN, 1, 1000)
         .result(IRItems.TIN_DUST)
         .result(IRItems.SMALL_IRON_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_tin"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_TITANIUM, 1, 2000)
         .result(IRItems.TITANIUM_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_titanium"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_TOXIC, 1, 2000)
         .result(IRItems.TOXIC_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_toxic"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_TUNGSTEN, 1, 2000)
         .result(IRItems.TUNGSTEN_DUST)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_tungsten"));
      CentrifugeBuilder.centrifuge(IRItems.CRUSHED_URANIUM, 1, 3000)
         .result(IRItems.SMALL_URANIUM_235)
         .result(IRItems.URANIUM_238, 4)
         .result(IRItems.STONE_DUST)
         .save(consumer, IR.location("centrifuge/crushed_uranium"));
      CentrifugeBuilder.centrifuge(IRItems.DEPLETED_DUAL_MOX_FUEL_ROD, 1, 5000)
         .result(IRItems.SMALL_PLUTONIUM, 2)
         .result(IRItems.PLUTONIUM, 6)
         .result(IRItems.IRON_DUST, 3)
         .save(consumer, IR.location("centrifuge/depleted_dual_mox_fuel_rod"));
      CentrifugeBuilder.centrifuge(IRItems.DEPLETED_DUAL_URANIUM_FUEL_ROD, 1, 4000)
         .result(IRItems.SMALL_PLUTONIUM, 2)
         .result(IRItems.URANIUM_238, 8)
         .result(IRItems.IRON_DUST, 3)
         .save(consumer, IR.location("centrifuge/depleted_dual_uranium_fuel_rod"));
      CentrifugeBuilder.centrifuge(IRItems.DEPLETED_MOX_FUEL_ROD, 1, 5000)
         .result(IRItems.SMALL_PLUTONIUM)
         .result(IRItems.PLUTONIUM, 3)
         .result(IRItems.IRON_DUST)
         .save(consumer, IR.location("centrifuge/depleted_mox_fuel_rod"));
      CentrifugeBuilder.centrifuge(IRItems.DEPLETED_QUAD_MOX_FUEL_ROD, 1, 5000)
         .result(IRItems.SMALL_PLUTONIUM, 4)
         .result(IRItems.PLUTONIUM, 12)
         .result(IRItems.IRON_DUST, 7)
         .save(consumer, IR.location("centrifuge/depleted_quad_mox_fuel_rod"));
      CentrifugeBuilder.centrifuge(IRItems.DEPLETED_QUAD_URANIUM_FUEL_ROD, 1, 4000)
         .result(IRItems.SMALL_PLUTONIUM, 4)
         .result(IRItems.URANIUM_238, 16)
         .result(IRItems.IRON_DUST, 7)
         .save(consumer, IR.location("centrifuge/depleted_quad_uranium_fuel_rod"));
      CentrifugeBuilder.centrifuge(IRItems.DEPLETED_URANIUM_FUEL_ROD, 1, 4000)
         .result(IRItems.SMALL_PLUTONIUM)
         .result(IRItems.URANIUM_238, 4)
         .result(IRItems.IRON_DUST)
         .save(consumer, IR.location("centrifuge/depleted_uranium_fuel_rod"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_COPPER, 1, 500)
         .result(IRItems.COPPER_DUST)
         .result(IRItems.SMALL_TIN_DUST)
         .save(consumer, IR.location("centrifuge/purified_copper"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_GOLD, 1, 2000)
         .result(IRItems.GOLD_DUST)
         .result(IRItems.SMALL_SILVER_DUST)
         .save(consumer, IR.location("centrifuge/purified_gold"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_IRON, 1, 1500)
         .result(IRItems.IRON_DUST)
         .result(IRItems.SMALL_GOLD_DUST)
         .save(consumer, IR.location("centrifuge/purified_iron"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_LEAD, 1, 2000)
         .result(IRItems.LEAD_DUST)
         .result(IRItems.SMALL_COPPER_DUST)
         .save(consumer, IR.location("centrifuge/purified_lead"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_NICKEL, 1, 2000)
         .result(IRItems.NICKEL_DUST)
         .result(IRItems.SMALL_COPPER_DUST)
         .save(consumer, IR.location("centrifuge/purified_nickel"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_SILVER, 1, 2000)
         .result(IRItems.SILVER_DUST)
         .result(IRItems.SMALL_LEAD_DUST)
         .save(consumer, IR.location("centrifuge/purified_silver"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_TIN, 1, 1000)
         .result(IRItems.TIN_DUST)
         .result(IRItems.SMALL_IRON_DUST)
         .save(consumer, IR.location("centrifuge/purified_tin"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_TITANIUM, 1, 2000)
         .result(IRItems.TITANIUM_DUST)
         .save(consumer, IR.location("centrifuge/purified_titanium"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_TOXIC, 1, 2000).result(IRItems.TOXIC_DUST).save(consumer, IR.location("centrifuge/purified_toxic"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_TUNGSTEN, 1, 2000)
         .result(IRItems.TUNGSTEN_DUST)
         .save(consumer, IR.location("centrifuge/purified_tungsten"));
      CentrifugeBuilder.centrifuge(IRItems.PURIFIED_URANIUM, 1, 3000)
         .result(IRItems.SMALL_URANIUM_235)
         .result(IRItems.URANIUM_238, 6)
         .save(consumer, IR.location("centrifuge/purified_uranium"));
      CentrifugeBuilder.centrifuge(Items.QUARTZ, 2, 500).result(IRItems.SMALL_LITHIUM_DUST).save(consumer, IR.location("centrifuge/quartz"));
      CentrifugeBuilder.centrifuge(IRItems.RTG_PELLET, 1, 5000)
         .result(IRItems.PLUTONIUM, 3)
         .result(IRItems.IRON_DUST, 54)
         .save(consumer, IR.location("centrifuge/rtg_pellet"));
      CentrifugeBuilder.centrifuge(IRItems.SLAG, 1, 1500)
         .result(IRItems.SMALL_GOLD_DUST)
         .result(IRItems.COAL_DUST, 5)
         .save(consumer, IR.location("centrifuge/slag"));
      CentrifugeBuilder.centrifuge(IRItems.URANIUM, 20, 4000)
         .result(IRItems.URANIUM_238, 64)
         .result(IRItems.URANIUM_238, 48)
         .result(IRItems.URANIUM_235, 7)
         .save(consumer, IR.location("centrifuge/uranium"));
   }
}
