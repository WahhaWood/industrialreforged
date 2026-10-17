package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.block.RubberLogBlock;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class IRConfiguredProvider {
   public static Holder<ConfiguredFeature<?, ?>> TIN_ORE;
   public static Holder<ConfiguredFeature<?, ?>> LEAD_ORE;
   public static Holder<ConfiguredFeature<?, ?>> NICKEL_ORE;
   public static Holder<ConfiguredFeature<?, ?>> SILVER_ORE;
   public static Holder<ConfiguredFeature<?, ?>> URANIUM_ORE;
   public static Holder<ConfiguredFeature<?, ?>> TITANIUM_ORE;
   public static Holder<ConfiguredFeature<?, ?>> TUNGSTEN_ORE;
   public static Holder<ConfiguredFeature<?, ?>> TOXIC_ORE;
   public static Holder<ConfiguredFeature<?, ?>> RUBBER_TREE;

   public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
      TIN_ORE = ctx.register(createKey("tin_ore"), createOre(IRBlocks.TIN_ORE, IRBlocks.DEEPSLATE_TIN_ORE));
      LEAD_ORE = ctx.register(createKey("lead_ore"), createOre(IRBlocks.LEAD_ORE, IRBlocks.DEEPSLATE_LEAD_ORE));
      NICKEL_ORE = ctx.register(createKey("nickel_ore"), createOre(IRBlocks.NICKEL_ORE, IRBlocks.DEEPSLATE_NICKEL_ORE));
      SILVER_ORE = ctx.register(createKey("silver_ore"), createOre(IRBlocks.SILVER_ORE, IRBlocks.DEEPSLATE_SILVER_ORE));
      URANIUM_ORE = ctx.register(createKey("uranium_ore"), createOre(IRBlocks.URANIUM_ORE, IRBlocks.DEEPSLATE_URANIUM_ORE));
      TITANIUM_ORE = ctx.register(createKey("titanium_ore"), createOre(IRBlocks.TITANIUM_ORE, IRBlocks.DEEPSLATE_TITANIUM_ORE));
      TUNGSTEN_ORE = ctx.register(createKey("tungsten_ore"), createOre(IRBlocks.TUNGSTEN_ORE, IRBlocks.DEEPSLATE_TUNGSTEN_ORE));
      TOXIC_ORE = ctx.register(
         createKey("toxic_ore"),
         new ConfiguredFeature(
            Feature.ORE,
            new OreConfiguration(
               List.of(OreConfiguration.target(new TagMatchTest(BlockTags.NETHER_CARVER_REPLACEABLES), ((Block)IRBlocks.TOXIC_ORE.get()).defaultBlockState())),
               8
            )
         )
      );
      BlockState rubberLog = ((RubberLogBlock)IRBlocks.RUBBER_LOG.get()).defaultBlockState();
      RUBBER_TREE = ctx.register(
         createKey("rubber_tree"),
         new ConfiguredFeature(
            Feature.TREE,
            new TreeConfigurationBuilder(
                  new WeightedStateProvider(
                     SimpleWeightedRandomList.<BlockState>builder()
                        .add((BlockState)rubberLog.setValue(RubberLogBlock.STATE, RubberLogBlock.RubberWoodState.PLAIN), 16)
                        .add((BlockState)rubberLog.setValue(RubberLogBlock.STATE, RubberLogBlock.RubberWoodState.WET_NORTH), 2)
                        .add((BlockState)rubberLog.setValue(RubberLogBlock.STATE, RubberLogBlock.RubberWoodState.WET_EAST), 2)
                        .add((BlockState)rubberLog.setValue(RubberLogBlock.STATE, RubberLogBlock.RubberWoodState.WET_SOUTH), 2)
                        .add((BlockState)rubberLog.setValue(RubberLogBlock.STATE, RubberLogBlock.RubberWoodState.WET_WEST), 2)
                        .build()
                  ),
                  new StraightTrunkPlacer(4, 4, 0),
                  BlockStateProvider.simple((Block)IRBlocks.RUBBER_LEAVES.get()),
                  new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
                  new TwoLayersFeatureSize(1, 0, 1)
               )
               .dirt(BlockStateProvider.simple(Blocks.DIRT))
               .ignoreVines()
               .build()
         )
      );
   }

   private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
      return ResourceKey.create(Registries.CONFIGURED_FEATURE, IR.location(name));
   }

   private static ConfiguredFeature<?, ?> createOre(Supplier<Block> ore, Supplier<Block> deepslate) {
      return new ConfiguredFeature(
         Feature.ORE,
         new OreConfiguration(
            List.of(
               OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), ore.get().defaultBlockState()),
               OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), deepslate.get().defaultBlockState())
            ),
            8
         )
      );
   }
}
