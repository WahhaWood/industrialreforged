package com.industrialreforged.tech.core.integration.botania;

import appeng.api.AECapabilities;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.blockentity.AEBaseBlockEntity;
import com.industrialreforged.tech.core.IR;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry entries of the ME machines for Botania recipes (AE2 and Botania both loaded): one block, block item
 * and block entity type per {@link BotaniaMachineKind}, their shared menu, and the hidden item and data
 * component that give machine patterns an identity. Before Botania Reforged 1.2.0 these lived there under the
 * {@code botania} namespace; aliases keep blocks, queued jobs and patterns of old worlds.
 */
public final class BotaniaMachines {
   private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("industrialreforged");
   private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("industrialreforged");
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "industrialreforged");
   private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "industrialreforged");
   private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "industrialreforged");

   /** Recipe id inside the definition of a machine pattern. */
   public static final Supplier<DataComponentType<ResourceLocation>> PATTERN_RECIPE = COMPONENTS.registerComponentType(
      "botania_machine_pattern_recipe", builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
   );
   /** Hidden item that only serves as the definition of machine patterns (not in any tab, no recipe). */
   public static final DeferredItem<Item> RECIPE_PATTERN = ITEMS.registerSimpleItem("botania_machine_recipe_pattern", new Item.Properties().stacksTo(1));

   public static final Map<BotaniaMachineKind, DeferredBlock<BotaniaMachineBlock>> BLOCKS_BY_KIND = new EnumMap<>(BotaniaMachineKind.class);
   public static final Map<BotaniaMachineKind, DeferredItem<BlockItem>> ITEMS_BY_KIND = new EnumMap<>(BotaniaMachineKind.class);
   public static final Map<BotaniaMachineKind, DeferredHolder<BlockEntityType<?>, BlockEntityType<BotaniaMachineBlockEntity>>> TYPES_BY_KIND = new EnumMap<>(
      BotaniaMachineKind.class
   );

   public static final DeferredHolder<MenuType<?>, MenuType<BotaniaMachineMenu>> MACHINE_MENU = MENUS.register(
      "botania_me_machine", () -> IMenuTypeExtension.create(BotaniaMachineMenu::new)
   );

   static {
      for (BotaniaMachineKind kind : BotaniaMachineKind.values()) {
         DeferredBlock<BotaniaMachineBlock> block = BLOCKS.register(kind.id, () -> new BotaniaMachineBlock(TYPES_BY_KIND.get(kind)));
         BLOCKS_BY_KIND.put(kind, block);
         ITEMS_BY_KIND.put(
            kind, ITEMS.registerSimpleBlockItem(block, kind == BotaniaMachineKind.ALFHEIM_MARKET ? new Item.Properties().rarity(Rarity.UNCOMMON) : new Item.Properties())
         );
         TYPES_BY_KIND.put(
            kind,
            BLOCK_ENTITIES.register(
               kind.id,
               () -> BlockEntityType.Builder.of((pos, state) -> new BotaniaMachineBlockEntity(kind, TYPES_BY_KIND.get(kind).get(), pos, state), block.get())
                  .build(null)
            )
         );
         ResourceLocation old = ResourceLocation.fromNamespaceAndPath("botania", kind.id);
         BLOCKS.addAlias(old, IR.location(kind.id));
         ITEMS.addAlias(old, IR.location(kind.id));
         BLOCK_ENTITIES.addAlias(old, IR.location(kind.id));
      }

      ITEMS.addAlias(ResourceLocation.fromNamespaceAndPath("botania", "machine_recipe_pattern"), IR.location("botania_machine_recipe_pattern"));
      COMPONENTS.addAlias(ResourceLocation.fromNamespaceAndPath("botania", "machine_pattern_recipe"), IR.location("botania_machine_pattern_recipe"));
   }

   private BotaniaMachines() {
   }

   public static void init(IEventBus modBus) {
      BLOCKS.register(modBus);
      ITEMS.register(modBus);
      BLOCK_ENTITIES.register(modBus);
      MENUS.register(modBus);
      COMPONENTS.register(modBus);
      modBus.addListener(BotaniaMachines::registerCapabilities);
      modBus.addListener(BotaniaMachines::commonSetup);
      NeoForge.EVENT_BUS.addListener(BotaniaMachines::onDatapackSync);
      modBus.addListener((RegisterGameTestsEvent event) -> event.register(BotaniaMachineTest.class));
      if (FMLEnvironment.dist.isClient()) {
         BotaniaMachinesClient.init(modBus);
      }
   }

   /** Creative tab entries (everything but the hidden pattern definition). */
   public static void tab(CreativeModeTab.Output output) {
      for (DeferredItem<BlockItem> item : ITEMS_BY_KIND.values()) {
         output.accept(item.get());
      }
   }

   private static void registerCapabilities(RegisterCapabilitiesEvent event) {
      // AE2 finds grid nodes in the world through this capability: without it cables never connect
      for (BotaniaMachineKind kind : BotaniaMachineKind.values()) {
         event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, TYPES_BY_KIND.get(kind).get(), (machine, context) -> machine);
      }
   }

   private static void commonSetup(FMLCommonSetupEvent event) {
      PatternDetailsHelper.registerDecoder(new BotaniaMachinePattern.Decoder());
      event.enqueueWork(() -> {
         // the grid node shows this item and the machine is named after it
         for (BotaniaMachineKind kind : BotaniaMachineKind.values()) {
            AEBaseBlockEntity.registerBlockEntityItem(TYPES_BY_KIND.get(kind).get(), ITEMS_BY_KIND.get(kind).get());
         }
      });
   }

   private static void onDatapackSync(OnDatapackSyncEvent event) {
      // null player = /reload: recipes may have changed, every machine re-offers its patterns
      if (event.getPlayer() == null) {
         BotaniaMachineBlockEntity.refreshAll();
      }
   }
}
