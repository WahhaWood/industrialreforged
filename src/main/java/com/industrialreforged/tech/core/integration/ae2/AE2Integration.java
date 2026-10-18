package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.AECapabilities;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.blockentity.AEBaseBlockEntity;
import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.menu.MenuBase;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * AE2 integration: the ME machines, the ME recycler and the ME matter generator. Everything with {@code appeng.*} types lives in
 * this package and is reached only through {@link #init}, which {@code IR} calls behind
 * {@code ModList.get().isLoaded("ae2")}. Do not add an "is AE2 loaded" helper here: loading this
 * class without AE2 is exactly what must never happen (1.0.8 crash, DEVNOTES 2026-10-11).
 */
public final class AE2Integration {
   private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(IR.MODID);
   private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IR.MODID);
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IR.MODID);
   private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, IR.MODID);
   private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, IR.MODID);

   /** Recipe id inside the definition of an ME machine pattern. */
   public static final Supplier<DataComponentType<ResourceLocation>> PATTERN_RECIPE = COMPONENTS.registerComponentType(
      "me_pattern_recipe", builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
   );
   /** Hidden item that only serves as the definition of ME machine patterns (not in any tab, no recipe). */
   public static final DeferredItem<Item> RECIPE_PATTERN = ITEMS.registerSimpleItem("me_recipe_pattern", new Item.Properties().stacksTo(1));

   public static final Map<MEMachineKind, DeferredBlock<MEMachineBlock>> MACHINE_BLOCKS = new EnumMap<>(MEMachineKind.class);
   public static final Map<MEMachineKind, DeferredItem<BlockItem>> MACHINE_ITEMS = new EnumMap<>(MEMachineKind.class);
   public static final Map<MEMachineKind, DeferredHolder<BlockEntityType<?>, BlockEntityType<MEMachineBlockEntity>>> MACHINE_TYPES = new EnumMap<>(
      MEMachineKind.class
   );

   public static final DeferredBlock<MEMachineBlock> RECYCLER_BLOCK = BLOCKS.register("me_recycler", () -> new MEMachineBlock(AE2Integration.RECYCLER_TYPE));
   public static final DeferredItem<BlockItem> RECYCLER_ITEM = ITEMS.registerSimpleBlockItem(RECYCLER_BLOCK);
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MERecyclerBlockEntity>> RECYCLER_TYPE = BLOCK_ENTITIES.register(
      "me_recycler",
      () -> BlockEntityType.Builder.of((pos, state) -> new MERecyclerBlockEntity(AE2Integration.RECYCLER_TYPE.get(), pos, state), RECYCLER_BLOCK.get())
         .build(null)
   );

   public static final DeferredBlock<MEMachineBlock> MATTER_BLOCK = BLOCKS.register("me_matter_generator", () -> new MEMachineBlock(AE2Integration.MATTER_TYPE));
   public static final DeferredItem<BlockItem> MATTER_ITEM = ITEMS.registerSimpleBlockItem(MATTER_BLOCK);
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MEMatterGeneratorBlockEntity>> MATTER_TYPE = BLOCK_ENTITIES.register(
      "me_matter_generator",
      () -> BlockEntityType.Builder.of((pos, state) -> new MEMatterGeneratorBlockEntity(AE2Integration.MATTER_TYPE.get(), pos, state), MATTER_BLOCK.get())
         .build(null)
   );

   public static final DeferredHolder<MenuType<?>, MenuType<MEMachineMenu>> ME_MACHINE_MENU = MENUS.register(
      "me_machine", () -> MenuBase.createType(MEMachineMenu::new)
   );
   public static final DeferredHolder<MenuType<?>, MenuType<MERecyclerMenu>> ME_RECYCLER_MENU = MENUS.register(
      "me_recycler", () -> MenuBase.createType(MERecyclerMenu::new)
   );

   public static final DeferredHolder<MenuType<?>, MenuType<MEMatterGeneratorMenu>> ME_MATTER_MENU = MENUS.register(
      "me_matter_generator", () -> MenuBase.createType(MEMatterGeneratorMenu::new)
   );

   static {
      for (MEMachineKind kind : MEMachineKind.values()) {
         DeferredBlock<MEMachineBlock> block = BLOCKS.register(kind.id, () -> new MEMachineBlock(MACHINE_TYPES.get(kind)));
         MACHINE_BLOCKS.put(kind, block);
         MACHINE_ITEMS.put(kind, ITEMS.registerSimpleBlockItem(block));
         MACHINE_TYPES.put(
            kind,
            BLOCK_ENTITIES.register(
               kind.id,
               () -> BlockEntityType.Builder.of((pos, state) -> new MEMachineBlockEntity(kind, MACHINE_TYPES.get(kind).get(), pos, state), block.get())
                  .build(null)
            )
         );
      }
   }

   private AE2Integration() {
   }

   public static void init(IEventBus modBus) {
      BLOCKS.register(modBus);
      ITEMS.register(modBus);
      BLOCK_ENTITIES.register(modBus);
      MENUS.register(modBus);
      COMPONENTS.register(modBus);
      modBus.addListener(AE2Integration::registerCapabilities);
      modBus.addListener(AE2Integration::commonSetup);
      NeoForge.EVENT_BUS.addListener(AE2Integration::onDatapackSync);
      IndustrialReforged.registerMessage(MEMachineTogglePayload.TYPE, MEMachineTogglePayload::write, MEMachineTogglePayload::new, PacketFlow.SERVERBOUND);
      if (FMLEnvironment.dist.isClient()) {
         AE2IntegrationClient.init(modBus);
      }
   }

   /** Creative tab entries (called from {@code IRCreativeTabs} only when the integration is active). */
   public static void tab(CreativeModeTab.Output output) {
      for (MEMachineKind kind : MEMachineKind.values()) {
         output.accept(MACHINE_ITEMS.get(kind).get());
      }

      output.accept(RECYCLER_ITEM.get());
      output.accept(MATTER_ITEM.get());
   }

   private static void registerCapabilities(RegisterCapabilitiesEvent event) {
      // AE2 finds grid nodes in the world through this capability: without it cables never connect
      for (MEMachineKind kind : MEMachineKind.values()) {
         event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, MACHINE_TYPES.get(kind).get(), (machine, context) -> machine);
      }

      event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, RECYCLER_TYPE.get(), (machine, context) -> machine);
      event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, MATTER_TYPE.get(), (machine, context) -> machine);

      // any energy cable can power an ME node: the FE goes into its AE buffer
      for (MEMachineKind kind : MEMachineKind.values()) {
         event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, MACHINE_TYPES.get(kind).get(), (machine, side) -> machine.feInput());
      }

      event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, RECYCLER_TYPE.get(), (machine, side) -> machine.feInput());
      event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, MATTER_TYPE.get(), (machine, side) -> machine.feInput());
   }

   private static void commonSetup(FMLCommonSetupEvent event) {
      PatternDetailsHelper.registerDecoder(new MEMachinePattern.Decoder());
      event.enqueueWork(() -> {
         // the grid node shows this item and the machine is named after it
         for (MEMachineKind kind : MEMachineKind.values()) {
            AEBaseBlockEntity.registerBlockEntityItem(MACHINE_TYPES.get(kind).get(), MACHINE_ITEMS.get(kind).get());
         }

         AEBaseBlockEntity.registerBlockEntityItem(RECYCLER_TYPE.get(), RECYCLER_ITEM.get());
         AEBaseBlockEntity.registerBlockEntityItem(MATTER_TYPE.get(), MATTER_ITEM.get());
      });
   }

   private static void onDatapackSync(OnDatapackSyncEvent event) {
      // null player = /reload: recipes may have changed, every machine re-offers its patterns
      if (event.getPlayer() == null) {
         MEMachineBlockEntity.refreshAll();
      }
   }
}
