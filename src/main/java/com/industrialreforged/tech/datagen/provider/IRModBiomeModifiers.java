package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.core.IR;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class IRModBiomeModifiers {
   public static void bootstrap(BootstrapContext<BiomeModifier> context) {
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      HolderSet<Biome> overworld = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
      context.register(createKey("tin_ore"), createOre(overworld, IRPlacedProvider.TIN_ORE));
      context.register(createKey("lead_ore"), createOre(overworld, IRPlacedProvider.LEAD_ORE));
      context.register(createKey("nickel_ore"), createOre(overworld, IRPlacedProvider.NICKEL_ORE));
      context.register(createKey("silver_ore"), createOre(overworld, IRPlacedProvider.SILVER_ORE));
      context.register(createKey("uranium_ore"), createOre(overworld, IRPlacedProvider.URANIUM_ORE));
      context.register(createKey("titanium_ore"), createOre(overworld, IRPlacedProvider.TITANIUM_ORE));
      context.register(createKey("tungsten_ore"), createOre(overworld, IRPlacedProvider.TUNGSTEN_ORE));
      context.register(createKey("toxic_ore"), createOre(biomes.getOrThrow(BiomeTags.IS_NETHER), IRPlacedProvider.TOXIC_ORE));
      context.register(
         createKey("rubber_tree"),
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_FOREST), HolderSet.direct(new Holder[]{IRPlacedProvider.RUBBER_TREE}), Decoration.VEGETAL_DECORATION
         )
      );
   }

   private static ResourceKey<BiomeModifier> createKey(String name) {
      return ResourceKey.create(Keys.BIOME_MODIFIERS, IR.location(name));
   }

   private static BiomeModifier createOre(HolderSet<Biome> biome, Holder<PlacedFeature> placed) {
      return new AddFeaturesBiomeModifier(biome, HolderSet.direct(new Holder[]{placed}), Decoration.UNDERGROUND_ORES);
   }
}
