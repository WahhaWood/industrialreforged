package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.block.BrewBarrelBlock;
import com.industrialreforged.tech.block.FoamBlock;
import com.industrialreforged.tech.block.RubberLogBlock;
import com.industrialreforged.tech.block.RubberWoodBlock;
import com.industrialreforged.tech.block.ScaffoldBlock;
import com.industrialreforged.tech.block.TexGlassBlock;
import com.industrialreforged.tech.block.WallBlock;
import com.industrialreforged.tech.block.WallEntityBlock;
import com.industrialreforged.tech.block.base.AbstractAdvStandardMachineBlock;
import com.industrialreforged.tech.block.base.AbstractStandardMachineBlock;
import com.industrialreforged.tech.multiblock.tank.TankValveBlock;
import com.industrialreforged.tech.multiblock.tank.TankWallBlock;
import com.industrialreforged.tech.block.generator.electric.GeothermalGeneratorBlock;
import com.industrialreforged.tech.block.generator.electric.KineticGeneratorBlock;
import com.industrialreforged.tech.block.generator.electric.RTGeneratorBlock;
import com.industrialreforged.tech.block.generator.electric.SemifluidGeneratorBlock;
import com.industrialreforged.tech.block.generator.electric.SolarPanelBlock;
import com.industrialreforged.tech.block.generator.electric.SolarPanelConnectorBlock;
import com.industrialreforged.tech.block.generator.electric.SolidFuelGeneratorBlock;
import com.industrialreforged.tech.block.generator.electric.StirlingGeneratorBlock;
import com.industrialreforged.tech.block.generator.heat.ElectricHeatGeneratorBlock;
import com.industrialreforged.tech.block.generator.heat.FluidHeatGeneratorBlock;
import com.industrialreforged.tech.block.generator.heat.RTHeatGeneratorBlock;
import com.industrialreforged.tech.block.generator.heat.SolidHeatGeneratorBlock;
import com.industrialreforged.tech.block.generator.kinetic.ElectricKineticGeneratorBlock;
import com.industrialreforged.tech.block.generator.kinetic.ManualKineticGeneratorBlock;
import com.industrialreforged.tech.block.generator.kinetic.SteamKineticGeneratorBlock;
import com.industrialreforged.tech.block.generator.kinetic.StirlingKineticGeneratorBlock;
import com.industrialreforged.tech.block.generator.kinetic.WaterKineticGeneratorBlock;
import com.industrialreforged.tech.block.generator.kinetic.WindKineticGeneratorBlock;
import com.industrialreforged.tech.block.heat.HeatPipeBlock;
import com.industrialreforged.tech.block.machine.BlastFurnaceBlock;
import com.industrialreforged.tech.block.machine.CondenserBlock;
import com.industrialreforged.tech.block.machine.OreBreakerBlock;
import com.industrialreforged.tech.block.machine.QuantumQuarryBlock;
import com.industrialreforged.tech.block.machine.CropHarvesterBlock;
import com.industrialreforged.tech.block.machine.CropmatronBlock;
import com.industrialreforged.tech.block.machine.ElectricFurnaceBlock;
import com.industrialreforged.tech.block.machine.FermenterBlock;
import com.industrialreforged.tech.block.machine.FluidBottlerBlock;
import com.industrialreforged.tech.block.machine.InductionFurnaceBlock;
import com.industrialreforged.tech.block.machine.IronFurnaceBlock;
import com.industrialreforged.tech.block.machine.LatheBlock;
import com.industrialreforged.tech.block.machine.LiquidHeatExchangerBlock;
import com.industrialreforged.tech.block.machine.MatterGeneratorBlock;
import com.industrialreforged.tech.block.machine.MolecularFarmingBlock;
import com.industrialreforged.tech.block.machine.MolecularTransformerBlock;
import com.industrialreforged.tech.block.machine.PatternStorageBlock;
import com.industrialreforged.tech.block.machine.PumpBlock;
import com.industrialreforged.tech.block.machine.ReplicatorBlock;
import com.industrialreforged.tech.block.machine.ScannerBlock;
import com.industrialreforged.tech.block.machine.SolarDestillerBlock;
import com.industrialreforged.tech.block.machine.TeleporterBlock;
import com.industrialreforged.tech.block.reactor.NuclearReactorElectricBlock;
import com.industrialreforged.tech.block.reactor.ReactorChamberElectricBlock;
import com.industrialreforged.tech.block.storage.ChargepadBlock;
import com.industrialreforged.tech.block.storage.ElectricBlock;
import com.industrialreforged.tech.block.storage.LuminatorBlock;
import com.industrialreforged.tech.blockentity.machine.BlockCutterBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CannerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CentrifugeBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity;
import com.industrialreforged.tech.blockentity.machine.SolidCannerBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ChargepadBatBoxBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ChargepadCESUBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ChargepadEFSUBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ChargepadMFEBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ChargepadMFSUBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricBatBoxBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricCESUBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricEFSUBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricMFEBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricMFSUBlockEntity;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.crop.IRCropBlock;
import com.industrialreforged.tech.crop.IRCropType;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public final class IRBlocks {
   public static final Blocks REGISTRY = DeferredRegister.createBlocks("industrialreforged");
   public static final DeferredBlock<Block> BREW_BARREL = REGISTRY.register("brew_barrel", BrewBarrelBlock::new);
   public static final DeferredBlock<Block> BASALT = REGISTRY.register(
      "basalt", () -> new Block(Properties.of().strength(20.0F, 45.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> TIN_ORE = REGISTRY.register("tin_ore", IRBlocks::createStoneOre);
   public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = REGISTRY.register("deepslate_tin_ore", IRBlocks::createDeepslateOre);
   public static final DeferredBlock<Block> LEAD_ORE = REGISTRY.register("lead_ore", IRBlocks::createStoneOre);
   public static final DeferredBlock<Block> DEEPSLATE_LEAD_ORE = REGISTRY.register("deepslate_lead_ore", IRBlocks::createDeepslateOre);
   public static final DeferredBlock<Block> SILVER_ORE = REGISTRY.register("silver_ore", IRBlocks::createStoneOre);
   public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = REGISTRY.register("deepslate_silver_ore", IRBlocks::createDeepslateOre);
   public static final DeferredBlock<Block> NICKEL_ORE = REGISTRY.register("nickel_ore", IRBlocks::createStoneOre);
   public static final DeferredBlock<Block> DEEPSLATE_NICKEL_ORE = REGISTRY.register("deepslate_nickel_ore", IRBlocks::createDeepslateOre);
   public static final DeferredBlock<Block> URANIUM_ORE = REGISTRY.register("uranium_ore", IRBlocks::createStoneOre);
   public static final DeferredBlock<Block> DEEPSLATE_URANIUM_ORE = REGISTRY.register("deepslate_uranium_ore", IRBlocks::createDeepslateOre);
   public static final DeferredBlock<Block> TITANIUM_ORE = REGISTRY.register("titanium_ore", IRBlocks::createStoneOre);
   public static final DeferredBlock<Block> DEEPSLATE_TITANIUM_ORE = REGISTRY.register("deepslate_titanium_ore", IRBlocks::createDeepslateOre);
   public static final DeferredBlock<Block> TUNGSTEN_ORE = REGISTRY.register("tungsten_ore", IRBlocks::createStoneOre);
   public static final DeferredBlock<Block> DEEPSLATE_TUNGSTEN_ORE = REGISTRY.register("deepslate_tungsten_ore", IRBlocks::createDeepslateOre);
   public static final DeferredBlock<Block> TOXIC_ORE = REGISTRY.register(
      "toxic_ore", () -> new Block(Properties.of().strength(3.0F, 3.0F).sound(SoundType.NETHERRACK).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> TIN_BLOCK = REGISTRY.register(
      "tin_block", () -> new Block(Properties.of().strength(8.0F, 40.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LEAD_BLOCK = REGISTRY.register(
      "lead_block", () -> new Block(Properties.of().strength(6.0F, 30.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> SILVER_BLOCK = REGISTRY.register(
      "silver_block", () -> new Block(Properties.of().strength(6.0F, 30.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> NICKEL_BLOCK = REGISTRY.register(
      "nickel_block", () -> new Block(Properties.of().strength(5.0F, 20.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRONZE_BLOCK = REGISTRY.register(
      "bronze_block", () -> new Block(Properties.of().strength(6.0F, 30.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> STEEL_BLOCK = REGISTRY.register(
      "steel_block", () -> new Block(Properties.of().strength(12.0F, 100.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> URANIUM_BLOCK = REGISTRY.register(
      "uranium_block", () -> new Block(Properties.of().strength(6.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> REINFORCED_STONE = REGISTRY.register(
      "reinforced_stone", com.industrialreforged.tech.block.ReinforcedCasingBlock::new
   );
   public static final DeferredBlock<Block> TANK_WALL = REGISTRY.register("tank_wall", TankWallBlock::new);
   public static final DeferredBlock<Block> TANK_VALVE = REGISTRY.register("tank_valve", TankValveBlock::new);
   public static final DeferredBlock<Block> TANK_CONTROLLER = REGISTRY.register(
      "tank_controller", com.industrialreforged.tech.multiblock.tank.TankControllerBlock::new
   );
   public static final DeferredBlock<Block> IRON_FURNACE = REGISTRY.register("iron_furnace", IronFurnaceBlock::new);
   public static final DeferredBlock<Block> MACHINE = REGISTRY.register(
      "machine", () -> new Block(Properties.of().strength(5.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> ADVANCED_MACHINE = REGISTRY.register(
      "advanced_machine", () -> new Block(Properties.of().strength(8.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> IRIDIUM_MACHINE = REGISTRY.register(
      "iridium_machine", () -> new Block(Properties.of().strength(8.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> REACTOR_VESSEL = REGISTRY.register(
      "reactor_vessel", () -> new Block(Properties.of().strength(40.0F, 90.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<LeavesBlock> RUBBER_LEAVES = REGISTRY.register(
      "rubber_leaves",
      () -> new LeavesBlock(
         Properties.of()
            .strength(0.2F)
            .mapColor(MapColor.PLANT)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn(IRBlocks::canSpawnOnLeaves)
            .isSuffocating(IRBlocks::never)
            .isViewBlocking(IRBlocks::never)
      )
   );
   public static final DeferredBlock<RubberLogBlock> RUBBER_LOG = REGISTRY.register(
      "rubber_log",
      () -> new RubberLogBlock(
         Properties.of()
            .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Axis.Y ? MapColor.PODZOL : MapColor.COLOR_BROWN)
            .strength(1.0F)
            .sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<RotatedPillarBlock> STRIPPED_RUBBER_LOG = REGISTRY.register(
      "stripped_rubber_log",
      () -> new RotatedPillarBlock(
         Properties.of()
            .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Axis.Y ? MapColor.PODZOL : MapColor.COLOR_BROWN)
            .strength(1.0F)
            .sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<RubberWoodBlock> RUBBER_WOOD = REGISTRY.register(
      "rubber_wood", () -> new RubberWoodBlock(Properties.of().strength(1.0F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> STRIPPED_RUBBER_WOOD = REGISTRY.register(
      "stripped_rubber_wood", () -> new Block(Properties.of().strength(1.0F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> RUBBER_SAPLING = REGISTRY.register(
      "rubber_sapling",
      () -> new SaplingBlock(
         new TreeGrower(
            "rubber_tree", Optional.empty(), Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, IR.location("rubber_tree"))), Optional.empty()
         ),
         Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> WOODEN_SCAFFOLD = REGISTRY.register(
      "wooden_scaffold", () -> new ScaffoldBlock(Properties.of().noOcclusion().strength(0.5F, 0.12F).randomTicks().sound(SoundType.WOOD), 2)
   );
   public static final DeferredBlock<Block> REINFORCED_WOODEN_SCAFFOLD = REGISTRY.register(
      "reinforced_wooden_scaffold", () -> new ScaffoldBlock(Properties.of().noOcclusion().strength(0.6F, 0.24F).randomTicks().sound(SoundType.WOOD), 5)
   );
   public static final DeferredBlock<Block> IRON_SCAFFOLD = REGISTRY.register(
      "iron_scaffold", () -> new ScaffoldBlock(Properties.of().noOcclusion().strength(0.8F, 6.0F).randomTicks().sound(SoundType.METAL), 5)
   );
   public static final DeferredBlock<Block> REINFORCED_IRON_SCAFFOLD = REGISTRY.register(
      "reinforced_iron_scaffold", () -> new ScaffoldBlock(Properties.of().noOcclusion().strength(1.0F, 8.0F).randomTicks().sound(SoundType.METAL), 12)
   );
   public static final DeferredBlock<Block> IRON_FENCE = REGISTRY.register("iron_fence", () -> new FenceBlock(Properties.of().strength(5.0F, 10.0F)));
   public static final DeferredBlock<Block> REINFORCED_GLASS = REGISTRY.register("reinforced_glass", TexGlassBlock::new);
   public static final DeferredBlock<Block> FOAM = REGISTRY.register(
      "foam", () -> new FoamBlock(Properties.of().noOcclusion().strength(0.01F, 10.0F).randomTicks().sound(SoundType.WOOL))
   );
   private static final Properties WALL_SETTINGS = Properties.of().strength(3.0F, 30.0F).requiresCorrectToolForDrops().sound(SoundType.STONE);
   public static final DeferredBlock<Block> WHITE_WALL = REGISTRY.register("white_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.WHITE));
   public static final DeferredBlock<Block> ORANGE_WALL = REGISTRY.register("orange_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.ORANGE));
   public static final DeferredBlock<Block> MAGENTA_WALL = REGISTRY.register("magenta_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.MAGENTA));
   public static final DeferredBlock<Block> LIGHT_BLUE_WALL = REGISTRY.register("light_blue_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.LIGHT_BLUE));
   public static final DeferredBlock<Block> YELLOW_WALL = REGISTRY.register("yellow_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.YELLOW));
   public static final DeferredBlock<Block> LIME_WALL = REGISTRY.register("lime_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.LIME));
   public static final DeferredBlock<Block> PINK_WALL = REGISTRY.register("pink_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.PINK));
   public static final DeferredBlock<Block> GRAY_WALL = REGISTRY.register("gray_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.GRAY));
   public static final DeferredBlock<Block> LIGHT_GRAY_WALL = REGISTRY.register("light_gray_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.LIGHT_GRAY));
   public static final DeferredBlock<Block> CYAN_WALL = REGISTRY.register("cyan_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.CYAN));
   public static final DeferredBlock<Block> PURPLE_WALL = REGISTRY.register("purple_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.PURPLE));
   public static final DeferredBlock<Block> BLUE_WALL = REGISTRY.register("blue_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.BLUE));
   public static final DeferredBlock<Block> BROWN_WALL = REGISTRY.register("brown_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.BROWN));
   public static final DeferredBlock<Block> GREEN_WALL = REGISTRY.register("green_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.GREEN));
   public static final DeferredBlock<Block> RED_WALL = REGISTRY.register("red_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.RED));
   public static final DeferredBlock<Block> BLACK_WALL = REGISTRY.register("black_wall", () -> new WallBlock(WALL_SETTINGS, DyeColor.BLACK));
   public static final DeferredBlock<Block> OBSCURED_WALL = REGISTRY.register("obscured_wall", () -> new WallEntityBlock(WALL_SETTINGS));
   public static final DeferredBlock<Block> REINFORCED_DOOR = REGISTRY.register(
      "reinforced_door", () -> new DoorBlock(BlockSetType.IRON, Properties.of().strength(50.0F, 150.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> GENERATOR = REGISTRY.register("generator", SolidFuelGeneratorBlock::new);
   public static final DeferredBlock<Block> STIRLING_GENERATOR = REGISTRY.register("stirling_generator", StirlingGeneratorBlock::new);
   public static final DeferredBlock<Block> GEO_GENERATOR = REGISTRY.register("geo_generator", GeothermalGeneratorBlock::new);
   public static final DeferredBlock<Block> SEMIFLUID_GENERATOR = REGISTRY.register("semifluid_generator", SemifluidGeneratorBlock::new);
   public static final DeferredBlock<Block> KINETIC_GENERATOR = REGISTRY.register("kinetic_generator", KineticGeneratorBlock::new);
   public static final DeferredBlock<Block> RT_GENERATOR = REGISTRY.register("rt_generator", RTGeneratorBlock::new);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_1_LEVEL = registerSolarPanel(1);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_2_LEVEL = registerSolarPanel(2);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_3_LEVEL = registerSolarPanel(3);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_4_LEVEL = registerSolarPanel(4);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_5_LEVEL = registerSolarPanel(5);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_6_LEVEL = registerSolarPanel(6);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_7_LEVEL = registerSolarPanel(7);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_8_LEVEL = registerSolarPanel(8);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_9_LEVEL = registerSolarPanel(9);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_10_LEVEL = registerSolarPanel(10);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_11_LEVEL = registerSolarPanel(11);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_12_LEVEL = registerSolarPanel(12);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_13_LEVEL = registerSolarPanel(13);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_14_LEVEL = registerSolarPanel(14);
   public static final DeferredBlock<SolarPanelBlock> SOLAR_GENERATOR_15_LEVEL = registerSolarPanel(15);
   public static final DeferredBlock<Block> SOLAR_CONNECTOR = REGISTRY.register("solar_connector", SolarPanelConnectorBlock::new);
   public static final DeferredBlock<Block> NUCLEAR_REACTOR = REGISTRY.register("nuclear_reactor", NuclearReactorElectricBlock::new);
   public static final DeferredBlock<Block> REACTOR_CHAMBER = REGISTRY.register("reactor_chamber", ReactorChamberElectricBlock::new);
   public static final DeferredBlock<Block> ELECTRIC_KINETIC_GENERATOR = REGISTRY.register("electric_kinetic_generator", ElectricKineticGeneratorBlock::new);
   public static final DeferredBlock<Block> MANUAL_KINETIC_GENERATOR = REGISTRY.register("manual_kinetic_generator", ManualKineticGeneratorBlock::new);
   public static final DeferredBlock<Block> STEAM_KINETIC_GENERATOR = REGISTRY.register("steam_kinetic_generator", SteamKineticGeneratorBlock::new);
   public static final DeferredBlock<Block> STIRLING_KINETIC_GENERATOR = REGISTRY.register("stirling_kinetic_generator", StirlingKineticGeneratorBlock::new);
   public static final DeferredBlock<Block> WATER_KINETIC_GENERATOR = REGISTRY.register("water_kinetic_generator", WaterKineticGeneratorBlock::new);
   public static final DeferredBlock<Block> WIND_KINETIC_GENERATOR = REGISTRY.register("wind_kinetic_generator", WindKineticGeneratorBlock::new);
   public static final DeferredBlock<Block> ELECTRIC_HEAT_GENERATOR = REGISTRY.register("electric_heat_generator", ElectricHeatGeneratorBlock::new);
   public static final DeferredBlock<Block> FLUID_HEAT_GENERATOR = REGISTRY.register("fluid_heat_generator", FluidHeatGeneratorBlock::new);
   public static final DeferredBlock<Block> RT_HEAT_GENERATOR = REGISTRY.register("rt_heat_generator", RTHeatGeneratorBlock::new);
   public static final DeferredBlock<Block> SOLID_HEAT_GENERATOR = REGISTRY.register("solid_heat_generator", SolidHeatGeneratorBlock::new);
   public static final DeferredBlock<Block> ELECTRIC_FURNACE_1_LEVEL = REGISTRY.register(
      "electric_furnace_1_level", () -> new ElectricFurnaceBlock<ElectricFurnace1LevelBlockEntity>(IRBlockEntities.ELECTRIC_FURNACE_1_LEVEL)
   );
   public static final DeferredBlock<Block> ELECTRIC_FURNACE_2_LEVEL = REGISTRY.register(
      "electric_furnace_2_level", () -> new ElectricFurnaceBlock<ElectricFurnace2LevelBlockEntity>(IRBlockEntities.ELECTRIC_FURNACE_2_LEVEL)
   );
   public static final DeferredBlock<Block> ELECTRIC_FURNACE_3_LEVEL = REGISTRY.register(
      "electric_furnace_3_level", () -> new ElectricFurnaceBlock<ElectricFurnace3LevelBlockEntity>(IRBlockEntities.ELECTRIC_FURNACE_3_LEVEL)
   );
   public static final DeferredBlock<Block> ELECTRIC_FURNACE_4_LEVEL = REGISTRY.register(
      "electric_furnace_4_level", () -> new ElectricFurnaceBlock<ElectricFurnace4LevelBlockEntity>(IRBlockEntities.ELECTRIC_FURNACE_4_LEVEL)
   );
   public static final DeferredBlock<Block> MACERATOR_1_LEVEL = REGISTRY.register(
      "macerator_1_level", () -> new AbstractStandardMachineBlock<Macerator1LevelBlockEntity>(IRBlockEntities.MACERATOR_1_LEVEL)
   );
   public static final DeferredBlock<Block> MACERATOR_2_LEVEL = REGISTRY.register(
      "macerator_2_level", () -> new AbstractStandardMachineBlock<Macerator2LevelBlockEntity>(IRBlockEntities.MACERATOR_2_LEVEL)
   );
   public static final DeferredBlock<Block> MACERATOR_3_LEVEL = REGISTRY.register(
      "macerator_3_level", () -> new AbstractStandardMachineBlock<Macerator3LevelBlockEntity>(IRBlockEntities.MACERATOR_3_LEVEL)
   );
   public static final DeferredBlock<Block> MACERATOR_4_LEVEL = REGISTRY.register(
      "macerator_4_level", () -> new AbstractStandardMachineBlock<Macerator4LevelBlockEntity>(IRBlockEntities.MACERATOR_4_LEVEL)
   );
   public static final DeferredBlock<Block> RECYCLER_1_LEVEL = REGISTRY.register(
      "recycler_1_level", () -> new AbstractStandardMachineBlock<Recycler1LevelBlockEntity>(IRBlockEntities.RECYCLER_1_LEVEL)
   );
   public static final DeferredBlock<Block> RECYCLER_2_LEVEL = REGISTRY.register(
      "recycler_2_level", () -> new AbstractStandardMachineBlock<Recycler2LevelBlockEntity>(IRBlockEntities.RECYCLER_2_LEVEL)
   );
   public static final DeferredBlock<Block> RECYCLER_3_LEVEL = REGISTRY.register(
      "recycler_3_level", () -> new AbstractStandardMachineBlock<Recycler3LevelBlockEntity>(IRBlockEntities.RECYCLER_3_LEVEL)
   );
   public static final DeferredBlock<Block> RECYCLER_4_LEVEL = REGISTRY.register(
      "recycler_4_level", () -> new AbstractStandardMachineBlock<Recycler4LevelBlockEntity>(IRBlockEntities.RECYCLER_4_LEVEL)
   );
   public static final DeferredBlock<Block> EXTRACTOR_1_LEVEL = REGISTRY.register(
      "extractor_1_level", () -> new AbstractStandardMachineBlock<Extractor1LevelBlockEntity>(IRBlockEntities.EXTRACTOR_1_LEVEL)
   );
   public static final DeferredBlock<Block> EXTRACTOR_2_LEVEL = REGISTRY.register(
      "extractor_2_level", () -> new AbstractStandardMachineBlock<Extractor2LevelBlockEntity>(IRBlockEntities.EXTRACTOR_2_LEVEL)
   );
   public static final DeferredBlock<Block> EXTRACTOR_3_LEVEL = REGISTRY.register(
      "extractor_3_level", () -> new AbstractStandardMachineBlock<Extractor3LevelBlockEntity>(IRBlockEntities.EXTRACTOR_3_LEVEL)
   );
   public static final DeferredBlock<Block> EXTRACTOR_4_LEVEL = REGISTRY.register(
      "extractor_4_level", () -> new AbstractStandardMachineBlock<Extractor4LevelBlockEntity>(IRBlockEntities.EXTRACTOR_4_LEVEL)
   );
   public static final DeferredBlock<Block> COMPRESSOR_1_LEVEL = REGISTRY.register(
      "compressor_1_level", () -> new AbstractStandardMachineBlock<Compressor1LevelBlockEntity>(IRBlockEntities.COMPRESSOR_1_LEVEL)
   );
   public static final DeferredBlock<Block> COMPRESSOR_2_LEVEL = REGISTRY.register(
      "compressor_2_level", () -> new AbstractStandardMachineBlock<Compressor2LevelBlockEntity>(IRBlockEntities.COMPRESSOR_2_LEVEL)
   );
   public static final DeferredBlock<Block> COMPRESSOR_3_LEVEL = REGISTRY.register(
      "compressor_3_level", () -> new AbstractStandardMachineBlock<Compressor3LevelBlockEntity>(IRBlockEntities.COMPRESSOR_3_LEVEL)
   );
   public static final DeferredBlock<Block> COMPRESSOR_4_LEVEL = REGISTRY.register(
      "compressor_4_level", () -> new AbstractStandardMachineBlock<Compressor4LevelBlockEntity>(IRBlockEntities.COMPRESSOR_4_LEVEL)
   );
   public static final DeferredBlock<Block> METAL_FORMER_1_LEVEL = REGISTRY.register(
      "metal_former_1_level", () -> new AbstractStandardMachineBlock<MetalFormer1LevelBlockEntity>(IRBlockEntities.METAL_FORMER_1_LEVEL)
   );
   public static final DeferredBlock<Block> METAL_FORMER_2_LEVEL = REGISTRY.register(
      "metal_former_2_level", () -> new AbstractStandardMachineBlock<MetalFormer2LevelBlockEntity>(IRBlockEntities.METAL_FORMER_2_LEVEL)
   );
   public static final DeferredBlock<Block> METAL_FORMER_3_LEVEL = REGISTRY.register(
      "metal_former_3_level", () -> new AbstractStandardMachineBlock<MetalFormer3LevelBlockEntity>(IRBlockEntities.METAL_FORMER_3_LEVEL)
   );
   public static final DeferredBlock<Block> METAL_FORMER_4_LEVEL = REGISTRY.register(
      "metal_former_4_level", () -> new AbstractStandardMachineBlock<MetalFormer4LevelBlockEntity>(IRBlockEntities.METAL_FORMER_4_LEVEL)
   );
   public static final DeferredBlock<Block> ORE_WASHING_PLANT = REGISTRY.register(
      "ore_washing_plant", () -> new AbstractStandardMachineBlock<OreWashingPlantBlockEntity>(IRBlockEntities.ORE_WASHING_PLANT)
   );
   public static final DeferredBlock<Block> CANNER = REGISTRY.register(
      "canner", () -> new AbstractStandardMachineBlock<CannerBlockEntity>(IRBlockEntities.CANNER)
   );
   public static final DeferredBlock<Block> SOLID_CANNER = REGISTRY.register(
      "solid_canner", () -> new AbstractStandardMachineBlock<SolidCannerBlockEntity>(IRBlockEntities.SOLID_CANNER)
   );
   public static final DeferredBlock<Block> TURNING = REGISTRY.register("turning", LatheBlock::new);
   public static final DeferredBlock<Block> INDUCTION_FURNACE = REGISTRY.register("induction_furnace", InductionFurnaceBlock::new);
   public static final DeferredBlock<Block> CENTRIFUGE = REGISTRY.register(
      "centrifuge", () -> new AbstractStandardMachineBlock<CentrifugeBlockEntity>(IRBlockEntities.CENTRIFUGE)
   );
   public static final DeferredBlock<Block> BLAST_FURNACE = REGISTRY.register("blast_furnace", BlastFurnaceBlock::new);
   public static final DeferredBlock<Block> BLOCK_CUTTER = REGISTRY.register(
      "block_cutter", () -> new AbstractAdvStandardMachineBlock<BlockCutterBlockEntity>(IRBlockEntities.BLOCK_CUTTER)
   );
   public static final DeferredBlock<Block> TELEPORTER = REGISTRY.register("teleporter", TeleporterBlock::new);
   public static final DeferredBlock<Block> MATTER_GENERATOR = REGISTRY.register("matter_generator", MatterGeneratorBlock::new);
   public static final DeferredBlock<Block> REPLICATOR = REGISTRY.register("replicator", ReplicatorBlock::new);
   public static final DeferredBlock<Block> UU_SCANNER = REGISTRY.register("uu_scanner", ScannerBlock::new);
   public static final DeferredBlock<Block> PATTERN_STORAGE = REGISTRY.register("pattern_storage", PatternStorageBlock::new);
   public static final DeferredBlock<Block> MOLECULAR_FARMING = REGISTRY.register("molecular_farming", MolecularFarmingBlock::new);
   public static final DeferredBlock<Block> MOLECULAR_TRANSFORMER = REGISTRY.register("molecular_transformer", MolecularTransformerBlock::new);
   public static final DeferredBlock<Block> PUMP = REGISTRY.register("pump", PumpBlock::new);
   public static final DeferredBlock<Block> FLUID_BOTTLER = REGISTRY.register("fluid_bottler", FluidBottlerBlock::new);
   public static final DeferredBlock<Block> SOLAR_DISTILLER = REGISTRY.register("solar_distiller", SolarDestillerBlock::new);
   public static final DeferredBlock<Block> CONDENSER = REGISTRY.register("condenser", CondenserBlock::new);
   public static final DeferredBlock<Block> QUANTUM_QUARRY = REGISTRY.register("quantum_quarry", QuantumQuarryBlock::new);
   public static final DeferredBlock<Block> ORE_BREAKER = REGISTRY.register("ore_breaker", OreBreakerBlock::new);
   public static final DeferredBlock<Block> LIQUID_HEAT_EXCHANGER = REGISTRY.register("liquid_heat_exchanger", LiquidHeatExchangerBlock::new);
   public static final DeferredBlock<Block> CROPMATRON = REGISTRY.register("cropmatron", CropmatronBlock::new);
   public static final DeferredBlock<Block> CROP_HARVESTER = REGISTRY.register("crop_harvester", CropHarvesterBlock::new);
   public static final DeferredBlock<Block> FERMENTER = REGISTRY.register("fermenter", FermenterBlock::new);
   public static final DeferredBlock<Block> HEAT_PIPE_1 = REGISTRY.register("heat_pipe_1", () -> new HeatPipeBlock(0));
   public static final DeferredBlock<Block> HEAT_PIPE_2 = REGISTRY.register("heat_pipe_2", () -> new HeatPipeBlock(1));
   public static final DeferredBlock<Block> HEAT_PIPE_3 = REGISTRY.register("heat_pipe_3", () -> new HeatPipeBlock(2));
   public static final DeferredBlock<Block> BATBOX = REGISTRY.register("batbox", () -> new ElectricBlock<ElectricBatBoxBlockEntity>(IRBlockEntities.BATBOX));
   public static final DeferredBlock<Block> CESU = REGISTRY.register("cesu", () -> new ElectricBlock<ElectricCESUBlockEntity>(IRBlockEntities.CESU));
   public static final DeferredBlock<Block> MFE = REGISTRY.register("mfe", () -> new ElectricBlock<ElectricMFEBlockEntity>(IRBlockEntities.MFE));
   public static final DeferredBlock<Block> MFSU = REGISTRY.register("mfsu", () -> new ElectricBlock<ElectricMFSUBlockEntity>(IRBlockEntities.MFSU));
   public static final DeferredBlock<Block> EFSU = REGISTRY.register("efsu", () -> new ElectricBlock<ElectricEFSUBlockEntity>(IRBlockEntities.EFSU));
   public static final DeferredBlock<Block> BATBOX_CHARGEPAD = REGISTRY.register(
      "batbox_chargepad", () -> new ChargepadBlock<ChargepadBatBoxBlockEntity>(IRBlockEntities.BATBOX_CHARGEPAD)
   );
   public static final DeferredBlock<Block> CESU_CHARGEPAD = REGISTRY.register(
      "cesu_chargepad", () -> new ChargepadBlock<ChargepadCESUBlockEntity>(IRBlockEntities.CESU_CHARGEPAD)
   );
   public static final DeferredBlock<Block> MFE_CHARGEPAD = REGISTRY.register(
      "mfe_chargepad", () -> new ChargepadBlock<ChargepadMFEBlockEntity>(IRBlockEntities.MFE_CHARGEPAD)
   );
   public static final DeferredBlock<Block> MFSU_CHARGEPAD = REGISTRY.register(
      "mfsu_chargepad", () -> new ChargepadBlock<ChargepadMFSUBlockEntity>(IRBlockEntities.MFSU_CHARGEPAD)
   );
   public static final DeferredBlock<Block> EFSU_CHARGEPAD = REGISTRY.register(
      "efsu_chargepad", () -> new ChargepadBlock<ChargepadEFSUBlockEntity>(IRBlockEntities.EFSU_CHARGEPAD)
   );
   public static final DeferredBlock<Block> LUMINATOR = REGISTRY.register("luminator", LuminatorBlock::new);
   private static final Properties CROP_PROPERTIES = Properties.of().strength(0.8F, 0.2F).sound(SoundType.CROP).noCollission();
   public static final DeferredBlock<IRCropBlock> CROP_STICK = REGISTRY.register(
      "crop_stick", () -> IRCropBlock.create(IRBlockEntities.CROP_STICK, IRCropType.none)
   );
   public static final DeferredBlock<IRCropBlock> WEED_CROP = REGISTRY.register(
      "weed_crop", () -> IRCropBlock.create(IRBlockEntities.WEED_CROP, IRCropType.weed)
   );
   public static final DeferredBlock<IRCropBlock> WHEAT_CROP = REGISTRY.register(
      "wheat_crop", () -> IRCropBlock.create(IRBlockEntities.WHEAT_CROP, IRCropType.wheat)
   );
   public static final DeferredBlock<IRCropBlock> CARROTS_CROP = REGISTRY.register(
      "carrots_crop", () -> IRCropBlock.create(IRBlockEntities.CARROTS_CROP, IRCropType.carrots)
   );
   public static final DeferredBlock<IRCropBlock> POTATO_CROP = REGISTRY.register(
      "potato_crop", () -> IRCropBlock.create(IRBlockEntities.POTATO_CROP, IRCropType.potato)
   );
   public static final DeferredBlock<IRCropBlock> BEETROOTS_CROP = REGISTRY.register(
      "beetroots_crop", () -> IRCropBlock.create(IRBlockEntities.BEETROOTS_CROP, IRCropType.beetroots)
   );
   public static final DeferredBlock<IRCropBlock> PUMPKIN_CROP = REGISTRY.register(
      "pumpkin_crop", () -> IRCropBlock.create(IRBlockEntities.PUMPKIN_CROP, IRCropType.pumpkin)
   );
   public static final DeferredBlock<IRCropBlock> MELON_CROP = REGISTRY.register(
      "melon_crop", () -> IRCropBlock.create(IRBlockEntities.MELON_CROP, IRCropType.melon)
   );
   public static final DeferredBlock<IRCropBlock> DANDELION_CROP = REGISTRY.register(
      "dandelion_crop", () -> IRCropBlock.create(IRBlockEntities.DANDELION_CROP, IRCropType.dandelion)
   );
   public static final DeferredBlock<IRCropBlock> POPPY_CROP = REGISTRY.register(
      "poppy_crop", () -> IRCropBlock.create(IRBlockEntities.POPPY_CROP, IRCropType.poppy)
   );
   public static final DeferredBlock<IRCropBlock> BLACKTHORN_CROP = REGISTRY.register(
      "blackthorn_crop", () -> IRCropBlock.create(IRBlockEntities.BLACKTHORN_CROP, IRCropType.blackthorn)
   );
   public static final DeferredBlock<IRCropBlock> TULIP_CROP = REGISTRY.register(
      "tulip_crop", () -> IRCropBlock.create(IRBlockEntities.TULIP_CROP, IRCropType.tulip)
   );
   public static final DeferredBlock<IRCropBlock> CYAZINT_CROP = REGISTRY.register(
      "cyazint_crop", () -> IRCropBlock.create(IRBlockEntities.CYAZINT_CROP, IRCropType.cyazint)
   );
   public static final DeferredBlock<IRCropBlock> VENOMILIA_CROP = REGISTRY.register(
      "venomilia_crop", () -> IRCropBlock.create(IRBlockEntities.VENOMILIA_CROP, IRCropType.venomilia)
   );
   public static final DeferredBlock<IRCropBlock> REED_CROP = REGISTRY.register(
      "reed_crop", () -> IRCropBlock.create(IRBlockEntities.REED_CROP, IRCropType.reed)
   );
   public static final DeferredBlock<IRCropBlock> STICKY_REED_CROP = REGISTRY.register(
      "sticky_reed_crop", () -> IRCropBlock.create(IRBlockEntities.STICKY_REED_CROP, IRCropType.stickyReed)
   );
   public static final DeferredBlock<IRCropBlock> COCOA_CROP = REGISTRY.register(
      "cocoa_crop", () -> IRCropBlock.create(IRBlockEntities.COCOA_CROP, IRCropType.cocoa)
   );
   public static final DeferredBlock<IRCropBlock> FLAX_CROP = REGISTRY.register(
      "flax_crop", () -> IRCropBlock.create(IRBlockEntities.FLAX_CROP, IRCropType.flax)
   );
   public static final DeferredBlock<IRCropBlock> RED_MUSHROOM_CROP = REGISTRY.register(
      "red_mushroom_crop", () -> IRCropBlock.create(IRBlockEntities.RED_MUSHROOM_CROP, IRCropType.redMushroom)
   );
   public static final DeferredBlock<IRCropBlock> BROWN_MUSHROOM_CROP = REGISTRY.register(
      "brown_mushroom_crop", () -> IRCropBlock.create(IRBlockEntities.BROWN_MUSHROOM_CROP, IRCropType.brownMushroom)
   );
   public static final DeferredBlock<IRCropBlock> NETHER_WART_CROP = REGISTRY.register(
      "nether_wart_crop", () -> IRCropBlock.create(IRBlockEntities.NETHER_WART_CROP, IRCropType.netherWart)
   );
   public static final DeferredBlock<IRCropBlock> TERRA_WART_CROP = REGISTRY.register(
      "terra_wart_crop", () -> IRCropBlock.create(IRBlockEntities.TERRA_WART_CROP, IRCropType.terraWart)
   );
   public static final DeferredBlock<IRCropBlock> OAK_SAPLING_CROP = REGISTRY.register(
      "oak_sapling_crop", () -> IRCropBlock.create(IRBlockEntities.OAK_SAPLING_CROP, IRCropType.oakSapling)
   );
   public static final DeferredBlock<IRCropBlock> SPRUCE_SAPLING_CROP = REGISTRY.register(
      "spruce_sapling_crop", () -> IRCropBlock.create(IRBlockEntities.SPRUCE_SAPLING_CROP, IRCropType.spruceSapling)
   );
   public static final DeferredBlock<IRCropBlock> BIRCH_SAPLING_CROP = REGISTRY.register(
      "birch_sapling_crop", () -> IRCropBlock.create(IRBlockEntities.BIRCH_SAPLING_CROP, IRCropType.birchSapling)
   );
   public static final DeferredBlock<IRCropBlock> JUNGLE_SAPLING_CROP = REGISTRY.register(
      "jungle_sapling_crop", () -> IRCropBlock.create(IRBlockEntities.JUNGLE_SAPLING_CROP, IRCropType.jungleSapling)
   );
   public static final DeferredBlock<IRCropBlock> ACACIA_SAPLING_CROP = REGISTRY.register(
      "acacia_sapling_crop", () -> IRCropBlock.create(IRBlockEntities.ACACIA_SAPLING_CROP, IRCropType.acaciaSapling)
   );
   public static final DeferredBlock<IRCropBlock> DARK_OAK_SAPLING_CROP = REGISTRY.register(
      "dark_oak_sapling_crop", () -> IRCropBlock.create(IRBlockEntities.DARK_OAK_SAPLING_CROP, IRCropType.darkOakSapling)
   );
   public static final DeferredBlock<IRCropBlock> FERRU_CROP = REGISTRY.register(
      "ferru_crop", () -> IRCropBlock.create(IRBlockEntities.FERRU_CROP, IRCropType.ferru)
   );
   public static final DeferredBlock<IRCropBlock> CYPRIUM_CROP = REGISTRY.register(
      "cyprium_crop", () -> IRCropBlock.create(IRBlockEntities.CYPRIUM_CROP, IRCropType.cyprium)
   );
   public static final DeferredBlock<IRCropBlock> STAGNIUM_CROP = REGISTRY.register(
      "stagnium_crop", () -> IRCropBlock.create(IRBlockEntities.STAGNIUM_CROP, IRCropType.stagnium)
   );
   public static final DeferredBlock<IRCropBlock> PLUMBISCUS_CROP = REGISTRY.register(
      "plumbiscus_crop", () -> IRCropBlock.create(IRBlockEntities.PLUMBISCUS_CROP, IRCropType.plumbiscus)
   );
   public static final DeferredBlock<IRCropBlock> AURELIA_CROP = REGISTRY.register(
      "aurelia_crop", () -> IRCropBlock.create(IRBlockEntities.AURELIA_CROP, IRCropType.aurelia)
   );
   public static final DeferredBlock<IRCropBlock> SHINING_CROP = REGISTRY.register(
      "shining_crop", () -> IRCropBlock.create(IRBlockEntities.SHINING_CROP, IRCropType.shining)
   );
   public static final DeferredBlock<IRCropBlock> RED_WHEAT_CROP = REGISTRY.register(
      "red_wheat_crop", () -> IRCropBlock.create(IRBlockEntities.RED_WHEAT_CROP, IRCropType.redWheat)
   );
   public static final DeferredBlock<IRCropBlock> COFFEE_CROP = REGISTRY.register(
      "coffee_crop", () -> IRCropBlock.create(IRBlockEntities.COFFEE_CROP, IRCropType.coffee)
   );
   public static final DeferredBlock<IRCropBlock> HOPS_CROP = REGISTRY.register(
      "hops_crop", () -> IRCropBlock.create(IRBlockEntities.HOPS_CROP, IRCropType.hops)
   );
   public static final DeferredBlock<IRCropBlock> EATING_PLANT_CROP = REGISTRY.register(
      "eating_plant_crop", () -> IRCropBlock.create(IRBlockEntities.EATING_PLANT_CROP, IRCropType.eatingPlant)
   );

   private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   private static Boolean canSpawnOnLeaves(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
      return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
   }

   private static Block createStoneOre() {
      return new Block(Properties.of().strength(3.0F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
   }

   private static Block createDeepslateOre() {
      return new Block(Properties.of().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops());
   }

   private static DeferredBlock<SolarPanelBlock> registerSolarPanel(int level) {
      return REGISTRY.register(String.format("solar_generator_%d_level", level), () -> new SolarPanelBlock(level));
   }
}
