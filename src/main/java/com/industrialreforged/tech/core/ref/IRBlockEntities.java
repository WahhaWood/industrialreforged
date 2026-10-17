package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.blockentity.BrewBarrelBlockEntity;
import com.industrialreforged.tech.blockentity.WallBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.GeothermalGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.KineticGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.RTGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.SemifluidGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelConnectorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.SolidFuelGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.StirlingGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.heat.ElectricHeatGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.heat.FluidHeatGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.heat.RTHeatGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.heat.SolidHeatGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.kinetic.ElectricKineticGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.kinetic.ManualKineticGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.kinetic.SteamKineticGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.kinetic.StirlingKineticGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.kinetic.WaterKineticGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.kinetic.WindKineticGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.heat.HeatPipeBlockEntity;
import com.industrialreforged.tech.blockentity.machine.BlastFurnaceBlockEntity;
import com.industrialreforged.tech.blockentity.machine.BlockCutterBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CannerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CentrifugeBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CondenserBlockEntity;
import com.industrialreforged.tech.blockentity.machine.OreBreakerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.QuantumQuarryBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CropHarvesterBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CropmatronBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.FermenterBlockEntity;
import com.industrialreforged.tech.blockentity.machine.FluidBottlerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.InductionFurnaceBlockEntity;
import com.industrialreforged.tech.blockentity.machine.IronFurnaceBlockEntity;
import com.industrialreforged.tech.blockentity.machine.LatheBlockEntity;
import com.industrialreforged.tech.blockentity.machine.LiquidHeatExchangerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MatterGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer4LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MolecularFarmingBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MolecularTransformerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity;
import com.industrialreforged.tech.blockentity.machine.PatternStorageBlockEntity;
import com.industrialreforged.tech.blockentity.machine.PumpBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ReplicatorBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ScannerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.SolarDestillerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.SolidCannerBlockEntity;
import com.industrialreforged.tech.blockentity.machine.TeleporterBlockEntity;
import com.industrialreforged.tech.blockentity.reactor.NuclearReactorElectricBlockEntity;
import com.industrialreforged.tech.blockentity.reactor.ReactorChamberElectricBlockEntity;
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
import com.industrialreforged.tech.blockentity.storage.LuminatorBlockEntity;
import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.multiblock.tank.TankControllerBlockEntity;
import com.industrialreforged.tech.multiblock.tank.TankValveBlockEntity;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public final class IRBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "industrialreforged");
   public static final Supplier<BlockEntityType<BrewBarrelBlockEntity>> BREW_BARREL = register("brew_barrel", BrewBarrelBlockEntity::new, IRBlocks.BREW_BARREL);
   public static final Supplier<BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE = register(
      "iron_furnace", IronFurnaceBlockEntity::new, IRBlocks.IRON_FURNACE
   );
   public static final Supplier<BlockEntityType<TankValveBlockEntity>> TANK_VALVE = register(
      "tank_valve", TankValveBlockEntity::new, IRBlocks.TANK_VALVE
   );
   public static final Supplier<BlockEntityType<TankControllerBlockEntity>> TANK_CONTROLLER = register(
      "tank_controller", TankControllerBlockEntity::new, IRBlocks.TANK_CONTROLLER
   );
   public static final Supplier<BlockEntityType<WallBlockEntity>> WALL = register("wall", WallBlockEntity::new, IRBlocks.OBSCURED_WALL);
   public static final Supplier<BlockEntityType<SolidFuelGeneratorBlockEntity>> GENERATOR = register(
      "generator", SolidFuelGeneratorBlockEntity::new, IRBlocks.GENERATOR
   );
   public static final Supplier<BlockEntityType<StirlingGeneratorBlockEntity>> STIRLING_GENERATOR = register(
      "stirling_generator", StirlingGeneratorBlockEntity::new, IRBlocks.STIRLING_GENERATOR
   );
   public static final Supplier<BlockEntityType<GeothermalGeneratorBlockEntity>> GEO_GENERATOR = register(
      "geo_generator", GeothermalGeneratorBlockEntity::new, IRBlocks.GEO_GENERATOR
   );
   public static final Supplier<BlockEntityType<SemifluidGeneratorBlockEntity>> SEMIFLUID_GENERATOR = register(
      "semifluid_generator", SemifluidGeneratorBlockEntity::new, IRBlocks.SEMIFLUID_GENERATOR
   );
   public static final Supplier<BlockEntityType<KineticGeneratorBlockEntity>> KINETIC_GENERATOR = register(
      "kinetic_generator", KineticGeneratorBlockEntity::new, IRBlocks.KINETIC_GENERATOR
   );
   public static final Supplier<BlockEntityType<RTGeneratorBlockEntity>> RT_GENERATOR = register(
      "rt_generator", RTGeneratorBlockEntity::new, IRBlocks.RT_GENERATOR
   );
   public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_GENERATOR = register(
      "solar_generator",
      SolarPanelBlockEntity::new,
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
      IRBlocks.SOLAR_GENERATOR_15_LEVEL
   );
   public static final Supplier<BlockEntityType<SolarPanelConnectorBlockEntity>> SOLAR_CONNECTOR = register(
      "solar_connector", SolarPanelConnectorBlockEntity::new, IRBlocks.SOLAR_CONNECTOR
   );
   public static final Supplier<BlockEntityType<NuclearReactorElectricBlockEntity>> NUCLEAR_REACTOR = register(
      "nuclear_reactor", NuclearReactorElectricBlockEntity::new, IRBlocks.NUCLEAR_REACTOR
   );
   public static final Supplier<BlockEntityType<ReactorChamberElectricBlockEntity>> REACTOR_CHAMBER = register(
      "reactor_chamber", ReactorChamberElectricBlockEntity::new, IRBlocks.REACTOR_CHAMBER
   );
   public static final Supplier<BlockEntityType<ElectricKineticGeneratorBlockEntity>> ELECTRIC_KINETIC_GENERATOR = register(
      "electric_kinetic_generator", ElectricKineticGeneratorBlockEntity::new, IRBlocks.ELECTRIC_KINETIC_GENERATOR
   );
   public static final Supplier<BlockEntityType<ManualKineticGeneratorBlockEntity>> MANUAL_KINETIC_GENERATOR = register(
      "manual_kinetic_generator", ManualKineticGeneratorBlockEntity::new, IRBlocks.MANUAL_KINETIC_GENERATOR
   );
   public static final Supplier<BlockEntityType<SteamKineticGeneratorBlockEntity>> STEAM_KINETIC_GENERATOR = register(
      "steam_kinetic_generator", SteamKineticGeneratorBlockEntity::new, IRBlocks.STEAM_KINETIC_GENERATOR
   );
   public static final Supplier<BlockEntityType<StirlingKineticGeneratorBlockEntity>> STIRLING_KINETIC_GENERATOR = register(
      "stirling_kinetic_generator", StirlingKineticGeneratorBlockEntity::new, IRBlocks.STIRLING_KINETIC_GENERATOR
   );
   public static final Supplier<BlockEntityType<WaterKineticGeneratorBlockEntity>> WATER_KINETIC_GENERATOR = register(
      "water_kinetic_generator", WaterKineticGeneratorBlockEntity::new, IRBlocks.WATER_KINETIC_GENERATOR
   );
   public static final Supplier<BlockEntityType<WindKineticGeneratorBlockEntity>> WIND_KINETIC_GENERATOR = register(
      "wind_kinetic_generator", WindKineticGeneratorBlockEntity::new, IRBlocks.WIND_KINETIC_GENERATOR
   );
   public static final Supplier<BlockEntityType<ElectricHeatGeneratorBlockEntity>> ELECTRIC_HEAT_GENERATOR = register(
      "electric_heat_generator", ElectricHeatGeneratorBlockEntity::new, IRBlocks.ELECTRIC_HEAT_GENERATOR
   );
   public static final Supplier<BlockEntityType<FluidHeatGeneratorBlockEntity>> FLUID_HEAT_GENERATOR = register(
      "fluid_heat_generator", FluidHeatGeneratorBlockEntity::new, IRBlocks.FLUID_HEAT_GENERATOR
   );
   public static final Supplier<BlockEntityType<RTHeatGeneratorBlockEntity>> RT_HEAT_GENERATOR = register(
      "rt_heat_generator", RTHeatGeneratorBlockEntity::new, IRBlocks.RT_HEAT_GENERATOR
   );
   public static final Supplier<BlockEntityType<SolidHeatGeneratorBlockEntity>> SOLID_HEAT_GENERATOR = register(
      "solid_heat_generator", SolidHeatGeneratorBlockEntity::new, IRBlocks.SOLID_HEAT_GENERATOR
   );
   public static final Supplier<BlockEntityType<ElectricFurnace1LevelBlockEntity>> ELECTRIC_FURNACE_1_LEVEL = register(
      "electric_furnace_1_level", ElectricFurnace1LevelBlockEntity::new, IRBlocks.ELECTRIC_FURNACE_1_LEVEL
   );
   public static final Supplier<BlockEntityType<ElectricFurnace2LevelBlockEntity>> ELECTRIC_FURNACE_2_LEVEL = register(
      "electric_furnace_2_level", ElectricFurnace2LevelBlockEntity::new, IRBlocks.ELECTRIC_FURNACE_2_LEVEL
   );
   public static final Supplier<BlockEntityType<ElectricFurnace3LevelBlockEntity>> ELECTRIC_FURNACE_3_LEVEL = register(
      "electric_furnace_3_level", ElectricFurnace3LevelBlockEntity::new, IRBlocks.ELECTRIC_FURNACE_3_LEVEL
   );
   public static final Supplier<BlockEntityType<ElectricFurnace4LevelBlockEntity>> ELECTRIC_FURNACE_4_LEVEL = register(
      "electric_furnace_4_level", ElectricFurnace4LevelBlockEntity::new, IRBlocks.ELECTRIC_FURNACE_4_LEVEL
   );
   public static final Supplier<BlockEntityType<Macerator1LevelBlockEntity>> MACERATOR_1_LEVEL = register(
      "macerator_1_level", Macerator1LevelBlockEntity::new, IRBlocks.MACERATOR_1_LEVEL
   );
   public static final Supplier<BlockEntityType<Macerator2LevelBlockEntity>> MACERATOR_2_LEVEL = register(
      "macerator_2_level", Macerator2LevelBlockEntity::new, IRBlocks.MACERATOR_2_LEVEL
   );
   public static final Supplier<BlockEntityType<Macerator3LevelBlockEntity>> MACERATOR_3_LEVEL = register(
      "macerator_3_level", Macerator3LevelBlockEntity::new, IRBlocks.MACERATOR_3_LEVEL
   );
   public static final Supplier<BlockEntityType<Macerator4LevelBlockEntity>> MACERATOR_4_LEVEL = register(
      "macerator_4_level", Macerator4LevelBlockEntity::new, IRBlocks.MACERATOR_4_LEVEL
   );
   public static final Supplier<BlockEntityType<Recycler1LevelBlockEntity>> RECYCLER_1_LEVEL = register(
      "recycler_1_level", Recycler1LevelBlockEntity::new, IRBlocks.RECYCLER_1_LEVEL
   );
   public static final Supplier<BlockEntityType<Recycler2LevelBlockEntity>> RECYCLER_2_LEVEL = register(
      "recycler_2_level", Recycler2LevelBlockEntity::new, IRBlocks.RECYCLER_2_LEVEL
   );
   public static final Supplier<BlockEntityType<Recycler3LevelBlockEntity>> RECYCLER_3_LEVEL = register(
      "recycler_3_level", Recycler3LevelBlockEntity::new, IRBlocks.RECYCLER_3_LEVEL
   );
   public static final Supplier<BlockEntityType<Recycler4LevelBlockEntity>> RECYCLER_4_LEVEL = register(
      "recycler_4_level", Recycler4LevelBlockEntity::new, IRBlocks.RECYCLER_4_LEVEL
   );
   public static final Supplier<BlockEntityType<Extractor1LevelBlockEntity>> EXTRACTOR_1_LEVEL = register(
      "extractor_1_level", Extractor1LevelBlockEntity::new, IRBlocks.EXTRACTOR_1_LEVEL
   );
   public static final Supplier<BlockEntityType<Extractor2LevelBlockEntity>> EXTRACTOR_2_LEVEL = register(
      "extractor_2_level", Extractor2LevelBlockEntity::new, IRBlocks.EXTRACTOR_2_LEVEL
   );
   public static final Supplier<BlockEntityType<Extractor3LevelBlockEntity>> EXTRACTOR_3_LEVEL = register(
      "extractor_3_level", Extractor3LevelBlockEntity::new, IRBlocks.EXTRACTOR_3_LEVEL
   );
   public static final Supplier<BlockEntityType<Extractor4LevelBlockEntity>> EXTRACTOR_4_LEVEL = register(
      "extractor_4_level", Extractor4LevelBlockEntity::new, IRBlocks.EXTRACTOR_4_LEVEL
   );
   public static final Supplier<BlockEntityType<Compressor1LevelBlockEntity>> COMPRESSOR_1_LEVEL = register(
      "compressor_1_level", Compressor1LevelBlockEntity::new, IRBlocks.COMPRESSOR_1_LEVEL
   );
   public static final Supplier<BlockEntityType<Compressor2LevelBlockEntity>> COMPRESSOR_2_LEVEL = register(
      "compressor_2_level", Compressor2LevelBlockEntity::new, IRBlocks.COMPRESSOR_2_LEVEL
   );
   public static final Supplier<BlockEntityType<Compressor3LevelBlockEntity>> COMPRESSOR_3_LEVEL = register(
      "compressor_3_level", Compressor3LevelBlockEntity::new, IRBlocks.COMPRESSOR_3_LEVEL
   );
   public static final Supplier<BlockEntityType<Compressor4LevelBlockEntity>> COMPRESSOR_4_LEVEL = register(
      "compressor_4_level", Compressor4LevelBlockEntity::new, IRBlocks.COMPRESSOR_4_LEVEL
   );
   public static final Supplier<BlockEntityType<MetalFormer1LevelBlockEntity>> METAL_FORMER_1_LEVEL = register(
      "metal_former_1_level", MetalFormer1LevelBlockEntity::new, IRBlocks.METAL_FORMER_1_LEVEL
   );
   public static final Supplier<BlockEntityType<MetalFormer2LevelBlockEntity>> METAL_FORMER_2_LEVEL = register(
      "metal_former_2_level", MetalFormer2LevelBlockEntity::new, IRBlocks.METAL_FORMER_2_LEVEL
   );
   public static final Supplier<BlockEntityType<MetalFormer3LevelBlockEntity>> METAL_FORMER_3_LEVEL = register(
      "metal_former_3_level", MetalFormer3LevelBlockEntity::new, IRBlocks.METAL_FORMER_3_LEVEL
   );
   public static final Supplier<BlockEntityType<MetalFormer4LevelBlockEntity>> METAL_FORMER_4_LEVEL = register(
      "metal_former_4_level", MetalFormer4LevelBlockEntity::new, IRBlocks.METAL_FORMER_4_LEVEL
   );
   public static final Supplier<BlockEntityType<OreWashingPlantBlockEntity>> ORE_WASHING_PLANT = register(
      "ore_washing_plant", OreWashingPlantBlockEntity::new, IRBlocks.ORE_WASHING_PLANT
   );
   public static final Supplier<BlockEntityType<CannerBlockEntity>> CANNER = register("canner", CannerBlockEntity::new, IRBlocks.CANNER);
   public static final Supplier<BlockEntityType<SolidCannerBlockEntity>> SOLID_CANNER = register(
      "solid_canner", SolidCannerBlockEntity::new, IRBlocks.SOLID_CANNER
   );
   public static final Supplier<BlockEntityType<LatheBlockEntity>> TURNING = register("turning", LatheBlockEntity::new, IRBlocks.TURNING);
   public static final Supplier<BlockEntityType<InductionFurnaceBlockEntity>> INDUCTION_FURNACE = register(
      "induction_furnace", InductionFurnaceBlockEntity::new, IRBlocks.INDUCTION_FURNACE
   );
   public static final Supplier<BlockEntityType<CentrifugeBlockEntity>> CENTRIFUGE = register("centrifuge", CentrifugeBlockEntity::new, IRBlocks.CENTRIFUGE);
   public static final Supplier<BlockEntityType<BlastFurnaceBlockEntity>> BLAST_FURNACE = register(
      "blast_furnace", BlastFurnaceBlockEntity::new, IRBlocks.BLAST_FURNACE
   );
   public static final Supplier<BlockEntityType<BlockCutterBlockEntity>> BLOCK_CUTTER = register(
      "block_cutter", BlockCutterBlockEntity::new, IRBlocks.BLOCK_CUTTER
   );
   public static final Supplier<BlockEntityType<TeleporterBlockEntity>> TELEPORTER = register("teleporter", TeleporterBlockEntity::new, IRBlocks.TELEPORTER);
   public static final Supplier<BlockEntityType<MatterGeneratorBlockEntity>> MATTER_GENERATOR = register(
      "matter_generator", MatterGeneratorBlockEntity::new, IRBlocks.MATTER_GENERATOR
   );
   public static final Supplier<BlockEntityType<ReplicatorBlockEntity>> REPLICATOR = register("replicator", ReplicatorBlockEntity::new, IRBlocks.REPLICATOR);
   public static final Supplier<BlockEntityType<ScannerBlockEntity>> UU_SCANNER = register("uu_scanner", ScannerBlockEntity::new, IRBlocks.UU_SCANNER);
   public static final Supplier<BlockEntityType<PatternStorageBlockEntity>> PATTERN_STORAGE = register(
      "pattern_storage", PatternStorageBlockEntity::new, IRBlocks.PATTERN_STORAGE
   );
   public static final Supplier<BlockEntityType<MolecularFarmingBlockEntity>> MOLECULAR_FARMING = register(
      "molecular_farming", MolecularFarmingBlockEntity::new, IRBlocks.MOLECULAR_FARMING
   );
   public static final Supplier<BlockEntityType<MolecularTransformerBlockEntity>> MOLECULAR_TRANSFORMER = register(
      "molecular_transformer", MolecularTransformerBlockEntity::new, IRBlocks.MOLECULAR_TRANSFORMER
   );
   public static final Supplier<BlockEntityType<PumpBlockEntity>> PUMP = register("pump", PumpBlockEntity::new, IRBlocks.PUMP);
   public static final Supplier<BlockEntityType<FluidBottlerBlockEntity>> FLUID_BOTTLER = register(
      "fluid_bottler", FluidBottlerBlockEntity::new, IRBlocks.FLUID_BOTTLER
   );
   public static final Supplier<BlockEntityType<SolarDestillerBlockEntity>> SOLAR_DISTILLER = register(
      "solar_distiller", SolarDestillerBlockEntity::new, IRBlocks.SOLAR_DISTILLER
   );
   public static final Supplier<BlockEntityType<CondenserBlockEntity>> CONDENSER = register("condenser", CondenserBlockEntity::new, IRBlocks.CONDENSER);
   public static final Supplier<BlockEntityType<HeatPipeBlockEntity>> HEAT_PIPE = register(
      "heat_pipe", HeatPipeBlockEntity::new, IRBlocks.HEAT_PIPE_1, IRBlocks.HEAT_PIPE_2, IRBlocks.HEAT_PIPE_3
   );
   public static final Supplier<BlockEntityType<QuantumQuarryBlockEntity>> QUANTUM_QUARRY = register(
      "quantum_quarry", QuantumQuarryBlockEntity::new, IRBlocks.QUANTUM_QUARRY
   );
   public static final Supplier<BlockEntityType<OreBreakerBlockEntity>> ORE_BREAKER = register(
      "ore_breaker", OreBreakerBlockEntity::new, IRBlocks.ORE_BREAKER
   );
   public static final Supplier<BlockEntityType<LiquidHeatExchangerBlockEntity>> LIQUID_HEAT_EXCHANGER = register(
      "liquid_heat_exchanger", LiquidHeatExchangerBlockEntity::new, IRBlocks.LIQUID_HEAT_EXCHANGER
   );
   public static final Supplier<BlockEntityType<CropmatronBlockEntity>> CROPMATRON = register("cropmatron", CropmatronBlockEntity::new, IRBlocks.CROPMATRON);
   public static final Supplier<BlockEntityType<CropHarvesterBlockEntity>> CROP_HARVESTER = register(
      "crop_harvester", CropHarvesterBlockEntity::new, IRBlocks.CROP_HARVESTER
   );
   public static final Supplier<BlockEntityType<FermenterBlockEntity>> FERMENTER = register("fermenter", FermenterBlockEntity::new, IRBlocks.FERMENTER);
   public static final Supplier<BlockEntityType<ElectricBatBoxBlockEntity>> BATBOX = register("batbox", ElectricBatBoxBlockEntity::new, IRBlocks.BATBOX);
   public static final Supplier<BlockEntityType<ElectricCESUBlockEntity>> CESU = register("cesu", ElectricCESUBlockEntity::new, IRBlocks.CESU);
   public static final Supplier<BlockEntityType<ElectricMFEBlockEntity>> MFE = register("mfe", ElectricMFEBlockEntity::new, IRBlocks.MFE);
   public static final Supplier<BlockEntityType<ElectricMFSUBlockEntity>> MFSU = register("mfsu", ElectricMFSUBlockEntity::new, IRBlocks.MFSU);
   public static final Supplier<BlockEntityType<ElectricEFSUBlockEntity>> EFSU = register("efsu", ElectricEFSUBlockEntity::new, IRBlocks.EFSU);
   public static final Supplier<BlockEntityType<ChargepadBatBoxBlockEntity>> BATBOX_CHARGEPAD = register(
      "batbox_chargepad", ChargepadBatBoxBlockEntity::new, IRBlocks.BATBOX_CHARGEPAD
   );
   public static final Supplier<BlockEntityType<ChargepadCESUBlockEntity>> CESU_CHARGEPAD = register(
      "cesu_chargepad", ChargepadCESUBlockEntity::new, IRBlocks.CESU_CHARGEPAD
   );
   public static final Supplier<BlockEntityType<ChargepadMFEBlockEntity>> MFE_CHARGEPAD = register(
      "mfe_chargepad", ChargepadMFEBlockEntity::new, IRBlocks.MFE_CHARGEPAD
   );
   public static final Supplier<BlockEntityType<ChargepadMFSUBlockEntity>> MFSU_CHARGEPAD = register(
      "mfsu_chargepad", ChargepadMFSUBlockEntity::new, IRBlocks.MFSU_CHARGEPAD
   );
   public static final Supplier<BlockEntityType<ChargepadEFSUBlockEntity>> EFSU_CHARGEPAD = register(
      "efsu_chargepad", ChargepadEFSUBlockEntity::new, IRBlocks.EFSU_CHARGEPAD
   );
   public static final Supplier<BlockEntityType<LuminatorBlockEntity>> LUMINATOR = register("luminator", LuminatorBlockEntity::new, IRBlocks.LUMINATOR);
   public static final Supplier<BlockEntityType<CropBlockEntity>> CROP_STICK = registerCrop("crop_stick", IRBlocks.CROP_STICK);
   public static final Supplier<BlockEntityType<CropBlockEntity>> WEED_CROP = registerCrop("weed_crop", IRBlocks.WEED_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> WHEAT_CROP = registerCrop("wheat_crop", IRBlocks.WHEAT_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> CARROTS_CROP = registerCrop("carrots_crop", IRBlocks.CARROTS_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> POTATO_CROP = registerCrop("potato_crop", IRBlocks.POTATO_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> BEETROOTS_CROP = registerCrop("beetroots_crop", IRBlocks.BEETROOTS_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> PUMPKIN_CROP = registerCrop("pumpkin_crop", IRBlocks.PUMPKIN_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> MELON_CROP = registerCrop("melon_crop", IRBlocks.MELON_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> DANDELION_CROP = registerCrop("dandelion_crop", IRBlocks.DANDELION_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> POPPY_CROP = registerCrop("poppy_crop", IRBlocks.POPPY_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> BLACKTHORN_CROP = registerCrop("blackthorn_crop", IRBlocks.BLACKTHORN_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> TULIP_CROP = registerCrop("tulip_crop", IRBlocks.TULIP_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> CYAZINT_CROP = registerCrop("cyazint_crop", IRBlocks.CYAZINT_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> VENOMILIA_CROP = registerCrop("venomilia_crop", IRBlocks.VENOMILIA_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> REED_CROP = registerCrop("reed_crop", IRBlocks.REED_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> STICKY_REED_CROP = registerCrop("sticky_reed_crop", IRBlocks.STICKY_REED_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> COCOA_CROP = registerCrop("cocoa_crop", IRBlocks.COCOA_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> FLAX_CROP = registerCrop("flax_crop", IRBlocks.FLAX_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> RED_MUSHROOM_CROP = registerCrop("red_mushroom_crop", IRBlocks.RED_MUSHROOM_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> BROWN_MUSHROOM_CROP = registerCrop("brown_mushroom_crop", IRBlocks.BROWN_MUSHROOM_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> NETHER_WART_CROP = registerCrop("nether_wart_crop", IRBlocks.NETHER_WART_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> TERRA_WART_CROP = registerCrop("terra_wart_crop", IRBlocks.TERRA_WART_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> OAK_SAPLING_CROP = registerCrop("oak_sapling_crop", IRBlocks.OAK_SAPLING_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> SPRUCE_SAPLING_CROP = registerCrop("spruce_sapling_crop", IRBlocks.SPRUCE_SAPLING_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> BIRCH_SAPLING_CROP = registerCrop("birch_sapling_crop", IRBlocks.BIRCH_SAPLING_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> JUNGLE_SAPLING_CROP = registerCrop("jungle_sapling_crop", IRBlocks.JUNGLE_SAPLING_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> ACACIA_SAPLING_CROP = registerCrop("acacia_sapling_crop", IRBlocks.ACACIA_SAPLING_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> DARK_OAK_SAPLING_CROP = registerCrop("dark_oak_sapling_crop", IRBlocks.DARK_OAK_SAPLING_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> FERRU_CROP = registerCrop("ferru_crop", IRBlocks.FERRU_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> CYPRIUM_CROP = registerCrop("cyprium_crop", IRBlocks.CYPRIUM_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> STAGNIUM_CROP = registerCrop("stagnium_crop", IRBlocks.STAGNIUM_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> PLUMBISCUS_CROP = registerCrop("plumbiscus_crop", IRBlocks.PLUMBISCUS_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> AURELIA_CROP = registerCrop("aurelia_crop", IRBlocks.AURELIA_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> SHINING_CROP = registerCrop("shining_crop", IRBlocks.SHINING_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> RED_WHEAT_CROP = registerCrop("red_wheat_crop", IRBlocks.RED_WHEAT_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> COFFEE_CROP = registerCrop("coffee_crop", IRBlocks.COFFEE_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> HOPS_CROP = registerCrop("hops_crop", IRBlocks.HOPS_CROP);
   public static final Supplier<BlockEntityType<CropBlockEntity>> EATING_PLANT_CROP = registerCrop("eating_plant_crop", IRBlocks.EATING_PLANT_CROP);

   @SafeVarargs
   @NotNull
   private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(
      String string, BlockEntitySupplier<T> biFunction, DeferredBlock<? extends Block>... arrblock
   ) {
      return REGISTRY.register(string, () -> Builder.of(biFunction, Arrays.stream(arrblock).map(DeferredHolder::get).toArray(Block[]::new)).build(null));
   }

   @NotNull
   private static Supplier<BlockEntityType<CropBlockEntity>> registerCrop(String string, DeferredBlock<? extends Block> block) {
      IRBlockEntities.CropEntitySupplier supplier = new IRBlockEntities.CropEntitySupplier();
      Supplier<BlockEntityType<CropBlockEntity>> typeSupplier = REGISTRY.register(
         string, () -> Builder.of(supplier, new Block[]{(Block)block.get()}).build(null)
      );
      supplier.setSupplier(typeSupplier);
      return typeSupplier;
   }

   private static class CropEntitySupplier implements BlockEntitySupplier<CropBlockEntity> {
      private Supplier<BlockEntityType<CropBlockEntity>> supplier;

      public void setSupplier(Supplier<BlockEntityType<CropBlockEntity>> supplier) {
         this.supplier = supplier;
      }

      @NotNull
      public CropBlockEntity create(@NotNull BlockPos pos, @NotNull BlockState state) {
         return new CropBlockEntity(this.supplier, pos, state);
      }
   }
}
