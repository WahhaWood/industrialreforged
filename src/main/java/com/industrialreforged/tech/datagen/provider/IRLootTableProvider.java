package com.industrialreforged.tech.datagen.provider;

import com.google.common.collect.ImmutableList;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

public class IRLootTableProvider extends LootTableProvider {
   public IRLootTableProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, Collections.emptySet(), VanillaLootTableProvider.create(output, registries).getTables(), registries);
   }

   @NotNull
   public List<SubProviderEntry> getTables() {
      return ImmutableList.of(new SubProviderEntry(IRLootTableProvider.ModBlockLootTables::new, LootContextParamSets.BLOCK));
   }

   private static class ModBlockLootTables extends BlockLootSubProvider {
      public ModBlockLootTables(Provider registries) {
         super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
      }

      protected void generate() {
         this.dropSelf(IRBlocks.BASALT);
         this.dropOre(IRBlocks.TIN_ORE, IRBlocks.DEEPSLATE_TIN_ORE, IRItems.RAW_TIN);
         this.dropOre(IRBlocks.LEAD_ORE, IRBlocks.DEEPSLATE_LEAD_ORE, IRItems.RAW_LEAD);
         this.dropOre(IRBlocks.SILVER_ORE, IRBlocks.DEEPSLATE_SILVER_ORE, IRItems.RAW_SILVER);
         this.dropOre(IRBlocks.NICKEL_ORE, IRBlocks.DEEPSLATE_NICKEL_ORE, IRItems.RAW_NICKEL);
         this.dropOre(IRBlocks.URANIUM_ORE, IRBlocks.DEEPSLATE_URANIUM_ORE, IRItems.RAW_URANIUM);
         this.dropOre(IRBlocks.TITANIUM_ORE, IRBlocks.DEEPSLATE_TITANIUM_ORE, IRItems.RAW_TITANIUM);
         this.dropOre(IRBlocks.TUNGSTEN_ORE, IRBlocks.DEEPSLATE_TUNGSTEN_ORE, IRItems.RAW_TUNGSTEN);
         this.add((Block)IRBlocks.TOXIC_ORE.get(), this.createOreDrop((Block)IRBlocks.TOXIC_ORE.get(), (Item)IRItems.RAW_TOXIC.get()));
         this.dropSelf(IRBlocks.TIN_BLOCK);
         this.dropSelf(IRBlocks.LEAD_BLOCK);
         this.dropSelf(IRBlocks.SILVER_BLOCK);
         this.dropSelf(IRBlocks.NICKEL_BLOCK);
         this.dropSelf(IRBlocks.BRONZE_BLOCK);
         this.dropSelf(IRBlocks.STEEL_BLOCK);
         this.dropSelf(IRBlocks.URANIUM_BLOCK);
         this.dropSelf(IRBlocks.MACHINE);
         this.dropSelf(IRBlocks.ADVANCED_MACHINE);
         this.dropSelf(IRBlocks.IRIDIUM_MACHINE);
         this.dropSelf(IRBlocks.REACTOR_VESSEL);
         this.add(
            (Block)IRBlocks.RUBBER_LEAVES.get(),
            this.createLeavesDrops(
               (Block)IRBlocks.RUBBER_LEAVES.get(), (Block)IRBlocks.RUBBER_SAPLING.get(), new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F, 0.1F}
            )
         );
         this.dropSelf((Block)IRBlocks.RUBBER_LOG.get());
         this.dropSelf((Block)IRBlocks.STRIPPED_RUBBER_LOG.get());
         this.dropSelf((Block)IRBlocks.RUBBER_WOOD.get());
         this.dropSelf(IRBlocks.STRIPPED_RUBBER_WOOD);
         this.dropSelf(IRBlocks.RUBBER_SAPLING);
         this.dropSelf(IRBlocks.WOODEN_SCAFFOLD);
         this.dropSelf(IRBlocks.REINFORCED_WOODEN_SCAFFOLD);
         this.dropSelf(IRBlocks.IRON_SCAFFOLD);
         this.dropSelf(IRBlocks.REINFORCED_IRON_SCAFFOLD);
         this.dropSelf(IRBlocks.IRON_FENCE);
         this.dropSelf(IRBlocks.REINFORCED_GLASS);
         this.add((Block)IRBlocks.REINFORCED_DOOR.get(), this.createDoorTable((Block)IRBlocks.REINFORCED_DOOR.get()));
      }

      public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, Builder> output) {
         this.generate();
         this.map.forEach(output);
      }

      @NotNull
      protected Iterable<Block> getKnownBlocks() {
         return BuiltInRegistries.BLOCK
            .stream()
            .filter(block -> "industrialreforged".equals(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getNamespace()))
            .collect(Collectors.toSet());
      }

      private void dropOre(DeferredBlock<Block> ore, DeferredBlock<Block> deepslate, DeferredItem<Item> raw) {
         this.add((Block)ore.get(), this.createOreDrop((Block)ore.get(), (Item)raw.get()));
         this.add((Block)deepslate.get(), this.createOreDrop((Block)deepslate.get(), (Item)raw.get()));
      }

      private void dropSelf(DeferredBlock<Block> block) {
         this.dropSelf((Block)block.get());
      }
   }
}
