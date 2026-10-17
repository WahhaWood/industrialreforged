package com.industrialreforged.tech.datagen;

import com.industrialreforged.tech.datagen.provider.IRBlockStateProvider;
import com.industrialreforged.tech.datagen.provider.IRBlockTagsProvider;
import com.industrialreforged.tech.datagen.provider.IRConfiguredProvider;
import com.industrialreforged.tech.datagen.provider.IRDamageTypeTagsProvider;
import com.industrialreforged.tech.datagen.provider.IRFluidTagsProvider;
import com.industrialreforged.tech.datagen.provider.IRItemTagsProvider;
import com.industrialreforged.tech.datagen.provider.IRLootTableProvider;
import com.industrialreforged.tech.datagen.provider.IRModBiomeModifiers;
import com.industrialreforged.tech.datagen.provider.IRPlacedProvider;
import com.industrialreforged.tech.datagen.provider.IRRecipeProvider;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

@EventBusSubscriber(modid = "industrialreforged")
public class IRDataGenerator {
   private static final RegistrySetBuilder WORLDGEN = new RegistrySetBuilder()
      .add(Registries.CONFIGURED_FEATURE, IRConfiguredProvider::bootstrap)
      .add(Registries.PLACED_FEATURE, IRPlacedProvider::bootstrap)
      .add(Keys.BIOME_MODIFIERS, IRModBiomeModifiers::bootstrap);

   @SubscribeEvent
   public static void onGatherData(GatherDataEvent event) {
      DataGenerator gen = event.getGenerator();
      PackOutput packOutput = gen.getPackOutput();
      CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
      ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
      IRBlockTagsProvider blockTags = new IRBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
      gen.addProvider(event.includeServer(), blockTags);
      gen.addProvider(event.includeServer(), new IRItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
      gen.addProvider(event.includeServer(), new IRDamageTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
      gen.addProvider(event.includeServer(), new IRFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
      DatapackBuiltinEntriesProvider datapackEntries = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, WORLDGEN, Set.of("industrialreforged"));
      gen.addProvider(event.includeServer(), datapackEntries);
      gen.addProvider(event.includeClient(), new IRBlockStateProvider(packOutput, existingFileHelper));
      gen.addProvider(event.includeServer(), new IRRecipeProvider(packOutput, lookupProvider));
      gen.addProvider(event.includeServer(), new IRLootTableProvider(packOutput, lookupProvider));
   }
}
