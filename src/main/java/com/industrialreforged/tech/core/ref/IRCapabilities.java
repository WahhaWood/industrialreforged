package com.industrialreforged.tech.core.ref;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.utils.CapabilitiesUtils;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = "industrialreforged")
public class IRCapabilities {
   @SubscribeEvent
   public static void registryCapabilities(RegisterCapabilitiesEvent event) {
      registerEnergyAndContainer(event, IRBlockEntities.QUANTUM_QUARRY);
      registerEnergyAndContainer(event, IRBlockEntities.ORE_BREAKER);
      registerEnergyAndContainer(event, IRBlockEntities.GENERATOR);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.STIRLING_GENERATOR);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.GEO_GENERATOR);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.SEMIFLUID_GENERATOR);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.KINETIC_GENERATOR);
      registerEnergyAndContainer(event, IRBlockEntities.RT_GENERATOR);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.SOLAR_GENERATOR);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.SOLAR_CONNECTOR);
      registerEnergyAndContainer(event, IRBlockEntities.NUCLEAR_REACTOR);
      registerEnergyAndContainer(event, IRBlockEntities.REACTOR_CHAMBER);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.ELECTRIC_KINETIC_GENERATOR);
      CapabilitiesUtils.registerFluidHandlerBlock(event, IRBlockEntities.STIRLING_KINETIC_GENERATOR);
      CapabilitiesUtils.registerFluidHandlerBlock(event, IRBlockEntities.STEAM_KINETIC_GENERATOR);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.ELECTRIC_HEAT_GENERATOR);
      CapabilitiesUtils.registerFluidHandlerBlock(event, IRBlockEntities.FLUID_HEAT_GENERATOR);
      CapabilitiesUtils.registerContainerBlock(event, IRBlockEntities.IRON_FURNACE);
      registerEnergyAndContainer(event, IRBlockEntities.ELECTRIC_FURNACE_1_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.ELECTRIC_FURNACE_2_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.ELECTRIC_FURNACE_3_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.ELECTRIC_FURNACE_4_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.MACERATOR_1_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.MACERATOR_2_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.MACERATOR_3_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.MACERATOR_4_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.RECYCLER_1_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.RECYCLER_2_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.RECYCLER_3_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.RECYCLER_4_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.EXTRACTOR_1_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.EXTRACTOR_2_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.EXTRACTOR_3_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.EXTRACTOR_4_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.COMPRESSOR_1_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.COMPRESSOR_2_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.COMPRESSOR_3_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.COMPRESSOR_4_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.METAL_FORMER_1_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.METAL_FORMER_2_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.METAL_FORMER_3_LEVEL);
      registerEnergyAndContainer(event, IRBlockEntities.METAL_FORMER_4_LEVEL);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.ORE_WASHING_PLANT);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.CANNER);
      registerEnergyAndContainer(event, IRBlockEntities.SOLID_CANNER);
      registerEnergyAndContainer(event, IRBlockEntities.INDUCTION_FURNACE);
      registerEnergyAndContainer(event, IRBlockEntities.CENTRIFUGE);
      CapabilitiesUtils.registerContainerBlock(event, IRBlockEntities.BLAST_FURNACE);
      registerEnergyAndContainer(event, IRBlockEntities.BLOCK_CUTTER);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.MATTER_GENERATOR);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.REPLICATOR);
      registerEnergyAndContainer(event, IRBlockEntities.UU_SCANNER);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.MOLECULAR_FARMING);
      registerEnergyAndContainer(event, IRBlockEntities.MOLECULAR_TRANSFORMER);
      registerEnergyAndFluid(event, IRBlockEntities.PUMP);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.FLUID_BOTTLER);
      CapabilitiesUtils.registerFluidHandlerBlock(event, IRBlockEntities.SOLAR_DISTILLER);
      registerEnergyAndFluid(event, IRBlockEntities.CONDENSER);
      CapabilitiesUtils.registerFluidHandlerBlock(event, IRBlockEntities.LIQUID_HEAT_EXCHANGER);
      registerEnergyAndContainerAndFluid(event, IRBlockEntities.CROPMATRON);
      registerEnergyAndContainer(event, IRBlockEntities.CROP_HARVESTER);
      CapabilitiesUtils.registerContainerBlock(event, IRBlockEntities.FERMENTER);
      CapabilitiesUtils.registerFluidHandlerBlock(event, IRBlockEntities.FERMENTER);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.BATBOX);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.CESU);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.MFE);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.MFSU);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.EFSU);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.BATBOX_CHARGEPAD);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.CESU_CHARGEPAD);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.MFE_CHARGEPAD);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.MFSU_CHARGEPAD);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.EFSU_CHARGEPAD);
      CapabilitiesUtils.registerEnergyStorageBlock(event, IRBlockEntities.LUMINATOR);
      CapabilitiesUtils.registerFluidHandlerBlock(event, IRBlockEntities.TANK_VALVE);
      CapabilitiesUtils.registerEnergyContainerItem(event, IRItems.REGISTRY);
      CapabilitiesUtils.registerFluidContainerItem(event, IRItems.REGISTRY);
   }

   private static <T extends BlockEntity & IEnergyWrapper & IContainerWrapper> void registerEnergyAndContainer(
      RegisterCapabilitiesEvent event, Supplier<BlockEntityType<T>> t
   ) {
      CapabilitiesUtils.registerContainerBlock(event, t);
      CapabilitiesUtils.registerEnergyStorageBlock(event, t);
   }

   private static <T extends BlockEntity & IEnergyWrapper & IFluidWrapper> void registerEnergyAndFluid(
      RegisterCapabilitiesEvent event, Supplier<BlockEntityType<T>> t
   ) {
      CapabilitiesUtils.registerEnergyStorageBlock(event, t);
      CapabilitiesUtils.registerFluidHandlerBlock(event, t);
   }

   private static <T extends BlockEntity & IEnergyWrapper & IContainerWrapper & IFluidWrapper> void registerEnergyAndContainerAndFluid(
      RegisterCapabilitiesEvent event, Supplier<BlockEntityType<T>> t
   ) {
      CapabilitiesUtils.registerContainerBlock(event, t);
      CapabilitiesUtils.registerEnergyStorageBlock(event, t);
      CapabilitiesUtils.registerFluidHandlerBlock(event, t);
   }
}
