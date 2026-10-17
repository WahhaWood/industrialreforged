package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.api.item.ItemToolMaterials;
import com.industrialreforged.tech.blockentity.machine.Compressor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricBatBoxBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricCESUBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricEFSUBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricMFEBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricMFSUBlockEntity;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.crop.ItemCrop;
import com.industrialreforged.tech.item.ElectricProperties;
import com.industrialreforged.tech.item.ItemScrap;
import com.industrialreforged.tech.item.ElectricStorageBlockItem;
import com.industrialreforged.tech.item.ItemBarrel;
import com.industrialreforged.tech.item.ItemBattery;
import com.industrialreforged.tech.item.ItemBooze;
import com.industrialreforged.tech.item.ItemCell;
import com.industrialreforged.tech.item.ItemCropSeed;
import com.industrialreforged.tech.item.ItemCrystalMemory;
import com.industrialreforged.tech.item.ItemMug;
import com.industrialreforged.tech.item.ItemNuclearResource;
import com.industrialreforged.tech.item.ItemTerraWart;
import com.industrialreforged.tech.item.ItemTinCan;
import com.industrialreforged.tech.item.ItemWeedExCell;
import com.industrialreforged.tech.item.SolarPanelBlockItem;
import com.industrialreforged.tech.item.armor.IRArmorItem;
import com.industrialreforged.tech.item.armor.ItemArmorAdvancedNanoChestplate;
import com.industrialreforged.tech.item.armor.ItemArmorBatteryPack;
import com.industrialreforged.tech.item.armor.ItemArmorCFPack;
import com.industrialreforged.tech.item.armor.ItemArmorGraviChestaplate;
import com.industrialreforged.tech.item.armor.ItemArmorHazmat;
import com.industrialreforged.tech.item.armor.ItemArmorJetpack;
import com.industrialreforged.tech.item.armor.ItemArmorJetpackElectric;
import com.industrialreforged.tech.item.armor.ItemArmorNanoSuit;
import com.industrialreforged.tech.item.armor.ItemArmorNightvisionGoggles;
import com.industrialreforged.tech.item.armor.ItemArmorQuantumSuit;
import com.industrialreforged.tech.item.armor.ItemArmorSingularSuit;
import com.industrialreforged.tech.item.armor.ItemArmorSolarHelmet;
import com.industrialreforged.tech.item.armor.ItemArmorStaticBoots;
import com.industrialreforged.tech.item.reactor.ItemReactorCondensator;
import com.industrialreforged.tech.item.reactor.ItemReactorDepletedUranium;
import com.industrialreforged.tech.item.reactor.ItemReactorHeatStorage;
import com.industrialreforged.tech.item.reactor.ItemReactorHeatSwitch;
import com.industrialreforged.tech.item.reactor.ItemReactorHeatpack;
import com.industrialreforged.tech.item.reactor.ItemReactorIridiumReflector;
import com.industrialreforged.tech.item.reactor.ItemReactorLithiumCell;
import com.industrialreforged.tech.item.reactor.ItemReactorMOX;
import com.industrialreforged.tech.item.reactor.ItemReactorPlating;
import com.industrialreforged.tech.item.reactor.ItemReactorPlutonium;
import com.industrialreforged.tech.item.reactor.ItemReactorReflector;
import com.industrialreforged.tech.item.reactor.ItemReactorTritium;
import com.industrialreforged.tech.item.reactor.ItemReactorUranium;
import com.industrialreforged.tech.item.reactor.ItemReactorVent;
import com.industrialreforged.tech.item.reactor.ItemReactorVentSpread;
import com.industrialreforged.tech.item.resources.ItemSteamTurbine;
import com.industrialreforged.tech.item.resources.ItemWindRotor;
import com.industrialreforged.tech.item.solar.SolarPanelModuleItem;
import com.industrialreforged.tech.item.tool.IRAxe;
import com.industrialreforged.tech.item.tool.IRHoe;
import com.industrialreforged.tech.item.tool.IRPickaxe;
import com.industrialreforged.tech.item.tool.ItemBow;
import com.industrialreforged.tech.item.tool.ItemChainsaw;
import com.industrialreforged.tech.item.tool.ItemChainsawAdvanced;
import com.industrialreforged.tech.item.tool.ItemCropnalyzer;
import com.industrialreforged.tech.item.tool.ItemCuttingBlade;
import com.industrialreforged.tech.item.tool.ItemDrill;
import com.industrialreforged.tech.item.tool.ItemDrillAdvanced;
import com.industrialreforged.tech.item.tool.ItemDrillIridium;
import com.industrialreforged.tech.item.tool.ItemDrillUltimate;
import com.industrialreforged.tech.item.tool.ItemElectricTools;
import com.industrialreforged.tech.item.tool.ItemFilter;
import com.industrialreforged.tech.item.tool.ItemFrequencyTransmitter;
import com.industrialreforged.tech.item.tool.ItemGraviTool;
import com.industrialreforged.tech.item.tool.ItemObscurator;
import com.industrialreforged.tech.item.tool.ItemOreBreaker;
import com.industrialreforged.tech.item.tool.ItemQuantumQuarry;
import com.industrialreforged.tech.item.tool.ItemSaber;
import com.industrialreforged.tech.item.tool.ItemScanner;
import com.industrialreforged.tech.item.tool.ItemScannerAdv;
import com.industrialreforged.tech.item.tool.ItemSprayer;
import com.industrialreforged.tech.item.tool.ItemToolCrafting;
import com.industrialreforged.tech.item.tool.ItemToolCutter;
import com.industrialreforged.tech.item.tool.ItemToolHoeElectric;
import com.industrialreforged.tech.item.tool.ItemToolMiningLaser;
import com.industrialreforged.tech.item.tool.ItemToolPainter;
import com.industrialreforged.tech.item.tool.ItemToolWrench;
import com.industrialreforged.tech.item.tool.ItemToolWrenchElectric;
import com.industrialreforged.tech.item.tool.ItemTreetap;
import com.industrialreforged.tech.item.tool.ItemTreetapElectric;
import com.industrialreforged.tech.item.tool.ItemVajra;
import com.industrialreforged.tech.item.tool.ItemWeedingTrowel;
import com.industrialreforged.tech.item.tool.ItemWindMeter;
import com.industrialreforged.tech.item.turning.ItemTurningMold;
import com.industrialreforged.tech.item.turning.ItemTurningProduct;
import com.industrialreforged.tech.item.upgrade.ItemUpgradeMachine;
import com.industrialreforged.tech.item.upgrade.ItemUpgradeModule;
import com.industrialreforged.item.TickArmorItem;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public final class IRItems {
   public static final Items REGISTRY = DeferredRegister.createItems("industrialreforged");

   /**
    * One energy core per solar panel level.  The molecular transformer turns
    * cheap dust into these; they carry most of a panel's crafting cost, so a
    * copy of any panel equals roughly 30 hours of that panel's own output.
    */
   public static final java.util.Map<Integer, DeferredItem<Item>> SOLAR_CORES = new java.util.LinkedHashMap<>();

   static {
      for (int coreLevel = 1; coreLevel <= 15; coreLevel++) {
         final int level = coreLevel;
         SOLAR_CORES.put(
            level,
            REGISTRY.register("solar_core_" + level + "_level", () -> new Item(new Properties().stacksTo(16)))
         );
      }
   }
   public static final DeferredItem<Item> BREW_BARREL = REGISTRY.register("brew_barrel", ItemBarrel::new);
   public static final DeferredItem<Item> BASALT = REGISTRY.register("basalt", () -> new BlockItem((Block)IRBlocks.BASALT.get(), new Properties()));
   public static final DeferredItem<Item> HEAT_PIPE_1 = REGISTRY.register(
      "heat_pipe_1", () -> new com.industrialreforged.tech.item.ItemHeatPipe((com.industrialreforged.tech.block.heat.HeatPipeBlock)IRBlocks.HEAT_PIPE_1.get())
   );
   public static final DeferredItem<Item> HEAT_PIPE_2 = REGISTRY.register(
      "heat_pipe_2", () -> new com.industrialreforged.tech.item.ItemHeatPipe((com.industrialreforged.tech.block.heat.HeatPipeBlock)IRBlocks.HEAT_PIPE_2.get())
   );
   public static final DeferredItem<Item> HEAT_PIPE_3 = REGISTRY.register(
      "heat_pipe_3", () -> new com.industrialreforged.tech.item.ItemHeatPipe((com.industrialreforged.tech.block.heat.HeatPipeBlock)IRBlocks.HEAT_PIPE_3.get())
   );
   public static final DeferredItem<Item> TIN_ORE = REGISTRY.register("tin_ore", () -> new BlockItem((Block)IRBlocks.TIN_ORE.get(), new Properties()));
   public static final DeferredItem<Item> DEEPSLATE_TIN_ORE = REGISTRY.register(
      "deepslate_tin_ore", () -> new BlockItem((Block)IRBlocks.DEEPSLATE_TIN_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> LEAD_ORE = REGISTRY.register("lead_ore", () -> new BlockItem((Block)IRBlocks.LEAD_ORE.get(), new Properties()));
   public static final DeferredItem<Item> DEEPSLATE_LEAD_ORE = REGISTRY.register(
      "deepslate_lead_ore", () -> new BlockItem((Block)IRBlocks.DEEPSLATE_LEAD_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> NICKEL_ORE = REGISTRY.register("nickel_ore", () -> new BlockItem((Block)IRBlocks.NICKEL_ORE.get(), new Properties()));
   public static final DeferredItem<Item> DEEPSLATE_NICKEL_ORE = REGISTRY.register(
      "deepslate_nickel_ore", () -> new BlockItem((Block)IRBlocks.DEEPSLATE_NICKEL_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> SILVER_ORE = REGISTRY.register("silver_ore", () -> new BlockItem((Block)IRBlocks.SILVER_ORE.get(), new Properties()));
   public static final DeferredItem<Item> DEEPSLATE_SILVER_ORE = REGISTRY.register(
      "deepslate_silver_ore", () -> new BlockItem((Block)IRBlocks.DEEPSLATE_SILVER_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> URANIUM_ORE = REGISTRY.register(
      "uranium_ore", () -> new BlockItem((Block)IRBlocks.URANIUM_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_URANIUM_ORE = REGISTRY.register(
      "deepslate_uranium_ore", () -> new BlockItem((Block)IRBlocks.DEEPSLATE_URANIUM_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> TITANIUM_ORE = REGISTRY.register(
      "titanium_ore", () -> new BlockItem((Block)IRBlocks.TITANIUM_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_TITANIUM_ORE = REGISTRY.register(
      "deepslate_titanium_ore", () -> new BlockItem((Block)IRBlocks.DEEPSLATE_TITANIUM_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> TUNGSTEN_ORE = REGISTRY.register(
      "tungsten_ore", () -> new BlockItem((Block)IRBlocks.TUNGSTEN_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_TUNGSTEN_ORE = REGISTRY.register(
      "deepslate_tungsten_ore", () -> new BlockItem((Block)IRBlocks.DEEPSLATE_TUNGSTEN_ORE.get(), new Properties())
   );
   public static final DeferredItem<Item> TOXIC_ORE = REGISTRY.register("toxic_ore", () -> new BlockItem((Block)IRBlocks.TOXIC_ORE.get(), new Properties()));
   public static final DeferredItem<Item> URANIUM_BLOCK = REGISTRY.register(
      "uranium_block", () -> new BlockItem((Block)IRBlocks.URANIUM_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> TIN_BLOCK = REGISTRY.register("tin_block", () -> new BlockItem((Block)IRBlocks.TIN_BLOCK.get(), new Properties()));
   public static final DeferredItem<Item> LEAD_BLOCK = REGISTRY.register("lead_block", () -> new BlockItem((Block)IRBlocks.LEAD_BLOCK.get(), new Properties()));
   public static final DeferredItem<Item> NICKEL_BLOCK = REGISTRY.register(
      "nickel_block", () -> new BlockItem((Block)IRBlocks.NICKEL_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> SILVER_BLOCK = REGISTRY.register(
      "silver_block", () -> new BlockItem((Block)IRBlocks.SILVER_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> BRONZE_BLOCK = REGISTRY.register(
      "bronze_block", () -> new BlockItem((Block)IRBlocks.BRONZE_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> STEEL_BLOCK = REGISTRY.register(
      "steel_block", () -> new BlockItem((Block)IRBlocks.STEEL_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> REINFORCED_STONE = REGISTRY.register(
      "reinforced_stone", () -> new BlockItem((Block)IRBlocks.REINFORCED_STONE.get(), new Properties())
   );
   public static final DeferredItem<Item> TANK_WALL = REGISTRY.register(
      "tank_wall", () -> new BlockItem((Block)IRBlocks.TANK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> TANK_VALVE = REGISTRY.register(
      "tank_valve", com.industrialreforged.tech.item.ItemTankValve::new
   );
   public static final DeferredItem<Item> TANK_CONTROLLER = REGISTRY.register(
      "tank_controller", com.industrialreforged.tech.item.ItemTankController::new
   );
   public static final DeferredItem<Item> IRON_FURNACE = REGISTRY.register(
      "iron_furnace", () -> new BlockItem((Block)IRBlocks.IRON_FURNACE.get(), new Properties())
   );
   public static final DeferredItem<Item> MACHINE = REGISTRY.register("machine", () -> new BlockItem((Block)IRBlocks.MACHINE.get(), new Properties()));
   public static final DeferredItem<Item> ADVANCED_MACHINE = REGISTRY.register(
      "advanced_machine", () -> new BlockItem((Block)IRBlocks.ADVANCED_MACHINE.get(), new Properties())
   );
   public static final DeferredItem<Item> IRIDIUM_MACHINE = REGISTRY.register(
      "iridium_machine", () -> new BlockItem((Block)IRBlocks.IRIDIUM_MACHINE.get(), new Properties())
   );
   public static final DeferredItem<Item> REACTOR_VESSEL = REGISTRY.register(
      "reactor_vessel", () -> new BlockItem((Block)IRBlocks.REACTOR_VESSEL.get(), new Properties())
   );
   public static final DeferredItem<Item> RUBBER_LEAVES = REGISTRY.register(
      "rubber_leaves", () -> new BlockItem((Block)IRBlocks.RUBBER_LEAVES.get(), new Properties())
   );
   public static final DeferredItem<Item> RUBBER_LOG = REGISTRY.register("rubber_log", () -> new BlockItem((Block)IRBlocks.RUBBER_LOG.get(), new Properties()));
   public static final DeferredItem<Item> SPENT_RUBBER_LOG = REGISTRY.register(
      "spent_rubber_log", com.industrialreforged.tech.item.ItemSpentRubberLog::new
   );
   public static final DeferredItem<Item> STRIPPED_RUBBER_LOG = REGISTRY.register(
      "stripped_rubber_log", () -> new BlockItem((Block)IRBlocks.STRIPPED_RUBBER_LOG.get(), new Properties())
   );
   public static final DeferredItem<Item> RUBBER_WOOD = REGISTRY.register(
      "rubber_wood", () -> new BlockItem((Block)IRBlocks.RUBBER_WOOD.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_RUBBER_WOOD = REGISTRY.register(
      "stripped_rubber_wood", () -> new BlockItem((Block)IRBlocks.STRIPPED_RUBBER_WOOD.get(), new Properties())
   );
   public static final DeferredItem<Item> RUBBER_SAPLING = REGISTRY.register(
      "rubber_sapling", () -> new BlockItem((Block)IRBlocks.RUBBER_SAPLING.get(), new Properties())
   );
   public static final DeferredItem<Item> WOODEN_SCAFFOLD = REGISTRY.register(
      "wooden_scaffold", () -> new BlockItem((Block)IRBlocks.WOODEN_SCAFFOLD.get(), new Properties())
   );
   public static final DeferredItem<Item> REINFORCED_WOODEN_SCAFFOLD = REGISTRY.register(
      "reinforced_wooden_scaffold", () -> new BlockItem((Block)IRBlocks.REINFORCED_WOODEN_SCAFFOLD.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_SCAFFOLD = REGISTRY.register(
      "iron_scaffold", () -> new BlockItem((Block)IRBlocks.IRON_SCAFFOLD.get(), new Properties())
   );
   public static final DeferredItem<Item> REINFORCED_IRON_SCAFFOLD = REGISTRY.register(
      "reinforced_iron_scaffold", () -> new BlockItem((Block)IRBlocks.REINFORCED_IRON_SCAFFOLD.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_FENCE = REGISTRY.register("iron_fence", () -> new BlockItem((Block)IRBlocks.IRON_FENCE.get(), new Properties()));
   public static final DeferredItem<Item> REINFORCED_GLASS = REGISTRY.register(
      "reinforced_glass", () -> new BlockItem((Block)IRBlocks.REINFORCED_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> FOAM = REGISTRY.register("foam", () -> new BlockItem((Block)IRBlocks.FOAM.get(), new Properties()));
   public static final DeferredItem<Item> WHITE_WALL = REGISTRY.register("white_wall", () -> new BlockItem((Block)IRBlocks.WHITE_WALL.get(), new Properties()));
   public static final DeferredItem<Item> ORANGE_WALL = REGISTRY.register(
      "orange_wall", () -> new BlockItem((Block)IRBlocks.ORANGE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_WALL = REGISTRY.register(
      "magenta_wall", () -> new BlockItem((Block)IRBlocks.MAGENTA_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_WALL = REGISTRY.register(
      "light_blue_wall", () -> new BlockItem((Block)IRBlocks.LIGHT_BLUE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_WALL = REGISTRY.register(
      "yellow_wall", () -> new BlockItem((Block)IRBlocks.YELLOW_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_WALL = REGISTRY.register("lime_wall", () -> new BlockItem((Block)IRBlocks.LIME_WALL.get(), new Properties()));
   public static final DeferredItem<Item> PINK_WALL = REGISTRY.register("pink_wall", () -> new BlockItem((Block)IRBlocks.PINK_WALL.get(), new Properties()));
   public static final DeferredItem<Item> GRAY_WALL = REGISTRY.register("gray_wall", () -> new BlockItem((Block)IRBlocks.GRAY_WALL.get(), new Properties()));
   public static final DeferredItem<Item> LIGHT_GRAY_WALL = REGISTRY.register(
      "light_gray_wall", () -> new BlockItem((Block)IRBlocks.LIGHT_GRAY_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_WALL = REGISTRY.register("cyan_wall", () -> new BlockItem((Block)IRBlocks.CYAN_WALL.get(), new Properties()));
   public static final DeferredItem<Item> PURPLE_WALL = REGISTRY.register(
      "purple_wall", () -> new BlockItem((Block)IRBlocks.PURPLE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_WALL = REGISTRY.register("blue_wall", () -> new BlockItem((Block)IRBlocks.BLUE_WALL.get(), new Properties()));
   public static final DeferredItem<Item> BROWN_WALL = REGISTRY.register("brown_wall", () -> new BlockItem((Block)IRBlocks.BROWN_WALL.get(), new Properties()));
   public static final DeferredItem<Item> GREEN_WALL = REGISTRY.register("green_wall", () -> new BlockItem((Block)IRBlocks.GREEN_WALL.get(), new Properties()));
   public static final DeferredItem<Item> RED_WALL = REGISTRY.register("red_wall", () -> new BlockItem((Block)IRBlocks.RED_WALL.get(), new Properties()));
   public static final DeferredItem<Item> BLACK_WALL = REGISTRY.register("black_wall", () -> new BlockItem((Block)IRBlocks.BLACK_WALL.get(), new Properties()));
   public static final DeferredItem<Item> REINFORCED_DOOR = REGISTRY.register(
      "reinforced_door", () -> new BlockItem((Block)IRBlocks.REINFORCED_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> GENERATOR = REGISTRY.register("generator", () -> new BlockItem((Block)IRBlocks.GENERATOR.get(), new Properties()));
   public static final DeferredItem<Item> STIRLING_GENERATOR = REGISTRY.register(
      "stirling_generator", () -> new BlockItem((Block)IRBlocks.STIRLING_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> GEO_GENERATOR = REGISTRY.register(
      "geo_generator", () -> new BlockItem((Block)IRBlocks.GEO_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SEMIFLUID_GENERATOR = REGISTRY.register(
      "semifluid_generator", () -> new BlockItem((Block)IRBlocks.SEMIFLUID_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> KINETIC_GENERATOR = REGISTRY.register(
      "kinetic_generator", () -> new BlockItem((Block)IRBlocks.KINETIC_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> RT_GENERATOR = REGISTRY.register(
      "rt_generator", () -> new BlockItem((Block)IRBlocks.RT_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_1_LEVEL = REGISTRY.register(
      "solar_generator_1_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_1_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_2_LEVEL = REGISTRY.register(
      "solar_generator_2_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_2_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_3_LEVEL = REGISTRY.register(
      "solar_generator_3_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_3_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_4_LEVEL = REGISTRY.register(
      "solar_generator_4_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_4_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_5_LEVEL = REGISTRY.register(
      "solar_generator_5_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_5_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_6_LEVEL = REGISTRY.register(
      "solar_generator_6_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_6_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_7_LEVEL = REGISTRY.register(
      "solar_generator_7_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_7_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_8_LEVEL = REGISTRY.register(
      "solar_generator_8_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_8_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_9_LEVEL = REGISTRY.register(
      "solar_generator_9_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_9_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_10_LEVEL = REGISTRY.register(
      "solar_generator_10_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_10_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_11_LEVEL = REGISTRY.register(
      "solar_generator_11_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_11_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_12_LEVEL = REGISTRY.register(
      "solar_generator_12_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_12_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_13_LEVEL = REGISTRY.register(
      "solar_generator_13_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_13_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_14_LEVEL = REGISTRY.register(
      "solar_generator_14_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_14_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_GENERATOR_15_LEVEL = REGISTRY.register(
      "solar_generator_15_level", () -> new SolarPanelBlockItem((Block)IRBlocks.SOLAR_GENERATOR_15_LEVEL.get())
   );
   public static final DeferredItem<Item> SOLAR_CONNECTOR = REGISTRY.register(
      "solar_connector", () -> new BlockItem((Block)IRBlocks.SOLAR_CONNECTOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SOLAR_MODULE_DAY_1 = REGISTRY.register(
      "solar_module_day_1", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.DAY, 0)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_DAY_2 = REGISTRY.register(
      "solar_module_day_2", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.DAY, 1)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_NIGHT_1 = REGISTRY.register(
      "solar_module_night_1", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.NIGHT, 0)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_NIGHT_2 = REGISTRY.register(
      "solar_module_night_2", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.NIGHT, 1)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_STORAGE_1 = REGISTRY.register(
      "solar_module_storage_1", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.STORAGE, 0)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_STORAGE_2 = REGISTRY.register(
      "solar_module_storage_2", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.STORAGE, 1)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_OUTPUT_1 = REGISTRY.register(
      "solar_module_output_1", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.OUTPUT, 0)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_OUTPUT_2 = REGISTRY.register(
      "solar_module_output_2", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.OUTPUT, 1)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_SUN_LENS_1 = REGISTRY.register(
      "solar_module_sun_lens_1", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.SUN_LENS, 0)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_SUN_LENS_2 = REGISTRY.register(
      "solar_module_sun_lens_2", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.SUN_LENS, 1)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_MOON_LENS_1 = REGISTRY.register(
      "solar_module_moon_lens_1", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.MOON_LENS, 0)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_MOON_LENS_2 = REGISTRY.register(
      "solar_module_moon_lens_2", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.MOON_LENS, 1)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_RAIN_LENS_1 = REGISTRY.register(
      "solar_module_rain_lens_1", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.RAIN_LENS, 0)
   );
   public static final DeferredItem<Item> SOLAR_MODULE_RAIN_LENS_2 = REGISTRY.register(
      "solar_module_rain_lens_2", () -> new SolarPanelModuleItem(SolarPanelModuleItem.Type.RAIN_LENS, 1)
   );
   public static final DeferredItem<Item> NUCLEAR_REACTOR = REGISTRY.register(
      "nuclear_reactor", () -> new BlockItem((Block)IRBlocks.NUCLEAR_REACTOR.get(), new Properties().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> REACTOR_CHAMBER = REGISTRY.register(
      "reactor_chamber", () -> new com.industrialreforged.tech.item.ItemReactorChamber()
   );
   public static final DeferredItem<Item> ELECTRIC_KINETIC_GENERATOR = REGISTRY.register(
      "electric_kinetic_generator", () -> new BlockItem((Block)IRBlocks.ELECTRIC_KINETIC_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANUAL_KINETIC_GENERATOR = REGISTRY.register(
      "manual_kinetic_generator", () -> new BlockItem((Block)IRBlocks.MANUAL_KINETIC_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> STEAM_KINETIC_GENERATOR = REGISTRY.register(
      "steam_kinetic_generator", () -> new BlockItem((Block)IRBlocks.STEAM_KINETIC_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> STIRLING_KINETIC_GENERATOR = REGISTRY.register(
      "stirling_kinetic_generator", () -> new BlockItem((Block)IRBlocks.STIRLING_KINETIC_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WATER_KINETIC_GENERATOR = REGISTRY.register(
      "water_kinetic_generator", () -> new BlockItem((Block)IRBlocks.WATER_KINETIC_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WIND_KINETIC_GENERATOR = REGISTRY.register(
      "wind_kinetic_generator", () -> new BlockItem((Block)IRBlocks.WIND_KINETIC_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ELECTRIC_HEAT_GENERATOR = REGISTRY.register(
      "electric_heat_generator", () -> new BlockItem((Block)IRBlocks.ELECTRIC_HEAT_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> FLUID_HEAT_GENERATOR = REGISTRY.register(
      "fluid_heat_generator", () -> new BlockItem((Block)IRBlocks.FLUID_HEAT_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> RT_HEAT_GENERATOR = REGISTRY.register(
      "rt_heat_generator", () -> new BlockItem((Block)IRBlocks.RT_HEAT_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SOLID_HEAT_GENERATOR = REGISTRY.register(
      "solid_heat_generator", () -> new BlockItem((Block)IRBlocks.SOLID_HEAT_GENERATOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ELECTRIC_FURNACE_1_LEVEL = REGISTRY.register(
      "electric_furnace_1_level", () -> new BlockItem((Block)IRBlocks.ELECTRIC_FURNACE_1_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> ELECTRIC_FURNACE_2_LEVEL = REGISTRY.register(
      "electric_furnace_2_level", () -> new BlockItem((Block)IRBlocks.ELECTRIC_FURNACE_2_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> ELECTRIC_FURNACE_3_LEVEL = REGISTRY.register(
      "electric_furnace_3_level", () -> new BlockItem((Block)IRBlocks.ELECTRIC_FURNACE_3_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> ELECTRIC_FURNACE_4_LEVEL = REGISTRY.register(
      "electric_furnace_4_level", () -> new BlockItem((Block)IRBlocks.ELECTRIC_FURNACE_4_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> MACERATOR_1_LEVEL = REGISTRY.register(
      "macerator_1_level", () -> new BlockItem((Block)IRBlocks.MACERATOR_1_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> MACERATOR_2_LEVEL = REGISTRY.register(
      "macerator_2_level", () -> new BlockItem((Block)IRBlocks.MACERATOR_2_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> MACERATOR_3_LEVEL = REGISTRY.register(
      "macerator_3_level", () -> new BlockItem((Block)IRBlocks.MACERATOR_3_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> MACERATOR_4_LEVEL = REGISTRY.register(
      "macerator_4_level", () -> new BlockItem((Block)IRBlocks.MACERATOR_4_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> RECYCLER_1_LEVEL = REGISTRY.register(
      "recycler_1_level", () -> new BlockItem((Block)IRBlocks.RECYCLER_1_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> RECYCLER_2_LEVEL = REGISTRY.register(
      "recycler_2_level", () -> new BlockItem((Block)IRBlocks.RECYCLER_2_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> RECYCLER_3_LEVEL = REGISTRY.register(
      "recycler_3_level", () -> new BlockItem((Block)IRBlocks.RECYCLER_3_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> RECYCLER_4_LEVEL = REGISTRY.register(
      "recycler_4_level", () -> new BlockItem((Block)IRBlocks.RECYCLER_4_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> EXTRACTOR_1_LEVEL = REGISTRY.register(
      "extractor_1_level", () -> new BlockItem((Block)IRBlocks.EXTRACTOR_1_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> EXTRACTOR_2_LEVEL = REGISTRY.register(
      "extractor_2_level", () -> new BlockItem((Block)IRBlocks.EXTRACTOR_2_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> EXTRACTOR_3_LEVEL = REGISTRY.register(
      "extractor_3_level", () -> new BlockItem((Block)IRBlocks.EXTRACTOR_3_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> EXTRACTOR_4_LEVEL = REGISTRY.register(
      "extractor_4_level", () -> new BlockItem((Block)IRBlocks.EXTRACTOR_4_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> COMPRESSOR_1_LEVEL = REGISTRY.register(
      "compressor_1_level", () -> new BlockItem((Block)IRBlocks.COMPRESSOR_1_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> COMPRESSOR_2_LEVEL = REGISTRY.register(
      "compressor_2_level", () -> new BlockItem((Block)IRBlocks.COMPRESSOR_2_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> COMPRESSOR_3_LEVEL = REGISTRY.register(
      "compressor_3_level", () -> new BlockItem((Block)IRBlocks.COMPRESSOR_3_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> COMPRESSOR_4_LEVEL = REGISTRY.register(
      "compressor_4_level", () -> new BlockItem((Block)IRBlocks.COMPRESSOR_4_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_FORMER_1_LEVEL = REGISTRY.register(
      "metal_former_1_level", () -> new BlockItem((Block)IRBlocks.METAL_FORMER_1_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_FORMER_2_LEVEL = REGISTRY.register(
      "metal_former_2_level", () -> new BlockItem((Block)IRBlocks.METAL_FORMER_2_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_FORMER_3_LEVEL = REGISTRY.register(
      "metal_former_3_level", () -> new BlockItem((Block)IRBlocks.METAL_FORMER_3_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_FORMER_4_LEVEL = REGISTRY.register(
      "metal_former_4_level", () -> new BlockItem((Block)IRBlocks.METAL_FORMER_4_LEVEL.get(), new Properties())
   );
   public static final DeferredItem<Item> ORE_WASHING_PLANT = REGISTRY.register(
      "ore_washing_plant", () -> new BlockItem((Block)IRBlocks.ORE_WASHING_PLANT.get(), new Properties())
   );
   public static final DeferredItem<Item> CANNER = REGISTRY.register("canner", () -> new BlockItem((Block)IRBlocks.CANNER.get(), new Properties()));
   public static final DeferredItem<Item> SOLID_CANNER = REGISTRY.register(
      "solid_canner", () -> new BlockItem((Block)IRBlocks.SOLID_CANNER.get(), new Properties())
   );
   public static final DeferredItem<Item> TURNING = REGISTRY.register("turning", () -> new BlockItem((Block)IRBlocks.TURNING.get(), new Properties()));
   public static final DeferredItem<Item> INDUCTION_FURNACE = REGISTRY.register(
      "induction_furnace", () -> new BlockItem((Block)IRBlocks.INDUCTION_FURNACE.get(), new Properties().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> CENTRIFUGE = REGISTRY.register("centrifuge", () -> new BlockItem((Block)IRBlocks.CENTRIFUGE.get(), new Properties()));
   public static final DeferredItem<Item> BLAST_FURNACE = REGISTRY.register(
      "blast_furnace", () -> new com.industrialreforged.tech.item.ItemBlastFurnace((Block)IRBlocks.BLAST_FURNACE.get())
   );
   public static final DeferredItem<Item> BLOCK_CUTTER = REGISTRY.register(
      "block_cutter", () -> new BlockItem((Block)IRBlocks.BLOCK_CUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> TELEPORTER = REGISTRY.register(
      "teleporter", () -> new com.industrialreforged.tech.item.ItemTeleporter()
   );
   public static final DeferredItem<Item> MATTER_GENERATOR = REGISTRY.register(
      "matter_generator", () -> new BlockItem((Block)IRBlocks.MATTER_GENERATOR.get(), new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> REPLICATOR = REGISTRY.register(
      "replicator", () -> new BlockItem((Block)IRBlocks.REPLICATOR.get(), new Properties().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> UU_SCANNER = REGISTRY.register(
      "uu_scanner", () -> new BlockItem((Block)IRBlocks.UU_SCANNER.get(), new Properties().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> PATTERN_STORAGE = REGISTRY.register(
      "pattern_storage",
      () -> new BlockItem((Block)IRBlocks.PATTERN_STORAGE.get(), new Properties().component(IRDataComponents.PATTERNS, NonNullList.create()))
   );
   public static final DeferredItem<Item> MOLECULAR_FARMING = REGISTRY.register(
      "molecular_farming", () -> new BlockItem((Block)IRBlocks.MOLECULAR_FARMING.get(), new Properties())
   );
   public static final DeferredItem<Item> MOLECULAR_TRANSFORMER = REGISTRY.register(
      "molecular_transformer", () -> new BlockItem((Block)IRBlocks.MOLECULAR_TRANSFORMER.get(), new Properties())
   );
   public static final DeferredItem<Item> PUMP = REGISTRY.register("pump", () -> new BlockItem((Block)IRBlocks.PUMP.get(), new Properties()));
   public static final DeferredItem<Item> FLUID_BOTTLER = REGISTRY.register(
      "fluid_bottler", () -> new BlockItem((Block)IRBlocks.FLUID_BOTTLER.get(), new Properties())
   );
   public static final DeferredItem<Item> SOLAR_DISTILLER = REGISTRY.register(
      "solar_distiller", () -> new BlockItem((Block)IRBlocks.SOLAR_DISTILLER.get(), new Properties())
   );
   public static final DeferredItem<Item> CONDENSER = REGISTRY.register("condenser", () -> new BlockItem((Block)IRBlocks.CONDENSER.get(), new Properties()));
   public static final DeferredItem<Item> QUANTUM_QUARRY = REGISTRY.register(
      "quantum_quarry", () -> new ItemQuantumQuarry((Block)IRBlocks.QUANTUM_QUARRY.get(), new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> ITEM_FILTER = REGISTRY.register("item_filter", () -> new ItemFilter(new Properties().stacksTo(1)));
   public static final DeferredItem<Item> ORE_BREAKER = REGISTRY.register(
      "ore_breaker", () -> new ItemOreBreaker((Block)IRBlocks.ORE_BREAKER.get(), new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> LIQUID_HEAT_EXCHANGER = REGISTRY.register(
      "liquid_heat_exchanger", () -> new BlockItem((Block)IRBlocks.LIQUID_HEAT_EXCHANGER.get(), new Properties())
   );
   public static final DeferredItem<Item> CROPMATRON = REGISTRY.register("cropmatron", () -> new BlockItem((Block)IRBlocks.CROPMATRON.get(), new Properties()));
   public static final DeferredItem<Item> CROP_HARVESTER = REGISTRY.register(
      "crop_harvester", () -> new BlockItem((Block)IRBlocks.CROP_HARVESTER.get(), new Properties())
   );
   public static final DeferredItem<Item> FERMENTER = REGISTRY.register("fermenter", () -> new BlockItem((Block)IRBlocks.FERMENTER.get(), new Properties()));
   public static final DeferredItem<Item> BATBOX = REGISTRY.register("batbox", () -> new ElectricStorageBlockItem((Block)IRBlocks.BATBOX.get(), ElectricBatBoxBlockEntity.CAPACITY, ElectricBatBoxBlockEntity.RECEIVE, ElectricBatBoxBlockEntity.EXTRACT, new Properties()));
   public static final DeferredItem<Item> CESU = REGISTRY.register("cesu", () -> new ElectricStorageBlockItem((Block)IRBlocks.CESU.get(), ElectricCESUBlockEntity.CAPACITY, ElectricCESUBlockEntity.RECEIVE, ElectricCESUBlockEntity.EXTRACT, new Properties()));
   public static final DeferredItem<Item> MFE = REGISTRY.register("mfe", () -> new ElectricStorageBlockItem((Block)IRBlocks.MFE.get(), ElectricMFEBlockEntity.CAPACITY, ElectricMFEBlockEntity.RECEIVE, ElectricMFEBlockEntity.EXTRACT, new Properties()));
   public static final DeferredItem<Item> MFSU = REGISTRY.register(
      "mfsu", () -> new ElectricStorageBlockItem((Block)IRBlocks.MFSU.get(), ElectricMFSUBlockEntity.CAPACITY, ElectricMFSUBlockEntity.RECEIVE, ElectricMFSUBlockEntity.EXTRACT, new Properties().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> EFSU = REGISTRY.register(
      "efsu", () -> new ElectricStorageBlockItem((Block)IRBlocks.EFSU.get(), ElectricEFSUBlockEntity.CAPACITY, ElectricEFSUBlockEntity.RECEIVE, ElectricEFSUBlockEntity.EXTRACT, new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> BATBOX_CHARGEPAD = REGISTRY.register(
      "batbox_chargepad", () -> new ElectricStorageBlockItem((Block)IRBlocks.BATBOX_CHARGEPAD.get(), ElectricBatBoxBlockEntity.CAPACITY, ElectricBatBoxBlockEntity.RECEIVE, ElectricBatBoxBlockEntity.EXTRACT, new Properties())
   );
   public static final DeferredItem<Item> CESU_CHARGEPAD = REGISTRY.register(
      "cesu_chargepad", () -> new ElectricStorageBlockItem((Block)IRBlocks.CESU_CHARGEPAD.get(), ElectricCESUBlockEntity.CAPACITY, ElectricCESUBlockEntity.RECEIVE, ElectricCESUBlockEntity.EXTRACT, new Properties())
   );
   public static final DeferredItem<Item> MFE_CHARGEPAD = REGISTRY.register(
      "mfe_chargepad", () -> new ElectricStorageBlockItem((Block)IRBlocks.MFE_CHARGEPAD.get(), ElectricMFEBlockEntity.CAPACITY, ElectricMFEBlockEntity.RECEIVE, ElectricMFEBlockEntity.EXTRACT, new Properties())
   );
   public static final DeferredItem<Item> MFSU_CHARGEPAD = REGISTRY.register(
      "mfsu_chargepad", () -> new ElectricStorageBlockItem((Block)IRBlocks.MFSU_CHARGEPAD.get(), ElectricMFSUBlockEntity.CAPACITY, ElectricMFSUBlockEntity.RECEIVE, ElectricMFSUBlockEntity.EXTRACT, new Properties().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> EFSU_CHARGEPAD = REGISTRY.register(
      "efsu_chargepad", () -> new ElectricStorageBlockItem((Block)IRBlocks.EFSU_CHARGEPAD.get(), ElectricEFSUBlockEntity.CAPACITY, ElectricEFSUBlockEntity.RECEIVE, ElectricEFSUBlockEntity.EXTRACT, new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> LUMINATOR = REGISTRY.register("luminator", () -> new BlockItem((Block)IRBlocks.LUMINATOR.get(), new Properties()));
   public static final DeferredItem<Item> STEAM_TURBINE_BLADE = REGISTRY.register("steam_turbine_blade", IRItems::defaultItem);
   public static final DeferredItem<Item> STEAM_TURBINE = REGISTRY.register(
      "steam_turbine", () -> new ItemSteamTurbine(new Properties().durability(20000), 1.0F, () -> IRItems.STEAM_TURBINE_BLADE)
   );
   public static final DeferredItem<Item> STEEL_STEAM_TURBINE = REGISTRY.register(
      "steel_steam_turbine", () -> new ItemSteamTurbine(new Properties().durability(50000), 1.25F, () -> IRItems.STEAM_TURBINE_BLADE)
   );
   public static final DeferredItem<Item> IRIDIUM_STEAM_TURBINE = REGISTRY.register(
      "iridium_steam_turbine", () -> new ItemSteamTurbine(new Properties(), 1.5F, () -> IRItems.STEAM_TURBINE_BLADE)
   );
   public static final DeferredItem<Item> IRON_CUTTING_BLADE = REGISTRY.register("iron_cutting_blade", () -> new ItemCuttingBlade(1));
   public static final DeferredItem<Item> STEEL_CUTTING_BLADE = REGISTRY.register("steel_cutting_blade", () -> new ItemCuttingBlade(5));
   public static final DeferredItem<Item> DIAMOND_CUTTING_BLADE = REGISTRY.register("diamond_cutting_blade", () -> new ItemCuttingBlade(10));
   public static final DeferredItem<Item> WOODEN_ROTOR_BLADE = REGISTRY.register("wooden_rotor_blade", IRItems::defaultItem);
   public static final DeferredItem<Item> IRON_ROTOR_BLADE = REGISTRY.register("iron_rotor_blade", IRItems::defaultItem);
   public static final DeferredItem<Item> BRONZE_ROTOR_BLADE = REGISTRY.register("bronze_rotor_blade", IRItems::defaultItem);
   public static final DeferredItem<Item> STEEL_ROTOR_BLADE = REGISTRY.register("steel_rotor_blade", IRItems::defaultItem);
   public static final DeferredItem<Item> CARBON_ROTOR_BLADE = REGISTRY.register("carbon_rotor_blade", IRItems::defaultItem);
   public static final DeferredItem<Item> WOODEN_ROTOR = REGISTRY.register(
      "wooden_rotor", () -> new ItemWindRotor(
         new Properties().durability(2500), 5, false, 0.25F, 3, 60,
         IR.location("textures/item/rotor/wooden_rotor_model.png"), () -> IRItems.WOODEN_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> BRONZE_ROTOR = REGISTRY.register(
      "bronze_rotor", () -> new ItemWindRotor(
         new Properties().durability(6000), 7, false, 0.5F, 4, 75,
         IR.location("textures/item/rotor/bronze_rotor_model.png"), () -> IRItems.BRONZE_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> IRON_ROTOR = REGISTRY.register(
      "iron_rotor", () -> new ItemWindRotor(
         new Properties().durability(9000), 7, false, 0.5F, 4, 75,
         IR.location("textures/item/rotor/iron_rotor_model.png"), () -> IRItems.IRON_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> STEEL_ROTOR = REGISTRY.register(
      "steel_rotor", () -> new ItemWindRotor(
         new Properties().durability(20000), 9, false, 0.75F, 5, 90,
         IR.location("textures/item/rotor/steel_rotor_model.png"), () -> IRItems.STEEL_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> CARBON_ROTOR = REGISTRY.register(
      "carbon_rotor", () -> new ItemWindRotor(
         new Properties().durability(60000), 11, false, 1.0F, 6, 110,
         IR.location("textures/item/rotor/carbon_rotor_model.png"), () -> IRItems.CARBON_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> IRIDIUM_ROTOR = REGISTRY.register(
      "iridium_rotor", () -> new ItemWindRotor(
         new Properties(), 11, false, 2.0F, 7, 110,
         IR.location("textures/item/rotor/iridium_rotor_model.png"), () -> IRItems.CARBON_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> TITANIUM_ROTOR = REGISTRY.register(
      "titanium_rotor", () -> new ItemWindRotor(
         new Properties(), 11, false, 5.0F, 8, 110,
         IR.location("textures/item/rotor/titanium_rotor_model.png"), () -> IRItems.CARBON_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> TUNGSTEN_ROTOR = REGISTRY.register(
      "tungsten_rotor", () -> new ItemWindRotor(
         new Properties(), 11, false, 10.0F, 9, 110,
         IR.location("textures/item/rotor/tungsten_rotor_model.png"), () -> IRItems.CARBON_ROTOR_BLADE
      )
   );
   public static final DeferredItem<Item> UPGRADE_MACHINE_TO_2_LEVEL = REGISTRY.register(
      "upgrade_machine_to_2_level",
      () -> new ItemUpgradeMachine(
         Map.of(
            ElectricFurnace1LevelBlockEntity.class,
            IRBlocks.ELECTRIC_FURNACE_2_LEVEL,
            Macerator1LevelBlockEntity.class,
            IRBlocks.MACERATOR_2_LEVEL,
            Recycler1LevelBlockEntity.class,
            IRBlocks.RECYCLER_2_LEVEL,
            Extractor1LevelBlockEntity.class,
            IRBlocks.EXTRACTOR_2_LEVEL,
            Compressor1LevelBlockEntity.class,
            IRBlocks.COMPRESSOR_2_LEVEL,
            MetalFormer1LevelBlockEntity.class,
            IRBlocks.METAL_FORMER_2_LEVEL
         ),
         new Properties().rarity(Rarity.UNCOMMON)
      )
   );
   public static final DeferredItem<Item> UPGRADE_MACHINE_TO_3_LEVEL = REGISTRY.register(
      "upgrade_machine_to_3_level",
      () -> new ItemUpgradeMachine(
         Map.of(
            ElectricFurnace2LevelBlockEntity.class,
            IRBlocks.ELECTRIC_FURNACE_3_LEVEL,
            Macerator2LevelBlockEntity.class,
            IRBlocks.MACERATOR_3_LEVEL,
            Recycler2LevelBlockEntity.class,
            IRBlocks.RECYCLER_3_LEVEL,
            Extractor2LevelBlockEntity.class,
            IRBlocks.EXTRACTOR_3_LEVEL,
            Compressor2LevelBlockEntity.class,
            IRBlocks.COMPRESSOR_3_LEVEL,
            MetalFormer2LevelBlockEntity.class,
            IRBlocks.METAL_FORMER_3_LEVEL
         ),
         new Properties().rarity(Rarity.RARE)
      )
   );
   public static final DeferredItem<Item> UPGRADE_MACHINE_TO_4_LEVEL = REGISTRY.register(
      "upgrade_machine_to_4_level",
      () -> new ItemUpgradeMachine(
         Map.of(
            ElectricFurnace3LevelBlockEntity.class,
            IRBlocks.ELECTRIC_FURNACE_4_LEVEL,
            Macerator3LevelBlockEntity.class,
            IRBlocks.MACERATOR_4_LEVEL,
            Recycler3LevelBlockEntity.class,
            IRBlocks.RECYCLER_4_LEVEL,
            Extractor3LevelBlockEntity.class,
            IRBlocks.EXTRACTOR_4_LEVEL,
            Compressor3LevelBlockEntity.class,
            IRBlocks.COMPRESSOR_4_LEVEL,
            MetalFormer3LevelBlockEntity.class,
            IRBlocks.METAL_FORMER_4_LEVEL
         ),
         new Properties().rarity(Rarity.EPIC)
      )
   );
   public static final DeferredItem<Item> RESIN = REGISTRY.register("resin", IRItems::defaultItem);
   public static final DeferredItem<Item> RUBBER = REGISTRY.register("rubber", IRItems::defaultItem);
   public static final DeferredItem<Item> CIRCUIT = REGISTRY.register("circuit", IRItems::defaultItem);
   public static final DeferredItem<Item> ADVANCED_CIRCUIT = REGISTRY.register("advanced_circuit", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> NANO_CIRCUIT = REGISTRY.register("nano_circuit", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> QUANTUM_CIRCUIT = REGISTRY.register("quantum_circuit", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> SINGULAR_CIRCUIT = REGISTRY.register("singular_circuit", () -> new Item(new Properties().rarity(Rarity.EPIC)));
   public static final DeferredItem<Item> ALLOY = REGISTRY.register("alloy", IRItems::defaultItem);
   public static final DeferredItem<Item> CARBON_FIBRE = REGISTRY.register("carbon_fibre", IRItems::defaultItem);
   public static final DeferredItem<Item> CARBON_MESH = REGISTRY.register("carbon_mesh", IRItems::defaultItem);
   public static final DeferredItem<Item> CARBON_PLATE = REGISTRY.register("carbon_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> IRIDIUM_ORE = REGISTRY.register("iridium_ore", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> IRIDIUM_PLATE = REGISTRY.register("iridium", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> IRIDIUM_SHARD = REGISTRY.register("iridium_shard", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> COAL_BALL = REGISTRY.register("coal_ball", IRItems::defaultItem);
   public static final DeferredItem<Item> COAL_BLOCK = REGISTRY.register("coal_block", IRItems::defaultItem);
   public static final DeferredItem<Item> COAL_CHUNK = REGISTRY.register("coal_chunk", IRItems::defaultItem);
   public static final DeferredItem<Item> SCRAP = REGISTRY.register("scrap", () -> new ItemScrap(new Properties()));
   public static final DeferredItem<Item> SCRAP_BOX = REGISTRY.register("scrap_box", () -> new ItemScrap(new Properties()));
   public static final DeferredItem<Item> INDUSTRIAL_DIAMOND = REGISTRY.register("industrial_diamond", IRItems::defaultItem);
   public static final DeferredItem<Item> COIL = REGISTRY.register("coil", IRItems::defaultItem);
   public static final DeferredItem<Item> ELECTRIC_MOTOR = REGISTRY.register("electric_motor", IRItems::defaultItem);
   public static final DeferredItem<Item> HEAT_CONDUCTOR = REGISTRY.register("heat_conductor", IRItems::defaultItem);
   public static final DeferredItem<Item> COPPER_BOILER = REGISTRY.register("copper_boiler", IRItems::defaultItem);
   public static final DeferredItem<Item> FUEL_ROD = REGISTRY.register("fuel_rod", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_POWER_UNIT = REGISTRY.register("small_power_unit", IRItems::defaultItem);
   public static final DeferredItem<Item> POWER_UNIT = REGISTRY.register("power_unit", IRItems::defaultItem);
   public static final DeferredItem<ItemTurningProduct> WOODEN_GRIP = REGISTRY.register(
      "wooden_grip", () -> new ItemTurningProduct(3, new byte[]{3, 3, 2, 2, 2}, "wooden_grip")
   );
   public static final DeferredItem<ItemTurningProduct> IRON_GRIP = REGISTRY.register(
      "iron_grip", () -> new ItemTurningProduct(5, new byte[]{4, 3, 2, 2, 2}, "iron_grip")
   );
   public static final DeferredItem<ItemTurningProduct> IRON_DRILL_HEAD = REGISTRY.register(
      "iron_drill_head", () -> new ItemTurningProduct(5, new byte[]{5, 4, 3, 2, 1}, "iron_drill_head")
   );
   public static final DeferredItem<ItemTurningProduct> IRON_THIN_KNURLED_AXLE = REGISTRY.register(
      "iron_thin_knurled_axle", () -> new ItemTurningProduct(5, new byte[]{3, 2, 3, 2, 3}, "iron_thin_knurled_axle")
   );
   public static final DeferredItem<ItemTurningProduct> STEEL_GRIP = REGISTRY.register(
      "steel_grip", () -> new ItemTurningProduct(7, new byte[]{5, 4, 3, 2, 2}, "steel_grip")
   );
   public static final DeferredItem<ItemTurningProduct> STEEL_DRILL_HEAD = REGISTRY.register(
      "steel_drill_head", () -> new ItemTurningProduct(7, new byte[]{7, 6, 4, 2, 1}, "steel_drill_head")
   );
   public static final DeferredItem<ItemTurningProduct> STEEL_THIN_KNURLED_AXLE = REGISTRY.register(
      "steel_thin_knurled_axle", () -> new ItemTurningProduct(7, new byte[]{5, 4, 5, 4, 5}, "steel_thin_knurled_axle")
   );
   public static final DeferredItem<ItemTurningProduct> STEEL_KNURLED_AXLE = REGISTRY.register(
      "steel_knurled_axle", () -> new ItemTurningProduct(7, new byte[]{6, 5, 6, 5, 6}, "steel_knurled_axle")
   );
   public static final DeferredItem<Item> IRON_SHAFT = REGISTRY.register("iron_shaft", IRItems::defaultItem);
   public static final DeferredItem<Item> BRONZE_SHAFT = REGISTRY.register("bronze_shaft", IRItems::defaultItem);
   public static final DeferredItem<Item> STEEL_SHAFT = REGISTRY.register("steel_shaft", IRItems::defaultItem);
   public static final DeferredItem<Item> CORE_MOLECULAR_TRANSFORMER = REGISTRY.register("core_molecular_transformer", IRItems::defaultItem);
   public static final DeferredItem<Item> SUNNARIUM_PART = REGISTRY.register("sunnarium_part", IRItems::defaultItem);
   public static final DeferredItem<Item> SUNNARIUM = REGISTRY.register("sunnarium", IRItems::defaultItem);
   public static final DeferredItem<Item> ENRICHED_SUNNARIUM = REGISTRY.register("enriched_sunnarium", IRItems::defaultItem);
   public static final DeferredItem<Item> SUNNARIUM_ALLOY = REGISTRY.register("sunnarium_alloy", IRItems::defaultItem);
   public static final DeferredItem<Item> ENRICHED_SUNNARIUM_ALLOY = REGISTRY.register("enriched_sunnarium_alloy", IRItems::defaultItem);
   public static final DeferredItem<Item> QUANTUM_MODULE = REGISTRY.register("quantum_module", IRItems::defaultItem);
   public static final DeferredItem<Item> HOT_QUANTUM_MODULE = REGISTRY.register("hot_quantum_module", IRItems::defaultItem);
   public static final DeferredItem<Item> RADIATION_QUANTUM_MODULE = REGISTRY.register("radiation_quantum_module", IRItems::defaultItem);
   public static final DeferredItem<Item> UNSTABLE_QUANTUM_MODULE = REGISTRY.register("unstable_quantum_module", IRItems::defaultItem);
   public static final DeferredItem<Item> SEMI_STABLE_QUANTUM_MODULE = REGISTRY.register("semi_stable_quantum_module", IRItems::defaultItem);
   public static final DeferredItem<Item> STABLE_QUANTUM_MODULE = REGISTRY.register("stable_quantum_module", IRItems::defaultItem);
   public static final DeferredItem<Item> QUANTUM_CORE_1_LEVEL = REGISTRY.register("quantum_core_1_level", IRItems::defaultItem);
   public static final DeferredItem<Item> QUANTUM_CORE_2_LEVEL = REGISTRY.register("quantum_core_2_level", IRItems::defaultItem);
   public static final DeferredItem<Item> QUANTUM_CORE_3_LEVEL = REGISTRY.register("quantum_core_3_level", IRItems::defaultItem);
   public static final DeferredItem<Item> TRANSISTOR = REGISTRY.register("transistor", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_1_LEVEL = REGISTRY.register("photocell_1_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_2_LEVEL = REGISTRY.register("photocell_2_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_3_LEVEL = REGISTRY.register("photocell_3_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_4_LEVEL = REGISTRY.register("photocell_4_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_5_LEVEL = REGISTRY.register("photocell_5_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_6_LEVEL = REGISTRY.register("photocell_6_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_7_LEVEL = REGISTRY.register("photocell_7_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_8_LEVEL = REGISTRY.register("photocell_8_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_9_LEVEL = REGISTRY.register("photocell_9_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_10_LEVEL = REGISTRY.register("photocell_10_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_11_LEVEL = REGISTRY.register("photocell_11_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_12_LEVEL = REGISTRY.register("photocell_12_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_13_LEVEL = REGISTRY.register("photocell_13_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_14_LEVEL = REGISTRY.register("photocell_14_level", IRItems::defaultItem);
   public static final DeferredItem<Item> PHOTOCELL_15_LEVEL = REGISTRY.register("photocell_15_level", IRItems::defaultItem);
   public static final DeferredItem<Item> COOLING_CORE = REGISTRY.register("cooling_core", IRItems::defaultItem);
   public static final DeferredItem<Item> ENGINE_BOOSTER = REGISTRY.register("engine_booster", IRItems::defaultItem);
   public static final DeferredItem<Item> GRAVITY_ENGINE = REGISTRY.register("gravity_engine", IRItems::defaultItem);
   public static final DeferredItem<Item> MAGNETRON = REGISTRY.register("magnetron", IRItems::defaultItem);
   public static final DeferredItem<Item> SUPERCONDUCTOR = REGISTRY.register("superconductor", IRItems::defaultItem);
   public static final DeferredItem<Item> SUPERCONDUCTOR_COVER = REGISTRY.register("superconductor_cover", IRItems::defaultItem);
   public static final DeferredItem<Item> VAJRA_CORE = REGISTRY.register("vajra_core", IRItems::defaultItem);
   public static final DeferredItem<Item> SLAG = REGISTRY.register("slag", IRItems::defaultItem);
   public static final DeferredItem<Item> PLANT_BALL = REGISTRY.register("plant_ball", IRItems::defaultItem);
   public static final DeferredItem<Item> BIO_CHAFF = REGISTRY.register("bio_chaff", IRItems::defaultItem);
   public static final DeferredItem<Item> TIN_CAN = REGISTRY.register("tin_can", IRItems::defaultItem);
   public static final DeferredItem<Item> WEED = REGISTRY.register("weed", IRItems::defaultItem);
   public static final DeferredItem<Item> COFFEE_BEANS = REGISTRY.register("coffee_beans", IRItems::defaultItem);
   public static final DeferredItem<Item> COFFEE_POWDER = REGISTRY.register("coffee_powder", IRItems::defaultItem);
   public static final DeferredItem<Item> COFFEE_MUG = REGISTRY.register("coffee_mug", () -> new ItemMug(new Properties().stacksTo(1), ItemMug.MugType.coffee));
   public static final DeferredItem<Item> COLD_COFFEE_MUG = REGISTRY.register(
      "cold_coffee_mug", () -> new ItemMug(new Properties().stacksTo(1), ItemMug.MugType.cold_coffee)
   );
   public static final DeferredItem<Item> DARK_COFFEE_MUG = REGISTRY.register(
      "dark_coffee_mug", () -> new ItemMug(new Properties().stacksTo(1), ItemMug.MugType.dark_coffee)
   );
   public static final DeferredItem<ItemBooze> BOOZE_MUG = REGISTRY.register("booze_mug", () -> new ItemBooze(new Properties().stacksTo(1)));
   public static final DeferredItem<Item> HOPS = REGISTRY.register("hops", IRItems::defaultItem);
   public static final DeferredItem<Item> TERRA_WART = REGISTRY.register(
      "terra_wart",
      () -> new ItemTerraWart(new Properties().food(new Builder().nutrition(0).saturationModifier(1.0F).alwaysEdible().build()).rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> RAW_TIN = REGISTRY.register("raw_tin", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_LEAD = REGISTRY.register("raw_lead", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_NICKEL = REGISTRY.register("raw_nickel", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_SILVER = REGISTRY.register("raw_silver", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_URANIUM = REGISTRY.register("raw_uranium", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_TOXIC = REGISTRY.register("raw_toxic", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_TITANIUM = REGISTRY.register("raw_titanium", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_TUNGSTEN = REGISTRY.register("raw_tungsten", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_TIN = REGISTRY.register("crushed_tin", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_COPPER = REGISTRY.register("crushed_copper", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_GOLD = REGISTRY.register("crushed_gold", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_IRON = REGISTRY.register("crushed_iron", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_LEAD = REGISTRY.register("crushed_lead", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_ALUMINUM = REGISTRY.register("crushed_aluminum", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_NICKEL = REGISTRY.register("crushed_nickel", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_SILVER = REGISTRY.register("crushed_silver", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_URANIUM = REGISTRY.register("crushed_uranium", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_TOXIC = REGISTRY.register("crushed_toxic", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_TITANIUM = REGISTRY.register("crushed_titanium", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_TUNGSTEN = REGISTRY.register("crushed_tungsten", IRItems::defaultItem);
   public static final DeferredItem<Item> CRUSHED_OSMIUM = REGISTRY.register("crushed_osmium", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_TIN = REGISTRY.register("purified_tin", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_COPPER = REGISTRY.register("purified_copper", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_GOLD = REGISTRY.register("purified_gold", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_IRON = REGISTRY.register("purified_iron", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_LEAD = REGISTRY.register("purified_lead", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_ALUMINUM = REGISTRY.register("purified_aluminum", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_NICKEL = REGISTRY.register("purified_nickel", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_SILVER = REGISTRY.register("purified_silver", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_URANIUM = REGISTRY.register("purified_uranium", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_TOXIC = REGISTRY.register("purified_toxic", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_TITANIUM = REGISTRY.register("purified_titanium", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_TUNGSTEN = REGISTRY.register("purified_tungsten", IRItems::defaultItem);
   public static final DeferredItem<Item> PURIFIED_OSMIUM = REGISTRY.register("purified_osmium", IRItems::defaultItem);
   public static final DeferredItem<Item> COAL_DUST = REGISTRY.register("coal_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SULFUR_DUST = REGISTRY.register("sulfur_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> LAPIS_DUST = REGISTRY.register("lapis_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> OBSIDIAN_DUST = REGISTRY.register("obsidian_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> DIAMOND_DUST = REGISTRY.register("diamond_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> CLAY_DUST = REGISTRY.register("clay_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> IRON_DUST = REGISTRY.register("iron_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> GOLD_DUST = REGISTRY.register("gold_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> COPPER_DUST = REGISTRY.register("copper_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> TIN_DUST = REGISTRY.register("tin_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> LEAD_DUST = REGISTRY.register("lead_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> NICKEL_DUST = REGISTRY.register("nickel_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SILVER_DUST = REGISTRY.register("silver_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> BRONZE_DUST = REGISTRY.register("bronze_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> STEEL_DUST = REGISTRY.register("steel_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> ENERGIUM_DUST = REGISTRY.register("energium_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> LITHIUM_DUST = REGISTRY.register("lithium_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SILICON_DIOXIDE_DUST = REGISTRY.register("silicon_dioxide_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> STONE_DUST = REGISTRY.register("stone_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> TOXIC_DUST = REGISTRY.register("toxic_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> TITANIUM_DUST = REGISTRY.register("titanium_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> TUNGSTEN_DUST = REGISTRY.register("tungsten_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_BRONZE_DUST = REGISTRY.register("small_bronze_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_COPPER_DUST = REGISTRY.register("small_copper_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_GOLD_DUST = REGISTRY.register("small_gold_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_IRON_DUST = REGISTRY.register("small_iron_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_LAPIS_DUST = REGISTRY.register("small_lapis_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_LEAD_DUST = REGISTRY.register("small_lead_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_LITHIUM_DUST = REGISTRY.register("small_lithium_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_OBSIDIAN_DUST = REGISTRY.register("small_obsidian_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_SILVER_DUST = REGISTRY.register("small_silver_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_SULFUR_DUST = REGISTRY.register("small_sulfur_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> SMALL_TIN_DUST = REGISTRY.register("small_tin_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> HYDRATED_TIN_DUST = REGISTRY.register("hydrated_tin_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> NETHERRACK_DUST = REGISTRY.register("netherrack_dust", IRItems::defaultItem);
   public static final DeferredItem<Item> TIN_INGOT = REGISTRY.register("tin_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> LEAD_INGOT = REGISTRY.register("lead_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> NICKEL_INGOT = REGISTRY.register("nickel_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> SILVER_INGOT = REGISTRY.register("silver_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> BRONZE_INGOT = REGISTRY.register("bronze_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> STEEL_INGOT = REGISTRY.register("steel_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> MIXED_METAL_INGOT = REGISTRY.register("mixed_metal_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> URANIUM_INGOT = REGISTRY.register("uranium_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> IRRADIANT_URANIUM_INGOT = REGISTRY.register("irradiant_uranium_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> IRIDIUM_INGOT = REGISTRY.register("iridium_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> TOXIC_INGOT = REGISTRY.register("toxic_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> TITANIUM_INGOT = REGISTRY.register("titanium_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> TUNGSTEN_INGOT = REGISTRY.register("tungsten_ingot", IRItems::defaultItem);
   public static final DeferredItem<Item> LAPIS_PLATE = REGISTRY.register("lapis_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> OBSIDIAN_PLATE = REGISTRY.register("obsidian_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> IRON_PLATE = REGISTRY.register("iron_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> GOLD_PLATE = REGISTRY.register("gold_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> COPPER_PLATE = REGISTRY.register("copper_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> TIN_PLATE = REGISTRY.register("tin_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> LEAD_PLATE = REGISTRY.register("lead_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> NICKEL_PLATE = REGISTRY.register("nickel_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> SILVER_PLATE = REGISTRY.register("silver_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> BRONZE_PLATE = REGISTRY.register("bronze_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> STEEL_PLATE = REGISTRY.register("steel_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> TOXIC_PLATE = REGISTRY.register("toxic_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> TITANIUM_PLATE = REGISTRY.register("titanium_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> TUNGSTEN_PLATE = REGISTRY.register("tungsten_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> OSMIUM_PLATE = REGISTRY.register("osmium_plate", IRItems::defaultItem);
    public static final DeferredItem<Item> TERRASTEEL_PLATE = REGISTRY.register("terrasteel_plate", IRItems::defaultItem);
    public static final DeferredItem<Item> ELEMENTIUM_PLATE = REGISTRY.register("elementium_plate", IRItems::defaultItem);
    public static final DeferredItem<Item> MANASTEEL_PLATE = REGISTRY.register("manasteel_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_BRONZE_PLATE = REGISTRY.register("dense_bronze_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_COPPER_PLATE = REGISTRY.register("dense_copper_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_GOLD_PLATE = REGISTRY.register("dense_gold_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_IRON_PLATE = REGISTRY.register("dense_iron_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_LAPIS_PLATE = REGISTRY.register("dense_lapis_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_LEAD_PLATE = REGISTRY.register("dense_lead_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_NICKEL_PLATE = REGISTRY.register("dense_nickel_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_OBSIDIAN_PLATE = REGISTRY.register("dense_obsidian_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_STEEL_PLATE = REGISTRY.register("dense_steel_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_TIN_PLATE = REGISTRY.register("dense_tin_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_TOXIC_PLATE = REGISTRY.register("dense_toxic_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_TITANIUM_PLATE = REGISTRY.register("dense_titanium_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_TUNGSTEN_PLATE = REGISTRY.register("dense_tungsten_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> DENSE_OSMIUM_PLATE = REGISTRY.register("dense_osmium_plate", IRItems::defaultItem);
   public static final DeferredItem<Item> BRONZE_CASING = REGISTRY.register("bronze_casing", IRItems::defaultItem);
   public static final DeferredItem<Item> COPPER_CASING = REGISTRY.register("copper_casing", IRItems::defaultItem);
   public static final DeferredItem<Item> GOLD_CASING = REGISTRY.register("gold_casing", IRItems::defaultItem);
   public static final DeferredItem<Item> IRON_CASING = REGISTRY.register("iron_casing", IRItems::defaultItem);
   public static final DeferredItem<Item> LEAD_CASING = REGISTRY.register("lead_casing", IRItems::defaultItem);
   public static final DeferredItem<Item> STEEL_CASING = REGISTRY.register("steel_casing", IRItems::defaultItem);
   public static final DeferredItem<Item> TIN_CASING = REGISTRY.register("tin_casing", IRItems::defaultItem);
   public static final DeferredItem<Item> URANIUM = REGISTRY.register("uranium", () -> new ItemNuclearResource(new Properties(), 60, 100));
   public static final DeferredItem<Item> URANIUM_235 = REGISTRY.register("uranium_235", () -> new ItemNuclearResource(new Properties(), 150, 100));
   public static final DeferredItem<Item> URANIUM_238 = REGISTRY.register("uranium_238", () -> new ItemNuclearResource(new Properties(), 10, 90));
   public static final DeferredItem<Item> PLUTONIUM = REGISTRY.register("plutonium", () -> new ItemNuclearResource(new Properties(), 150, 100));
   public static final DeferredItem<Item> MOX = REGISTRY.register("mox", () -> new ItemNuclearResource(new Properties(), 300, 100));
   public static final DeferredItem<Item> SMALL_URANIUM_235 = REGISTRY.register("small_uranium_235", () -> new ItemNuclearResource(new Properties(), 150, 100));
   public static final DeferredItem<Item> SMALL_URANIUM_238 = REGISTRY.register("small_uranium_238", () -> new ItemNuclearResource(new Properties(), 10, 90));
   public static final DeferredItem<Item> SMALL_PLUTONIUM = REGISTRY.register("small_plutonium", () -> new ItemNuclearResource(new Properties(), 150, 100));
   public static final DeferredItem<Item> URANIUM_PELLET = REGISTRY.register("uranium_pellet", () -> new ItemNuclearResource(new Properties(), 60, 100));
   public static final DeferredItem<Item> MOX_PELLET = REGISTRY.register("mox_pellet", () -> new ItemNuclearResource(new Properties(), 300, 100));
   public static final DeferredItem<Item> PLUTONIUM_PELLET = REGISTRY.register(
      "plutonium_pellet", () -> new ItemNuclearResource(new Properties(), 200, 100)
   );
   public static final DeferredItem<Item> RTG_PELLET = REGISTRY.register("rtg_pellet", () -> new ItemNuclearResource(new Properties().stacksTo(1), 2, 90));
   public static final DeferredItem<Item> DEPLETED_URANIUM_FUEL_ROD = REGISTRY.register(
      "depleted_uranium_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_DUAL_URANIUM_FUEL_ROD = REGISTRY.register(
      "depleted_dual_uranium_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_QUAD_URANIUM_FUEL_ROD = REGISTRY.register(
      "depleted_quad_uranium_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_MOX_FUEL_ROD = REGISTRY.register(
      "depleted_mox_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_DUAL_MOX_FUEL_ROD = REGISTRY.register(
      "depleted_dual_mox_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_QUAD_MOX_FUEL_ROD = REGISTRY.register(
      "depleted_quad_mox_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_PLUTONIUM_FUEL_ROD = REGISTRY.register(
      "depleted_plutonium_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_DUAL_PLUTONIUM_FUEL_ROD = REGISTRY.register(
      "depleted_dual_plutonium_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_QUAD_PLUTONIUM_FUEL_ROD = REGISTRY.register(
      "depleted_quad_plutonium_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> DEPLETED_TRITIUM_FUEL_ROD = REGISTRY.register(
      "depleted_tritium_fuel_rod", () -> new ItemNuclearResource(new Properties(), 10, 100)
   );
   public static final DeferredItem<Item> NEAR_DEPLETED_URANIUM = REGISTRY.register(
      "near_depleted_uranium", () -> new ItemNuclearResource(new Properties(), 15, 100)
   );
   public static final DeferredItem<Item> RE_ENRICHED_URANIUM = REGISTRY.register(
      "re_enriched_uranium", () -> new ItemNuclearResource(new Properties(), 30, 100)
   );
   public static final DeferredItem<Item> IODINE = REGISTRY.register("iodine", IRItems::defaultItem);
   public static final DeferredItem<Item> CF_POWDER = REGISTRY.register("cf_powder", IRItems::defaultItem);
   public static final DeferredItem<Item> RAW_CRYSTAL_MEMORY = REGISTRY.register("raw_crystal_memory", IRItems::defaultItem);
   public static final DeferredItem<Item> CRYSTAL_MEMORY = REGISTRY.register("crystal_memory", ItemCrystalMemory::new);
   public static final DeferredItem<Item> RE_BATTERY = REGISTRY.register("re_battery", () -> new ItemBattery(10000, 128, new Properties()));
   public static final DeferredItem<Item> ADVANCED_RE_BATTERY = REGISTRY.register(
      "advanced_re_battery", () -> new ItemBattery(100000, 1024, new Properties().stacksTo(16))
   );
   public static final DeferredItem<Item> ENERGY_CRYSTAL = REGISTRY.register(
      "energy_crystal", () -> new ItemBattery(4000000, 16384, new Properties().stacksTo(16))
   );
   public static final DeferredItem<Item> LAPOTRON_CRYSTAL = REGISTRY.register(
      "lapotron_crystal", () -> new ItemBattery(40000000, 65536, new Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> SINGULAR_CRYSTAL = REGISTRY.register(
      "singular_crystal", () -> new ItemBattery(400000000, 262144, new Properties().stacksTo(16).rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> CHARGING_RE_BATTERY = REGISTRY.register(
      "charging_re_battery", () -> new ItemBattery(40000, 512, true, new Properties().stacksTo(16))
   );
   public static final DeferredItem<Item> CHARGING_ADVANCED_RE_BATTERY = REGISTRY.register(
      "charging_advanced_re_battery", () -> new ItemBattery(400000, 4096, true, new Properties().stacksTo(16))
   );
   public static final DeferredItem<Item> CHARGING_ENERGY_CRYSTAL = REGISTRY.register(
      "charging_energy_crystal", () -> new ItemBattery(4000000, 16384, true, new Properties().stacksTo(16))
   );
   public static final DeferredItem<Item> CHARGING_LAPOTRON_CRYSTAL = REGISTRY.register(
      "charging_lapotron_crystal", () -> new ItemBattery(40000000, 65536, true, new Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> REACTOR_COOLANT_CELL = REGISTRY.register(
      "reactor_coolant_cell", () -> new ItemReactorHeatStorage(new Properties(), 10000)
   );
   public static final DeferredItem<Item> TRIPLE_REACTOR_COOLANT_CELL = REGISTRY.register(
      "triple_reactor_coolant_cell", () -> new ItemReactorHeatStorage(new Properties(), 30000)
   );
   public static final DeferredItem<Item> SEXTUPLE_REACTOR_COOLANT_CELL = REGISTRY.register(
      "sextuple_reactor_coolant_cell", () -> new ItemReactorHeatStorage(new Properties(), 60000)
   );
   public static final DeferredItem<Item> REACTOR_PLATING = REGISTRY.register("reactor_plating", () -> new ItemReactorPlating(new Properties(), 1000, 0.95F));
   public static final DeferredItem<Item> REACTOR_HEAT_PLATING = REGISTRY.register(
      "reactor_heat_plating", () -> new ItemReactorPlating(new Properties(), 2000, 0.99F)
   );
   public static final DeferredItem<Item> CONTAINMENT_REACTOR_PLATING = REGISTRY.register(
      "containment_reactor_plating", () -> new ItemReactorPlating(new Properties(), 500, 0.9F)
   );
   public static final DeferredItem<Item> HEAT_EXCHANGER = REGISTRY.register("heat_exchanger", () -> new ItemReactorHeatSwitch(new Properties(), 2500, 12, 4));
   public static final DeferredItem<Item> REACTOR_HEAT_EXCHANGER = REGISTRY.register(
      "reactor_heat_exchanger", () -> new ItemReactorHeatSwitch(new Properties(), 5000, 0, 72)
   );
   public static final DeferredItem<Item> COMPONENT_HEAT_EXCHANGER = REGISTRY.register(
      "component_heat_exchanger", () -> new ItemReactorHeatSwitch(new Properties(), 5000, 36, 0)
   );
   public static final DeferredItem<Item> ADVANCED_HEAT_EXCHANGER = REGISTRY.register(
      "advanced_heat_exchanger", () -> new ItemReactorHeatSwitch(new Properties(), 10000, 24, 8)
   );
   public static final DeferredItem<Item> HEAT_VENT = REGISTRY.register("heat_vent", () -> new ItemReactorVent(new Properties(), 1000, 6, 0));
   public static final DeferredItem<Item> REACTOR_HEAT_VENT = REGISTRY.register("reactor_heat_vent", () -> new ItemReactorVent(new Properties(), 1000, 5, 5));
   public static final DeferredItem<Item> OVERCLOCKED_HEAT_VENT = REGISTRY.register(
      "overclocked_heat_vent", () -> new ItemReactorVent(new Properties(), 1000, 20, 36)
   );
   public static final DeferredItem<Item> COMPONENT_HEAT_VENT = REGISTRY.register("component_heat_vent", () -> new ItemReactorVentSpread(new Properties(), 4));
   public static final DeferredItem<Item> ADVANCED_HEAT_VENT = REGISTRY.register("advanced_heat_vent", () -> new ItemReactorVent(new Properties(), 1000, 12, 0));
   public static final DeferredItem<Item> NEUTRON_REFLECTOR = REGISTRY.register("neutron_reflector", () -> new ItemReactorReflector(new Properties(), 30000));
   public static final DeferredItem<Item> THICK_NEUTRON_REFLECTOR = REGISTRY.register(
      "thick_neutron_reflector", () -> new ItemReactorReflector(new Properties(), 120000)
   );
   public static final DeferredItem<Item> IRIDIUM_NEUTRON_REFLECTOR = REGISTRY.register(
      "iridium_neutron_reflector", () -> new ItemReactorIridiumReflector(new Properties())
   );
   public static final DeferredItem<Item> RSH_CONDENSATOR = REGISTRY.register("rsh_condensator", () -> new ItemReactorCondensator(new Properties(), 20000));
   public static final DeferredItem<Item> LZH_CONDENSATOR = REGISTRY.register("lzh_condensator", () -> new ItemReactorCondensator(new Properties(), 100000));
   public static final DeferredItem<Item> HEATPACK = REGISTRY.register("heatpack", () -> new ItemReactorHeatpack(new Properties(), 1000, 1));
   public static final DeferredItem<Item> URANIUM_FUEL_ROD = REGISTRY.register("uranium_fuel_rod", () -> new ItemReactorUranium(new Properties(), 1));
   public static final DeferredItem<Item> DUAL_URANIUM_FUEL_ROD = REGISTRY.register("dual_uranium_fuel_rod", () -> new ItemReactorUranium(new Properties(), 2));
   public static final DeferredItem<Item> QUAD_URANIUM_FUEL_ROD = REGISTRY.register("quad_uranium_fuel_rod", () -> new ItemReactorUranium(new Properties(), 4));
   public static final DeferredItem<Item> MOX_FUEL_ROD = REGISTRY.register("mox_fuel_rod", () -> new ItemReactorMOX(new Properties(), 1));
   public static final DeferredItem<Item> DUAL_MOX_FUEL_ROD = REGISTRY.register("dual_mox_fuel_rod", () -> new ItemReactorMOX(new Properties(), 2));
   public static final DeferredItem<Item> QUAD_MOX_FUEL_ROD = REGISTRY.register("quad_mox_fuel_rod", () -> new ItemReactorMOX(new Properties(), 4));
   public static final DeferredItem<Item> PLUTONIUM_FUEL_ROD = REGISTRY.register("plutonium_fuel_rod", () -> new ItemReactorPlutonium(new Properties(), 1));
   public static final DeferredItem<Item> DUAL_PLUTONIUM_FUEL_ROD = REGISTRY.register("dual_plutonium_fuel_rod", () -> new ItemReactorPlutonium(new Properties(), 2));
   public static final DeferredItem<Item> QUAD_PLUTONIUM_FUEL_ROD = REGISTRY.register("quad_plutonium_fuel_rod", () -> new ItemReactorPlutonium(new Properties(), 4));
   public static final DeferredItem<Item> TRITIUM_FUEL_ROD = REGISTRY.register("tritium_fuel_rod", () -> new ItemReactorTritium());
   public static final DeferredItem<Item> LITHIUM_FUEL_ROD = REGISTRY.register("lithium_fuel_rod", () -> new ItemReactorLithiumCell(new Properties()));
   public static final DeferredItem<Item> DEPLETED_ISOTOPE_FUEL_ROD = REGISTRY.register(
      "depleted_isotope_fuel_rod", () -> new ItemReactorDepletedUranium(new Properties())
   );
   public static final DeferredItem<Item> FREQUENCY_TRANSMITTER = REGISTRY.register(
      "frequency_transmitter", () -> new ItemFrequencyTransmitter(new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> TURNING_TOOL = REGISTRY.register("turning_tool", () -> new Item(new Properties().durability(30)));
   public static final DeferredItem<ItemTurningMold> WOODEN_TURNING_MOLD = REGISTRY.register(
      "wooden_turning_mold", () -> new ItemTurningMold((byte)3, 0.1F, WOODEN_GRIP)
   );
   public static final DeferredItem<ItemTurningMold> IRON_TURNING_MOLD = REGISTRY.register(
      "iron_turning_mold", () -> new ItemTurningMold((byte)5, 0.5F, IRON_GRIP, IRON_DRILL_HEAD, IRON_THIN_KNURLED_AXLE)
   );
   public static final DeferredItem<ItemTurningMold> STEEL_TURNING_MOLD = REGISTRY.register(
      "steel_turning_mold", () -> new ItemTurningMold((byte)7, 0.6F, STEEL_GRIP, STEEL_DRILL_HEAD, STEEL_THIN_KNURLED_AXLE, STEEL_KNURLED_AXLE)
   );
   public static final DeferredItem<Item> CUTTER = REGISTRY.register("cutter", () -> new ItemToolCutter(new Properties().durability(60)));
   public static final DeferredItem<Item> FORGE_HAMMER = REGISTRY.register("forge_hammer", () -> new ItemToolCrafting(new Properties().durability(80)));
   public static final DeferredItem<Item> TREETAP = REGISTRY.register("treetap", () -> new ItemTreetap(new Properties().durability(16)));
   public static final DeferredItem<Item> WRENCH = REGISTRY.register("wrench", () -> new ItemToolWrench(new Properties().durability(120)));
   public static final DeferredItem<Item> BRONZE_AXE = REGISTRY.register("bronze_axe", () -> new IRAxe(ItemToolMaterials.BRONZE, 6, -3.1F, new Properties()));
   public static final DeferredItem<Item> BRONZE_HOE = REGISTRY.register("bronze_hoe", () -> new IRHoe(ItemToolMaterials.BRONZE, -2, -1.0F, new Properties()));
   public static final DeferredItem<Item> BRONZE_SWORD = REGISTRY.register(
      "bronze_sword",
      () -> new SwordItem(ItemToolMaterials.BRONZE, new Properties().attributes(SwordItem.createAttributes(ItemToolMaterials.BRONZE, 3.0F, -2.4F)))
   );
   public static final DeferredItem<Item> BRONZE_SHOVEL = REGISTRY.register(
      "bronze_shovel",
      () -> new ShovelItem(ItemToolMaterials.BRONZE, new Properties().attributes(SwordItem.createAttributes(ItemToolMaterials.BRONZE, 1.5F, -3.0F)))
   );
   public static final DeferredItem<Item> BRONZE_PICKAXE = REGISTRY.register(
      "bronze_pickaxe", () -> new IRPickaxe(ItemToolMaterials.BRONZE, 1, -2.8F, new Properties())
   );
   public static final DeferredItem<Item> EMPTY_MUG = REGISTRY.register("empty_mug", () -> new ItemMug(new Properties().stacksTo(1), ItemMug.MugType.empty));
   public static final DeferredItem<Item> CROP_STICK = REGISTRY.register("crop_stick", () -> new ItemCrop(new Properties()));
   public static final DeferredItem<Item> WEEDING_TROWEL = REGISTRY.register("weeding_trowel", () -> new ItemWeedingTrowel(new Properties().stacksTo(1)));
   public static final DeferredItem<Item> CROP_SEED_BAG = REGISTRY.register("crop_seed_bag", ItemCropSeed::new);
   public static final DeferredItem<Item> FERTILIZER = REGISTRY.register("fertilizer", IRItems::defaultItem);
   public static final DeferredItem<Item> FOAM_SPRAYER = REGISTRY.register("foam_sprayer", () -> new ItemSprayer(new Properties().stacksTo(1)));
   public static final DeferredItem<Item> OBSCURATOR = REGISTRY.register("obscurator", () -> new ItemObscurator(new Properties().stacksTo(1)));
   public static final DeferredItem<Item> PAINTER = REGISTRY.register("painter", ItemToolPainter::new);
   public static final DeferredItem<Item> SCANNER = REGISTRY.register("scanner", () -> new ItemScanner(400000, 512, new ElectricProperties()));
   public static final DeferredItem<Item> ADVANCED_SCANNER = REGISTRY.register("advanced_scanner", () -> new ItemScannerAdv(new ElectricProperties()));
   public static final DeferredItem<Item> CROPNALYZER = REGISTRY.register("cropnalyzer", () -> new ItemCropnalyzer(ElectricProperties.of(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> NANO_AXE = REGISTRY.register(
      "nano_axe",
      () -> new ItemElectricTools(1, 80000, 20000, 200, new ElectricProperties(), ItemToolMaterials.NANO, Set.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.WOOL))
   );
   public static final DeferredItem<Item> QUANTUM_AXE = REGISTRY.register(
      "quantum_axe",
      () -> new ItemElectricTools(
         2, 1280000, 320000, 800, new ElectricProperties(), ItemToolMaterials.QUANTUM, Set.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.WOOL)
      )
   );
   public static final DeferredItem<Item> SINGULAR_AXE = REGISTRY.register(
      "singular_axe",
      () -> new ItemElectricTools(
         3, 5120000, 1280000, 1600, new ElectricProperties(), ItemToolMaterials.SINGULAR, Set.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.WOOL)
      )
   );
   public static final DeferredItem<Item> NANO_PICKAXE = REGISTRY.register(
      "nano_pickaxe",
      () -> new ItemElectricTools(1, 80000, 20000, 200, new ElectricProperties(), ItemToolMaterials.NANO, Set.of(BlockTags.MINEABLE_WITH_PICKAXE))
   );
   public static final DeferredItem<Item> QUANTUM_PICKAXE = REGISTRY.register(
      "quantum_pickaxe",
      () -> new ItemElectricTools(2, 1280000, 320000, 800, new ElectricProperties(), ItemToolMaterials.QUANTUM, Set.of(BlockTags.MINEABLE_WITH_PICKAXE))
   );
   public static final DeferredItem<Item> SINGULAR_PICKAXE = REGISTRY.register(
      "singular_pickaxe",
      () -> new ItemElectricTools(3, 5120000, 1280000, 1600, new ElectricProperties(), ItemToolMaterials.SINGULAR, Set.of(BlockTags.MINEABLE_WITH_PICKAXE))
   );
   public static final DeferredItem<Item> NANO_SHOVEL = REGISTRY.register(
      "nano_shovel",
      () -> new ItemElectricTools(1, 80000, 20000, 200, new ElectricProperties(), ItemToolMaterials.NANO, Set.of(BlockTags.MINEABLE_WITH_SHOVEL))
   );
   public static final DeferredItem<Item> QUANTUM_SHOVEL = REGISTRY.register(
      "quantum_shovel",
      () -> new ItemElectricTools(2, 1280000, 320000, 800, new ElectricProperties(), ItemToolMaterials.QUANTUM, Set.of(BlockTags.MINEABLE_WITH_SHOVEL))
   );
   public static final DeferredItem<Item> SINGULAR_SHOVEL = REGISTRY.register(
      "singular_shovel",
      () -> new ItemElectricTools(3, 5120000, 1280000, 1600, new ElectricProperties(), ItemToolMaterials.SINGULAR, Set.of(BlockTags.MINEABLE_WITH_SHOVEL))
   );
   public static final DeferredItem<Item> DRILL = REGISTRY.register("drill", () -> new ItemDrill(120000, 400, 50, 8.0F, new ElectricProperties(), Tiers.IRON));
   public static final DeferredItem<Item> DIAMOND_DRILL = REGISTRY.register(
      "diamond_drill", () -> new ItemDrill(120000, 400, 80, 16.0F, new ElectricProperties(), Tiers.DIAMOND)
   );
   public static final DeferredItem<Item> IRIDIUM_DRILL = REGISTRY.register("iridium_drill", () -> new ItemDrillIridium(new ElectricProperties().stacksTo(1)));
   public static final DeferredItem<Item> ADVANCED_DRILL = REGISTRY.register(
      "advanced_drill", () -> new ItemDrillAdvanced(ElectricProperties.of(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> ULTIMATE_DRILL = REGISTRY.register("ultimate_drill", () -> new ItemDrillUltimate(ElectricProperties.of(Rarity.RARE)));
   public static final DeferredItem<Item> CHAINSAW = REGISTRY.register("chainsaw", () -> new ItemChainsaw(new ElectricProperties()));
   public static final DeferredItem<Item> ADVANCED_CHAINSAW = REGISTRY.register(
      "advanced_chainsaw", () -> new ItemChainsawAdvanced(ElectricProperties.of(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> ELECTRIC_TREETAP = REGISTRY.register(
      "electric_treetap", () -> new ItemTreetapElectric(new ElectricProperties().stacksTo(1))
   );
   public static final DeferredItem<ItemToolWrenchElectric> ELECTRIC_WRENCH = REGISTRY.register(
      "electric_wrench", () -> new ItemToolWrenchElectric(new ElectricProperties())
   );
   public static final DeferredItem<Item> ELECTRIC_HOE = REGISTRY.register("electric_hoe", () -> new ItemToolHoeElectric(new ElectricProperties().stacksTo(1)));
   // Урон сабли = 1 (рука) + аргумент + бонус тира (алмаз 3, незерит 4): итог 48 / 80 / 110
   // (удвоено с 24 / 40 / 55).
   public static final DeferredItem<Item> NANO_SABER = REGISTRY.register(
      "nano_saber", () -> new ItemSaber(400, 44, Tiers.DIAMOND, ElectricProperties.of(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> QUANTUM_SABER = REGISTRY.register(
      "quantum_saber", () -> new ItemSaber(600, 75, Tiers.NETHERITE, ElectricProperties.of(Rarity.RARE))
   );
   public static final DeferredItem<Item> SINGULAR_SABER = REGISTRY.register(
      "singular_saber", () -> new ItemSaber(800, 105, Tiers.NETHERITE, ElectricProperties.of(Rarity.EPIC))
   );
   public static final DeferredItem<Item> NANO_BOW = REGISTRY.register("nano_bow", () -> new ItemBow(5000, 50000, 1.5, ElectricProperties.of(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> QUANTUM_BOW = REGISTRY.register(
      "quantum_bow", () -> new ItemBow(25000, 250000, 3.0, ElectricProperties.of(Rarity.RARE))
   );
   public static final DeferredItem<Item> SINGULAR_BOW = REGISTRY.register(
      "singular_bow", () -> new ItemBow(50000, 500000, 6.0, ElectricProperties.of(Rarity.EPIC))
   );
   public static final DeferredItem<Item> WIND_METER = REGISTRY.register("wind_meter", () -> new ItemWindMeter(new ElectricProperties()));
   public static final DeferredItem<Item> MINING_LASER = REGISTRY.register(
      "mining_laser", () -> new ItemToolMiningLaser(ElectricProperties.of(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> VAJRA = REGISTRY.register(
      "vajra", () -> new ItemVajra(3000000, 60000, 3333, false, ElectricProperties.of(Rarity.RARE))
   );
   public static final DeferredItem<Item> ADVANCED_VAJRA = REGISTRY.register(
      "advanced_vajra", () -> new ItemVajra(30000000, 600000, 5000, true, ElectricProperties.of(Rarity.EPIC)) {
         public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level world, @NotNull Player player) {
            super.onCraftedBy(stack, world, player);
            if (!stack.isEnchanted()) {
               stack.enchant(world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE), 3);
            }
         }

         @Override
         public int fortuneLevel() {
            return 3;
         }
      }
   );
   public static final DeferredItem<ItemGraviTool> GRAVI_TOOL = REGISTRY.register("gravi_tool", () -> new ItemGraviTool(ElectricProperties.of(Rarity.EPIC)));
   public static final DeferredItem<Item> INACTIVE_NANO_PICKAXE = REGISTRY.register("inactive_nano_pickaxe", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_NANO_AXE = REGISTRY.register("inactive_nano_axe", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_NANO_SHOVEL = REGISTRY.register("inactive_nano_shovel", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_QUANTUM_PICKAXE = REGISTRY.register("inactive_quantum_pickaxe", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_QUANTUM_AXE = REGISTRY.register("inactive_quantum_axe", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_QUANTUM_SHOVEL = REGISTRY.register("inactive_quantum_shovel", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_SINGULAR_PICKAXE = REGISTRY.register("inactive_singular_pickaxe", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_SINGULAR_AXE = REGISTRY.register("inactive_singular_axe", IRItems::defaultItem);
   public static final DeferredItem<Item> INACTIVE_SINGULAR_SHOVEL = REGISTRY.register("inactive_singular_shovel", IRItems::defaultItem);
   public static final DeferredItem<ItemCell> CELL = REGISTRY.register("cell", () -> new ItemCell(new Properties()));
   public static final DeferredItem<Item> COMPRESSED_AIR_CELL = REGISTRY.register("compressed_air_cell", IRItems::defaultItem);
   public static final DeferredItem<Item> WEED_EX_CELL = REGISTRY.register("weed_ex_cell", ItemWeedExCell::new);
   public static final DeferredItem<Item> HYDRATION_CELL = REGISTRY.register("hydration_cell", () -> new Item(new Properties().durability(10000)) {
      @NotNull
      public InteractionResult useOn(@NotNull UseOnContext context) {
         Level level = context.getLevel();
         if (level.getBlockEntity(context.getClickedPos()) instanceof CropBlockEntity crop) {
            ItemStack itemStack = context.getPlayer().getItemInHand(context.getHand());
            int damage = this.getDamage(itemStack) + 1;
            int maxDamage = this.getMaxDamage(itemStack);
            int apply = crop.applyHydration(Math.max(0, maxDamage - damage), false);
            if (apply > 0) {
               if (!level.isClientSide) {
                  itemStack.hurtAndBreak(apply, (ServerLevel)level, context.getPlayer(), player -> {});
               }

               return InteractionResult.sidedSuccess(level.isClientSide);
            }
         }

         return super.useOn(context);
      }
   });
   public static final DeferredItem<Item> OVERCLOCKER_1_LEVEL_UPGRADE = REGISTRY.register(
      "overclocker_1_level_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.OVERCLOCKER_1_LEVEL)
   );
   public static final DeferredItem<Item> OVERCLOCKER_2_LEVEL_UPGRADE = REGISTRY.register(
      "overclocker_2_level_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.OVERCLOCKER_2_LEVEL)
   );
   public static final DeferredItem<Item> OVERCLOCKER_3_LEVEL_UPGRADE = REGISTRY.register(
      "overclocker_3_level_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.OVERCLOCKER_3_LEVEL)
   );
   public static final DeferredItem<Item> TRANSFORMER_UPGRADE = REGISTRY.register(
      "transformer_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.TRANSFORMER)
   );
   public static final DeferredItem<Item> ENERGY_STORAGE_UPGRADE = REGISTRY.register(
      "energy_storage_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.ENERGY_STORAGE)
   );
   public static final DeferredItem<Item> REDSTONE_INVERTER_UPGRADE = REGISTRY.register(
      "redstone_inverter_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.REDSTONE_INVERTER)
   );
   public static final DeferredItem<Item> EJECTOR_UPGRADE = REGISTRY.register(
      "ejector_upgrade", () -> new ItemUpgradeModule(new Properties().component(IRDataComponents.DIRECTION, null), ItemUpgradeModule.UpgradeType.EJECTOR)
   );
   public static final DeferredItem<Item> PULLING_UPGRADE = REGISTRY.register(
      "pulling_upgrade", () -> new ItemUpgradeModule(new Properties().component(IRDataComponents.DIRECTION, null), ItemUpgradeModule.UpgradeType.PULLING)
   );
   public static final DeferredItem<Item> FLUID_EJECTOR_UPGRADE = REGISTRY.register(
      "fluid_ejector_upgrade",
      () -> new ItemUpgradeModule(new Properties().component(IRDataComponents.DIRECTION, null), ItemUpgradeModule.UpgradeType.FLUID_EJECTOR)
   );
   public static final DeferredItem<Item> FLUID_PULLING_UPGRADE = REGISTRY.register(
      "fluid_pulling_upgrade",
      () -> new ItemUpgradeModule(new Properties().component(IRDataComponents.DIRECTION, null), ItemUpgradeModule.UpgradeType.FLUID_PULLING)
   );
   public static final DeferredItem<Item> STRENGTHENING_UPGRADE = REGISTRY.register(
      "strengthening_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.STRENGTHENING)
   );
   public static final DeferredItem<Item> PARTICLE_ACCELERATOR_UPGRADE = REGISTRY.register(
      "particle_accelerator_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.PARTICLE_ACCELERATOR)
   );
   public static final DeferredItem<Item> SOUNDPROOFING_UPGRADE = REGISTRY.register(
      "soundproofing_upgrade", () -> new ItemUpgradeModule(new Properties(), ItemUpgradeModule.UpgradeType.SOUNDPROOFING)
   );
   public static final DeferredItem<Item> FILLED_TIN_CAN = REGISTRY.register("filled_tin_can", () -> new ItemTinCan(new Properties()));
   public static final DeferredItem<TickArmorItem> BATTERY_PACK = REGISTRY.register(
      "battery_pack", () -> new ItemArmorBatteryPack(60000, 1024, ArmorMaterials.IRON, new ElectricProperties())
   );
   public static final DeferredItem<TickArmorItem> ADVANCED_BATTERY_PACK = REGISTRY.register(
      "advanced_battery_pack", () -> new ItemArmorBatteryPack(600000, 8192, ArmorMaterials.IRON, new ElectricProperties())
   );
   public static final DeferredItem<TickArmorItem> ENERGY_PACK = REGISTRY.register(
      "energy_pack", () -> new ItemArmorBatteryPack(2000000, 32768, ArmorMaterials.IRON, new ElectricProperties())
   );
   public static final DeferredItem<TickArmorItem> ADVANCED_LAPPACK = REGISTRY.register(
      "advanced_lappack", () -> new ItemArmorBatteryPack(30000000, 512000, ArmorMaterials.IRON, new ElectricProperties())
   );
   public static final DeferredItem<TickArmorItem> ULTIMATE_LAPPACK = REGISTRY.register(
      "ultimate_lappack", () -> new ItemArmorBatteryPack(100000000, 4096000, ArmorMaterials.IRON, new ElectricProperties())
   );
   public static final DeferredItem<TickArmorItem> JETPACK = REGISTRY.register("jetpack", () -> new ItemArmorJetpack(new Properties()));
   public static final DeferredItem<TickArmorItem> ELECTRIC_JETPACK = REGISTRY.register(
      "electric_jetpack", () -> new ItemArmorJetpackElectric(30000, new Properties())
   );
   public static final DeferredItem<TickArmorItem> ADVANCED_ELECTRIC_JETPACK = REGISTRY.register(
      "advanced_electric_jetpack", () -> new ItemArmorJetpackElectric(300000, new Properties())
   );
   public static final DeferredItem<TickArmorItem> CF_PACK = REGISTRY.register("cf_pack", ItemArmorCFPack::new);
   public static final DeferredItem<ArmorItem> HAZMAT_CHESTPLATE = REGISTRY.register("hazmat_chestplate", () -> new ItemArmorHazmat(Type.CHESTPLATE));
   public static final DeferredItem<ArmorItem> HAZMAT_HELMET = REGISTRY.register("hazmat_helmet", () -> new ItemArmorHazmat(Type.HELMET));
   public static final DeferredItem<ArmorItem> HAZMAT_LEGGINGS = REGISTRY.register("hazmat_leggings", () -> new ItemArmorHazmat(Type.LEGGINGS));
   public static final DeferredItem<ArmorItem> RUBBER_BOOTS = REGISTRY.register("rubber_boots", () -> new ItemArmorHazmat(Type.BOOTS));
   public static final DeferredItem<TickArmorItem> STATIC_BOOTS = REGISTRY.register("static_boots", () -> new ItemArmorStaticBoots(new Properties()));
   public static final DeferredItem<ArmorItem> BRONZE_CHESTPLATE = REGISTRY.register(
      "bronze_chestplate", () -> new IRArmorItem(IRArmorMaterials.BRONZE, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(15)))
   );
   public static final DeferredItem<ArmorItem> BRONZE_HELMET = REGISTRY.register(
      "bronze_helmet", () -> new IRArmorItem(IRArmorMaterials.BRONZE, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(15)))
   );
   public static final DeferredItem<ArmorItem> BRONZE_LEGGINGS = REGISTRY.register(
      "bronze_leggings", () -> new IRArmorItem(IRArmorMaterials.BRONZE, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(15)))
   );
   public static final DeferredItem<ArmorItem> BRONZE_BOOTS = REGISTRY.register(
      "bronze_boots", () -> new IRArmorItem(IRArmorMaterials.BRONZE, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(15)))
   );
   public static final DeferredItem<TickArmorItem> ALLOY_CHESTPLATE = REGISTRY.register(
      "alloy_chestplate", () -> new IRArmorItem(IRArmorMaterials.ALLOY, Type.CHESTPLATE, new Properties())
   );
   public static final DeferredItem<TickArmorItem> SOLAR_HELMET = REGISTRY.register("solar_helmet", () -> new ItemArmorSolarHelmet(new Properties()));
   public static final DeferredItem<TickArmorItem> NIGHTVISION_GOOGLES = REGISTRY.register(
      "nightvision_googles", () -> new ItemArmorNightvisionGoggles(new Properties().setNoRepair())
   );
   public static final DeferredItem<TickArmorItem> NANO_CHESTPLATE = REGISTRY.register(
      "nano_chestplate", () -> new ItemArmorNanoSuit(IRArmorMaterials.NANO_SUIT, Type.CHESTPLATE, ElectricProperties.of(Rarity.UNCOMMON))
   );
   public static final DeferredItem<TickArmorItem> NANO_HELMET = REGISTRY.register(
      "nano_helmet",
      () -> new ItemArmorNanoSuit(IRArmorMaterials.NANO_SUIT, Type.HELMET, ElectricProperties.of(Rarity.UNCOMMON).component(IRDataComponents.ACTIVE, false))
   );
   public static final DeferredItem<TickArmorItem> NANO_LEGGINGS = REGISTRY.register(
      "nano_leggings", () -> new ItemArmorNanoSuit(IRArmorMaterials.NANO_SUIT, Type.LEGGINGS, ElectricProperties.of(Rarity.UNCOMMON))
   );
   public static final DeferredItem<TickArmorItem> NANO_BOOTS = REGISTRY.register(
      "nano_boots", () -> new ItemArmorNanoSuit(IRArmorMaterials.NANO_SUIT, Type.BOOTS, ElectricProperties.of(Rarity.UNCOMMON))
   );
   public static final DeferredItem<TickArmorItem> QUANTUM_CHESTPLATE = REGISTRY.register(
      "quantum_chestplate",
      () -> new ItemArmorQuantumSuit(
         IRArmorMaterials.QUANTUM_SUIT,
         Type.CHESTPLATE,
         ElectricProperties.of(Rarity.RARE).component(IRDataComponents.ACTIVE, false).component(IRDataComponents.HOVER, false)
      )
   );
   public static final DeferredItem<TickArmorItem> QUANTUM_HELMET = REGISTRY.register(
      "quantum_helmet",
      () -> new ItemArmorQuantumSuit(IRArmorMaterials.QUANTUM_SUIT, Type.HELMET, ElectricProperties.of(Rarity.RARE).component(IRDataComponents.ACTIVE, false))
   );
   public static final DeferredItem<TickArmorItem> QUANTUM_LEGGINGS = REGISTRY.register(
      "quantum_leggings", () -> new ItemArmorQuantumSuit(IRArmorMaterials.QUANTUM_SUIT, Type.LEGGINGS, ElectricProperties.of(Rarity.RARE))
   );
   public static final DeferredItem<TickArmorItem> QUANTUM_BOOTS = REGISTRY.register(
      "quantum_boots", () -> new ItemArmorQuantumSuit(IRArmorMaterials.QUANTUM_SUIT, Type.BOOTS, ElectricProperties.of(Rarity.RARE))
   );
   public static final DeferredItem<TickArmorItem> SINGULAR_CHESTPLATE = REGISTRY.register(
      "singular_chestplate",
      () -> new ItemArmorSingularSuit(
         IRArmorMaterials.SINGULAR_SUIT,
         Type.CHESTPLATE,
         ElectricProperties.of(Rarity.EPIC).component(IRDataComponents.ACTIVE, false).component(IRDataComponents.HOVER, false)
      )
   );
   public static final DeferredItem<TickArmorItem> SINGULAR_HELMET = REGISTRY.register(
      "singular_helmet",
      () -> new ItemArmorSingularSuit(
         IRArmorMaterials.SINGULAR_SUIT, Type.HELMET, ElectricProperties.of(Rarity.EPIC).component(IRDataComponents.ACTIVE, false)
      )
   );
   public static final DeferredItem<TickArmorItem> SINGULAR_LEGGINGS = REGISTRY.register(
      "singular_leggings", () -> new ItemArmorSingularSuit(IRArmorMaterials.SINGULAR_SUIT, Type.LEGGINGS, ElectricProperties.of(Rarity.EPIC))
   );
   public static final DeferredItem<TickArmorItem> SINGULAR_BOOTS = REGISTRY.register(
      "singular_boots", () -> new ItemArmorSingularSuit(IRArmorMaterials.SINGULAR_SUIT, Type.BOOTS, ElectricProperties.of(Rarity.EPIC))
   );
   public static final DeferredItem<TickArmorItem> ADVANCED_NANO_CHESTPLATE = REGISTRY.register(
      "advanced_nano_chestplate", () -> new ItemArmorAdvancedNanoChestplate(ElectricProperties.of(Rarity.RARE))
   );
   public static final DeferredItem<TickArmorItem> GRAVI_CHESTPLATE = REGISTRY.register(
      "gravi_chestplate", () -> new ItemArmorGraviChestaplate(ElectricProperties.of(Rarity.EPIC))
   );

   public static Item defaultItem() {
      return new Item(new Properties());
   }


   private static BlockItem blockItemNotPlace(Block block, Properties properties) {
      return new BlockItem(block, properties) {
         @NotNull
         public InteractionResult useOn(@NotNull UseOnContext context) {
            return InteractionResult.PASS;
         }
      };
   }
}
