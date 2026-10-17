package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

public class IRPlacedProvider {
   public static Holder<PlacedFeature> TIN_ORE;
   public static Holder<PlacedFeature> LEAD_ORE;
   public static Holder<PlacedFeature> NICKEL_ORE;
   public static Holder<PlacedFeature> SILVER_ORE;
   public static Holder<PlacedFeature> URANIUM_ORE;
   public static Holder<PlacedFeature> TITANIUM_ORE;
   public static Holder<PlacedFeature> TUNGSTEN_ORE;
   public static Holder<PlacedFeature> TOXIC_ORE;
   public static Holder<PlacedFeature> RUBBER_TREE;

   public static void bootstrap(BootstrapContext<PlacedFeature> ctx) {
      TIN_ORE = ctx.register(createKey("tin_ore"), createPlaced(IRConfiguredProvider.TIN_ORE, 6, false, -20, 60));
      LEAD_ORE = ctx.register(createKey("lead_ore"), createPlaced(IRConfiguredProvider.LEAD_ORE, 6, false, -60, 40));
      NICKEL_ORE = ctx.register(createKey("nickel_ore"), createPlaced(IRConfiguredProvider.NICKEL_ORE, 4, false, -40, 120));
      SILVER_ORE = ctx.register(createKey("silver_ore"), createPlaced(IRConfiguredProvider.SILVER_ORE, 4, false, -60, 40));
      URANIUM_ORE = ctx.register(createKey("uranium_ore"), createPlaced(IRConfiguredProvider.URANIUM_ORE, 3, true, -60, 64));
      TITANIUM_ORE = ctx.register(createKey("titanium_ore"), createPlaced(IRConfiguredProvider.TITANIUM_ORE, 4, true, -60, 24));
      TUNGSTEN_ORE = ctx.register(createKey("tungsten_ore"), createPlaced(IRConfiguredProvider.TUNGSTEN_ORE, 5, true, -60, 40));
      TOXIC_ORE = ctx.register(createKey("toxic_ore"), createPlaced(IRConfiguredProvider.TOXIC_ORE, 4, true, 2, 100));
      RUBBER_TREE = ctx.register(
         createKey("rubber_tree"),
         new PlacedFeature(
            IRConfiguredProvider.RUBBER_TREE,
            List.of(
               CountPlacement.of(new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(0), 19).add(ConstantInt.of(1), 1).build())),
               SurfaceWaterDepthFilter.forMaxDepth(0),
               HeightmapPlacement.onHeightmap(Types.OCEAN_FLOOR),
               BiomeFilter.biome(),
               BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(((Block)IRBlocks.RUBBER_SAPLING.get()).defaultBlockState(), Vec3i.ZERO))
            )
         )
      );
   }

   private static ResourceKey<PlacedFeature> createKey(String name) {
      return ResourceKey.create(Registries.PLACED_FEATURE, IR.location(name));
   }

   private static PlacedFeature createPlaced(Holder<ConfiguredFeature<?, ?>> holder, int count, boolean trapezoid, int min, int max) {
      VerticalAnchor minAnchor = VerticalAnchor.absolute(min);
      VerticalAnchor maxAnchor = VerticalAnchor.absolute(max);
      return new PlacedFeature(
         holder,
         List.of(
            CountPlacement.of(count),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            HeightRangePlacement.of((HeightProvider)(trapezoid ? TrapezoidHeight.of(minAnchor, maxAnchor) : UniformHeight.of(minAnchor, maxAnchor)))
         )
      );
   }
}
