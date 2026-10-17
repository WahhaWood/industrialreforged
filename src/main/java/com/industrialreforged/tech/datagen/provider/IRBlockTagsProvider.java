package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.core.ref.IRBlockTags;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IRBlockTagsProvider extends BlockTagsProvider {
   public IRBlockTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "industrialreforged", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.add(
         BlockTags.MINEABLE_WITH_AXE,
         IRBlocks.STRIPPED_RUBBER_WOOD,
         IRBlocks.RUBBER_LOG,
         IRBlocks.RUBBER_WOOD,
         IRBlocks.WOODEN_SCAFFOLD,
         IRBlocks.REINFORCED_WOODEN_SCAFFOLD
      );
      this.add(
         BlockTags.MINEABLE_WITH_PICKAXE,
         IRBlocks.BASALT,
         IRBlocks.TIN_ORE,
         IRBlocks.DEEPSLATE_TIN_ORE,
         IRBlocks.TIN_BLOCK,
         IRBlocks.LEAD_ORE,
         IRBlocks.DEEPSLATE_LEAD_ORE,
         IRBlocks.LEAD_BLOCK,
         IRBlocks.NICKEL_ORE,
         IRBlocks.DEEPSLATE_NICKEL_ORE,
         IRBlocks.NICKEL_BLOCK,
         IRBlocks.SILVER_ORE,
         IRBlocks.DEEPSLATE_SILVER_ORE,
         IRBlocks.SILVER_BLOCK,
         IRBlocks.URANIUM_ORE,
         IRBlocks.DEEPSLATE_URANIUM_ORE,
         IRBlocks.URANIUM_BLOCK,
         IRBlocks.TITANIUM_ORE,
         IRBlocks.DEEPSLATE_TITANIUM_ORE,
         IRBlocks.TUNGSTEN_ORE,
         IRBlocks.DEEPSLATE_TUNGSTEN_ORE,
         IRBlocks.TOXIC_ORE,
         IRBlocks.REINFORCED_STONE,
         IRBlocks.MACHINE,
         IRBlocks.ADVANCED_MACHINE,
         IRBlocks.REACTOR_VESSEL,
         IRBlocks.IRON_SCAFFOLD,
         IRBlocks.REINFORCED_IRON_SCAFFOLD,
         IRBlocks.REINFORCED_GLASS,
         IRBlocks.IRON_FENCE,
         IRBlocks.GENERATOR,
         IRBlocks.STIRLING_GENERATOR,
         IRBlocks.GEO_GENERATOR,
         IRBlocks.SEMIFLUID_GENERATOR,
         IRBlocks.KINETIC_GENERATOR,
         IRBlocks.RT_GENERATOR,
         IRBlocks.SOLAR_GENERATOR_1_LEVEL,
         IRBlocks.SOLAR_GENERATOR_2_LEVEL,
         IRBlocks.SOLAR_GENERATOR_3_LEVEL,
         IRBlocks.SOLAR_GENERATOR_4_LEVEL,
         IRBlocks.SOLAR_GENERATOR_5_LEVEL,
         IRBlocks.SOLAR_GENERATOR_6_LEVEL,
         IRBlocks.SOLAR_GENERATOR_7_LEVEL,
         IRBlocks.SOLAR_GENERATOR_8_LEVEL,
         IRBlocks.SOLAR_GENERATOR_9_LEVEL,
         IRBlocks.SOLAR_GENERATOR_10_LEVEL,
         IRBlocks.SOLAR_GENERATOR_11_LEVEL,
         IRBlocks.SOLAR_GENERATOR_12_LEVEL,
         IRBlocks.SOLAR_GENERATOR_13_LEVEL,
         IRBlocks.SOLAR_GENERATOR_14_LEVEL,
         IRBlocks.SOLAR_GENERATOR_15_LEVEL,
         IRBlocks.SOLAR_CONNECTOR,
         IRBlocks.NUCLEAR_REACTOR,
         IRBlocks.REACTOR_CHAMBER,
         IRBlocks.ELECTRIC_KINETIC_GENERATOR,
         IRBlocks.MANUAL_KINETIC_GENERATOR,
         IRBlocks.STEAM_KINETIC_GENERATOR,
         IRBlocks.STIRLING_KINETIC_GENERATOR,
         IRBlocks.WATER_KINETIC_GENERATOR,
         IRBlocks.WIND_KINETIC_GENERATOR,
         IRBlocks.ELECTRIC_HEAT_GENERATOR,
         IRBlocks.FLUID_HEAT_GENERATOR,
         IRBlocks.RT_HEAT_GENERATOR,
         IRBlocks.SOLID_HEAT_GENERATOR,
         IRBlocks.ELECTRIC_FURNACE_1_LEVEL,
         IRBlocks.ELECTRIC_FURNACE_2_LEVEL,
         IRBlocks.ELECTRIC_FURNACE_3_LEVEL,
         IRBlocks.ELECTRIC_FURNACE_4_LEVEL,
         IRBlocks.MACERATOR_1_LEVEL,
         IRBlocks.MACERATOR_2_LEVEL,
         IRBlocks.MACERATOR_3_LEVEL,
         IRBlocks.MACERATOR_4_LEVEL,
         IRBlocks.RECYCLER_1_LEVEL,
         IRBlocks.RECYCLER_2_LEVEL,
         IRBlocks.RECYCLER_3_LEVEL,
         IRBlocks.RECYCLER_4_LEVEL,
         IRBlocks.EXTRACTOR_1_LEVEL,
         IRBlocks.EXTRACTOR_2_LEVEL,
         IRBlocks.EXTRACTOR_3_LEVEL,
         IRBlocks.EXTRACTOR_4_LEVEL,
         IRBlocks.COMPRESSOR_1_LEVEL,
         IRBlocks.COMPRESSOR_2_LEVEL,
         IRBlocks.COMPRESSOR_3_LEVEL,
         IRBlocks.COMPRESSOR_4_LEVEL,
         IRBlocks.METAL_FORMER_1_LEVEL,
         IRBlocks.METAL_FORMER_2_LEVEL,
         IRBlocks.METAL_FORMER_3_LEVEL,
         IRBlocks.METAL_FORMER_4_LEVEL,
         IRBlocks.ORE_WASHING_PLANT,
         IRBlocks.CANNER,
         IRBlocks.SOLID_CANNER,
         IRBlocks.TURNING,
         IRBlocks.INDUCTION_FURNACE,
         IRBlocks.CENTRIFUGE,
         IRBlocks.BLAST_FURNACE,
         IRBlocks.BLOCK_CUTTER,
         IRBlocks.TELEPORTER,
         IRBlocks.MATTER_GENERATOR,
         IRBlocks.REPLICATOR,
         IRBlocks.UU_SCANNER,
         IRBlocks.PATTERN_STORAGE,
         IRBlocks.MOLECULAR_FARMING,
         IRBlocks.MOLECULAR_TRANSFORMER,
         IRBlocks.PUMP,
         IRBlocks.FLUID_BOTTLER,
         IRBlocks.SOLAR_DISTILLER,
         IRBlocks.CONDENSER,
         IRBlocks.LIQUID_HEAT_EXCHANGER,
         IRBlocks.CROPMATRON,
         IRBlocks.CROP_HARVESTER,
         IRBlocks.FERMENTER,
         IRBlocks.BATBOX,
         IRBlocks.CESU,
         IRBlocks.MFE,
         IRBlocks.MFSU,
         IRBlocks.EFSU,
         IRBlocks.BATBOX_CHARGEPAD,
         IRBlocks.CESU_CHARGEPAD,
         IRBlocks.MFE_CHARGEPAD,
         IRBlocks.MFSU_CHARGEPAD,
         IRBlocks.EFSU_CHARGEPAD,
         IRBlocks.LUMINATOR,
         IRBlocks.BRONZE_BLOCK,
         IRBlocks.STEEL_BLOCK
      );
      this.add(
         BlockTags.CLIMBABLE, IRBlocks.WOODEN_SCAFFOLD, IRBlocks.REINFORCED_WOODEN_SCAFFOLD, IRBlocks.IRON_SCAFFOLD, IRBlocks.REINFORCED_IRON_SCAFFOLD
      );
      this.add(BlockTags.FENCES, IRBlocks.IRON_FENCE);
      this.add(BlockTags.LEAVES, IRBlocks.RUBBER_LEAVES);
      this.add(BlockTags.LOGS_THAT_BURN, IRBlocks.RUBBER_LOG);
      this.add(
         BlockTags.NEEDS_DIAMOND_TOOL,
         IRBlocks.URANIUM_BLOCK,
         IRBlocks.DEEPSLATE_URANIUM_ORE,
         IRBlocks.TITANIUM_ORE,
         IRBlocks.DEEPSLATE_TITANIUM_ORE,
         IRBlocks.TUNGSTEN_ORE,
         IRBlocks.DEEPSLATE_TUNGSTEN_ORE,
         IRBlocks.TOXIC_ORE,
         IRBlocks.TELEPORTER,
         IRBlocks.MATTER_GENERATOR,
         IRBlocks.REPLICATOR,
         IRBlocks.UU_SCANNER,
         IRBlocks.PATTERN_STORAGE,
         IRBlocks.MOLECULAR_FARMING,
         IRBlocks.MOLECULAR_TRANSFORMER,
         IRBlocks.CENTRIFUGE,
         IRBlocks.NUCLEAR_REACTOR,
         IRBlocks.STEEL_BLOCK
      );
      this.add(
         BlockTags.NEEDS_IRON_TOOL,
         IRBlocks.URANIUM_ORE,
         IRBlocks.NICKEL_ORE,
         IRBlocks.DEEPSLATE_NICKEL_ORE,
         IRBlocks.NICKEL_BLOCK,
         IRBlocks.SILVER_ORE,
         IRBlocks.DEEPSLATE_SILVER_ORE,
         IRBlocks.SILVER_BLOCK,
         IRBlocks.IRON_FENCE,
         IRBlocks.GENERATOR,
         IRBlocks.STIRLING_GENERATOR,
         IRBlocks.GEO_GENERATOR,
         IRBlocks.SEMIFLUID_GENERATOR,
         IRBlocks.KINETIC_GENERATOR,
         IRBlocks.RT_GENERATOR,
         IRBlocks.SOLAR_GENERATOR_1_LEVEL,
         IRBlocks.SOLAR_GENERATOR_2_LEVEL,
         IRBlocks.SOLAR_GENERATOR_3_LEVEL,
         IRBlocks.SOLAR_GENERATOR_4_LEVEL,
         IRBlocks.SOLAR_GENERATOR_5_LEVEL,
         IRBlocks.SOLAR_GENERATOR_6_LEVEL,
         IRBlocks.SOLAR_GENERATOR_7_LEVEL,
         IRBlocks.SOLAR_GENERATOR_8_LEVEL,
         IRBlocks.SOLAR_GENERATOR_9_LEVEL,
         IRBlocks.SOLAR_GENERATOR_10_LEVEL,
         IRBlocks.SOLAR_GENERATOR_11_LEVEL,
         IRBlocks.SOLAR_GENERATOR_12_LEVEL,
         IRBlocks.SOLAR_GENERATOR_13_LEVEL,
         IRBlocks.SOLAR_GENERATOR_14_LEVEL,
         IRBlocks.SOLAR_GENERATOR_15_LEVEL,
         IRBlocks.SOLAR_CONNECTOR,
         IRBlocks.REACTOR_CHAMBER,
         IRBlocks.ELECTRIC_KINETIC_GENERATOR,
         IRBlocks.MANUAL_KINETIC_GENERATOR,
         IRBlocks.STEAM_KINETIC_GENERATOR,
         IRBlocks.STIRLING_KINETIC_GENERATOR,
         IRBlocks.WATER_KINETIC_GENERATOR,
         IRBlocks.WIND_KINETIC_GENERATOR,
         IRBlocks.ELECTRIC_HEAT_GENERATOR,
         IRBlocks.FLUID_HEAT_GENERATOR,
         IRBlocks.RT_HEAT_GENERATOR,
         IRBlocks.SOLID_HEAT_GENERATOR,
         IRBlocks.ELECTRIC_FURNACE_1_LEVEL,
         IRBlocks.ELECTRIC_FURNACE_2_LEVEL,
         IRBlocks.ELECTRIC_FURNACE_3_LEVEL,
         IRBlocks.ELECTRIC_FURNACE_4_LEVEL,
         IRBlocks.MACERATOR_1_LEVEL,
         IRBlocks.MACERATOR_2_LEVEL,
         IRBlocks.MACERATOR_3_LEVEL,
         IRBlocks.MACERATOR_4_LEVEL,
         IRBlocks.RECYCLER_1_LEVEL,
         IRBlocks.RECYCLER_2_LEVEL,
         IRBlocks.RECYCLER_3_LEVEL,
         IRBlocks.RECYCLER_4_LEVEL,
         IRBlocks.EXTRACTOR_1_LEVEL,
         IRBlocks.EXTRACTOR_2_LEVEL,
         IRBlocks.EXTRACTOR_3_LEVEL,
         IRBlocks.EXTRACTOR_4_LEVEL,
         IRBlocks.COMPRESSOR_1_LEVEL,
         IRBlocks.COMPRESSOR_2_LEVEL,
         IRBlocks.COMPRESSOR_3_LEVEL,
         IRBlocks.COMPRESSOR_4_LEVEL,
         IRBlocks.METAL_FORMER_1_LEVEL,
         IRBlocks.METAL_FORMER_2_LEVEL,
         IRBlocks.METAL_FORMER_3_LEVEL,
         IRBlocks.METAL_FORMER_4_LEVEL,
         IRBlocks.ORE_WASHING_PLANT,
         IRBlocks.CANNER,
         IRBlocks.SOLID_CANNER,
         IRBlocks.TURNING,
         IRBlocks.INDUCTION_FURNACE,
         IRBlocks.BLAST_FURNACE,
         IRBlocks.BLOCK_CUTTER,
         IRBlocks.PUMP,
         IRBlocks.FLUID_BOTTLER,
         IRBlocks.SOLAR_DISTILLER,
         IRBlocks.CONDENSER,
         IRBlocks.LIQUID_HEAT_EXCHANGER,
         IRBlocks.CROPMATRON,
         IRBlocks.CROP_HARVESTER,
         IRBlocks.FERMENTER,
         IRBlocks.BATBOX,
         IRBlocks.CESU,
         IRBlocks.MFE,
         IRBlocks.MFSU,
         IRBlocks.EFSU,
         IRBlocks.BATBOX_CHARGEPAD,
         IRBlocks.CESU_CHARGEPAD,
         IRBlocks.MFE_CHARGEPAD,
         IRBlocks.MFSU_CHARGEPAD,
         IRBlocks.EFSU_CHARGEPAD,
         IRBlocks.LUMINATOR
      );
      this.add(
         BlockTags.NEEDS_STONE_TOOL,
         IRBlocks.REINFORCED_STONE,
         IRBlocks.TIN_ORE,
         IRBlocks.DEEPSLATE_TIN_ORE,
         IRBlocks.TIN_BLOCK,
         IRBlocks.LEAD_ORE,
         IRBlocks.DEEPSLATE_LEAD_ORE,
         IRBlocks.LEAD_BLOCK,
         IRBlocks.BRONZE_BLOCK
      );
      this.add(IRBlockTags.ORES_LEAD, IRBlocks.LEAD_ORE, IRBlocks.DEEPSLATE_LEAD_ORE);
      this.add(IRBlockTags.ORES_NICKEL, IRBlocks.NICKEL_ORE, IRBlocks.DEEPSLATE_NICKEL_ORE);
      this.add(IRBlockTags.ORES_SILVER, IRBlocks.SILVER_ORE, IRBlocks.DEEPSLATE_SILVER_ORE);
      this.add(IRBlockTags.ORES_TIN, IRBlocks.TIN_ORE, IRBlocks.DEEPSLATE_TIN_ORE);
      this.add(IRBlockTags.ORES_TITANIUM, IRBlocks.TITANIUM_ORE, IRBlocks.DEEPSLATE_TITANIUM_ORE);
      this.add(IRBlockTags.ORES_TOXIC, IRBlocks.TOXIC_ORE);
      this.add(IRBlockTags.ORES_TUNGSTEN, IRBlocks.TUNGSTEN_ORE, IRBlocks.DEEPSLATE_TUNGSTEN_ORE);
      this.add(IRBlockTags.ORES_URANIUM, IRBlocks.URANIUM_ORE, IRBlocks.DEEPSLATE_URANIUM_ORE);
      this.tag(Blocks.ORES)
         .addTags(
            new TagKey[]{
               IRBlockTags.ORES_LEAD,
               IRBlockTags.ORES_NICKEL,
               IRBlockTags.ORES_SILVER,
               IRBlockTags.ORES_TIN,
               IRBlockTags.ORES_TITANIUM,
               IRBlockTags.ORES_TOXIC,
               IRBlockTags.ORES_TUNGSTEN,
               IRBlockTags.ORES_URANIUM
            }
         );
      this.add(IRBlockTags.STORAGE_BLOCKS_BRONZE, IRBlocks.BRONZE_BLOCK);
      this.add(IRBlockTags.STORAGE_BLOCKS_LEAD, IRBlocks.LEAD_BLOCK);
      this.add(IRBlockTags.STORAGE_BLOCKS_NICKEL, IRBlocks.NICKEL_BLOCK);
      this.add(IRBlockTags.STORAGE_BLOCKS_SILVER, IRBlocks.SILVER_BLOCK);
      this.add(IRBlockTags.STORAGE_BLOCKS_STEEL, IRBlocks.STEEL_BLOCK);
      this.add(IRBlockTags.STORAGE_BLOCKS_TIN, IRBlocks.TIN_BLOCK);
      this.add(IRBlockTags.STORAGE_BLOCKS_URANIUM, IRBlocks.URANIUM_BLOCK);
      this.tag(Blocks.STORAGE_BLOCKS)
         .addTags(
            new TagKey[]{
               IRBlockTags.STORAGE_BLOCKS_BRONZE,
               IRBlockTags.STORAGE_BLOCKS_LEAD,
               IRBlockTags.STORAGE_BLOCKS_NICKEL,
               IRBlockTags.STORAGE_BLOCKS_SILVER,
               IRBlockTags.STORAGE_BLOCKS_STEEL,
               IRBlockTags.STORAGE_BLOCKS_TIN,
               IRBlockTags.STORAGE_BLOCKS_URANIUM
            }
         );
      this.add(
         BlockTags.BEACON_BASE_BLOCKS,
         IRBlocks.BRONZE_BLOCK,
         IRBlocks.LEAD_BLOCK,
         IRBlocks.NICKEL_BLOCK,
         IRBlocks.SILVER_BLOCK,
         IRBlocks.STEEL_BLOCK,
         IRBlocks.TIN_BLOCK,
         IRBlocks.URANIUM_BLOCK
      );
   }

   @SafeVarargs
   private void add(TagKey<Block> key, Supplier<? extends Block>... suppliers) {
      this.tag(key).add(Arrays.stream(suppliers).map(Supplier::get).toArray(Block[]::new));
   }
}
