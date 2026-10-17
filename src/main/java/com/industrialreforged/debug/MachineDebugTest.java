package com.industrialreforged.debug;

import com.industrialreforged.tech.blockentity.base.AbstractElectricGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.GeothermalGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelBlockEntity;
import com.industrialreforged.tech.blockentity.generator.electric.SolidFuelGeneratorBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.item.ItemCell;
import com.industrialreforged.transfer.block.WireBlockEntity;
import com.industrialreforged.transfer.init.GTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraft.server.level.ServerLevel;

/**
 * Headless verification of the energy chain, enabled only with
 * {@code -Dindustrialreforged.machinetest=1}.
 */
@EventBusSubscriber(modid = "industrialreforged")
public final class MachineDebugTest {
   // scenario A: generator -> wire -> wire -> macerator with ore
   private static final BlockPos GEN = new BlockPos(100, 200, 100);
   private static final BlockPos WIRE_A1 = new BlockPos(101, 200, 100);
   private static final BlockPos WIRE_A2 = new BlockPos(102, 200, 100);
   private static final BlockPos MACERATOR = new BlockPos(103, 200, 100);

   // scenario B: geothermal with lava -> wire -> macerator with ore
   private static final BlockPos GEO = new BlockPos(100, 210, 100);
   private static final BlockPos WIRE_B1 = new BlockPos(101, 210, 100);
   private static final BlockPos MACERATOR_B = new BlockPos(102, 210, 100);

   // scenario C: solar panel buffering
   private static final BlockPos SOLAR = new BlockPos(100, 220, 100);

   // scenario D: MFSU -> wire level 1 (200 FE/t) -> big machine
   private static final BlockPos SOURCE = new BlockPos(100, 230, 100);
   private static final BlockPos WIRE_D = new BlockPos(101, 230, 100);
   private static final BlockPos SINK = new BlockPos(102, 230, 100);

   // scenario G: a run of three wires (grid operations)
   private static final BlockPos GRID_A = new BlockPos(110, 250, 100);
   private static final BlockPos GRID_B = new BlockPos(111, 250, 100);
   private static final BlockPos GRID_C = new BlockPos(112, 250, 100);

   // scenario F: ore washing plant with a water bucket
   private static final BlockPos WASHER = new BlockPos(100, 250, 100);

   // scenario E: MFSU -> wire level 3 (8000 FE/t) -> big machine
   private static final BlockPos SOURCE_E = new BlockPos(100, 240, 100);
   private static final BlockPos WIRE_E = new BlockPos(101, 240, 100);
   private static final BlockPos SINK_E = new BlockPos(102, 240, 100);

   private static boolean enabled;
   private static ServerLevel level;
   private static int ticks;

   private MachineDebugTest() {
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      enabled = "1".equals(System.getProperty("industrialreforged.machinetest"));
      if (enabled) {
         level = event.getServer().overworld();
         System.out.println("[IR-TEST] server started, preparing test area");
      }
   }

   @SubscribeEvent
   public static void onTick(ServerTickEvent.Post event) {
      if (!enabled || level == null) {
         return;
      }

      if (ticks == 0) {
         setup();
      }

      ticks++;

      if (ticks == 20) {
         level.setDayTime(6000L);
      }

      if (ticks % 40 == 0 && ticks <= 240) {
         report();
      }

      if (ticks == 100) {
         if (level.getBlockEntity(GRID_A) instanceof WireBlockEntity grid) {
            System.out.println("[IR-TEST] grid size = " + grid.collectGrid().size() + " (expected 3)");
         }
      }

      if (ticks == 150) {
         if (level.getBlockEntity(GRID_A) instanceof WireBlockEntity grid) {
            int broken = grid.breakGrid();
            boolean gone = level.getBlockState(GRID_A).isAir() && level.getBlockState(GRID_B).isAir() && level.getBlockState(GRID_C).isAir();
            System.out.println("[IR-TEST] breakGrid broke " + broken + " wires, all gone = " + gone);
         }
      }

      if (ticks >= 3 && ticks <= 8) {
         System.out.println("[IR-TEST-rate] tick=" + ticks + " sinkD=" + energyOf(SINK) + " sinkE=" + energyOf(SINK_E)
            + " wireD=" + energyOf(WIRE_D) + " wireE=" + energyOf(WIRE_E));
      }

      if (ticks == 300) {
         level.setDayTime(18000L);
         System.out.println("[IR-TEST] --- night falls ---");
      }

      if (ticks == 360) {
         report();
         System.out.println("[IR-TEST] finished");
         event.getServer().halt(false);
      }
   }

   private static void setup() {
      for (int x = 96; x <= 112; x += 16) {
         for (int z = 96; z <= 112; z += 16) {
            level.setChunkForced(x >> 4, z >> 4, true);
         }
      }

      // A: generator -> wires -> macerator
      place(GEN, IRBlocks.GENERATOR.get());
      place(WIRE_A1, GTBlocks.WIRE_1_LEVEL.get());
      place(WIRE_A2, GTBlocks.WIRE_2_LEVEL.get());
      place(MACERATOR, IRBlocks.MACERATOR_1_LEVEL.get());
      if (level.getBlockEntity(GEN) instanceof SolidFuelGeneratorBlockEntity generator) {
         generator.input.setItem(0, new ItemStack(Items.COAL, 16));
      }
      fillMachineInput(MACERATOR, new ItemStack(Items.IRON_ORE, 4));

      // B: geothermal with lava -> wire -> macerator
      place(GEO, IRBlocks.GEO_GENERATOR.get());
      place(WIRE_B1, GTBlocks.WIRE_1_LEVEL.get());
      place(MACERATOR_B, IRBlocks.MACERATOR_1_LEVEL.get());
      if (level.getBlockEntity(GEO) instanceof GeothermalGeneratorBlockEntity geo) {
         geo.tank.fill(new FluidStack(Fluids.LAVA, 2000), FluidAction.EXECUTE);
      }
      fillMachineInput(MACERATOR_B, new ItemStack(Items.GOLD_ORE, 4));

      // C: solar panel
      place(SOLAR, IRBlocks.SOLAR_GENERATOR_1_LEVEL.get());

      // D: transfer rate test (wire tier 1 = 200 FE/t)
      place(SOURCE, IRBlocks.MFSU.get());
      place(WIRE_D, GTBlocks.WIRE_1_LEVEL.get());
      place(SINK, IRBlocks.MACERATOR_1_LEVEL.get());
      if (level.getBlockEntity(SOURCE) instanceof AbstractElectricMachineBlockEntity mfsu) {
         mfsu.energy.setStored(1000000);
      }
      if (level.getBlockEntity(SINK) instanceof AbstractElectricMachineBlockEntity sink) {
         sink.energy.setCapacity(1000000);
         sink.energy.setStored(0);
      }

      // G: wire grid
      place(GRID_A, GTBlocks.WIRE_1_LEVEL.get());
      place(GRID_B, GTBlocks.WIRE_1_LEVEL.get());
      place(GRID_C, GTBlocks.WIRE_1_LEVEL.get());

      // F: ore washing plant accepts a water bucket
      place(WASHER, IRBlocks.ORE_WASHING_PLANT.get());
      if (level.getBlockEntity(WASHER) instanceof com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity washer) {
         washer.fluidSlot.setItem(0, new ItemStack(Items.WATER_BUCKET));
      }

      // E: transfer rate test (wire tier 3 = 8000 FE/t)
      place(SOURCE_E, IRBlocks.MFSU.get());
      place(WIRE_E, GTBlocks.WIRE_3_LEVEL.get());
      place(SINK_E, IRBlocks.MACERATOR_1_LEVEL.get());
      if (level.getBlockEntity(SOURCE_E) instanceof AbstractElectricMachineBlockEntity mfsu) {
         mfsu.energy.setStored(1000000);
      }
      if (level.getBlockEntity(SINK_E) instanceof AbstractElectricMachineBlockEntity sink) {
         sink.energy.setCapacity(1000000);
         sink.energy.setStored(0);
      }

      System.out.println("[IR-TEST] setup done");
   }

   private static void fillMachineInput(BlockPos pos, ItemStack stack) {
      if (level.getBlockEntity(pos) instanceof com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity<?> machine) {
         machine.inputSlot.setItem(0, stack);
      }
   }

   private static void place(BlockPos pos, Block block) {
      level.getChunkAt(pos);
      BlockState state = block.defaultBlockState();
      level.setBlock(pos, state, 3);
   }

   private static void report() {
      if (level.getBlockEntity(WIRE_D) instanceof WireBlockEntity wd) {
         StringBuilder sb = new StringBuilder();
         wd.connections.forEach((dir, data) -> sb.append(dir).append('=').append(data.type).append(' '));
         System.out.println("[IR-TEST]   wireD[" + sb.toString().trim() + "]");
      }
      if (level.getBlockEntity(SOURCE) instanceof AbstractElectricMachineBlockEntity src) {
         long before = src.energy.getStored();
         com.industrialreforged.utils.EnergyUtils.transferEnergyAllNear(src, src.energy);
         System.out.println("[IR-TEST]   src extract=" + src.energy.getExtract() + " canExtract=" + src.energy.canExtract()
            + " manual=" + before + "->" + src.energy.getStored());
      }

      System.out.println("[IR-TEST] tick=" + ticks
         + " gen=" + energyOf(GEN)
         + " wireA1=" + energyOf(WIRE_A1)
         + " wireA2=" + energyOf(WIRE_A2)
         + " macerator=" + energyOf(MACERATOR)
         + " output=" + outputOf(MACERATOR)
         + " | geo=" + energyOf(GEO) + " fuel=" + fuelOf(GEO)
         + " wireB1=" + energyOf(WIRE_B1) + " maceratorB=" + energyOf(MACERATOR_B) + " outputB=" + outputOf(MACERATOR_B)
         + " | solar=" + energyOf(SOLAR) + " prod=" + productionOf(SOLAR) + " day=" + isDay()
         + " | src=" + energyOf(SOURCE) + " sink=" + energyOf(SINK) + " wireD=" + energyOf(WIRE_D)
         + " | srcE=" + energyOf(SOURCE_E) + " sinkE=" + energyOf(SINK_E) + " wireE=" + energyOf(WIRE_E)
         + " | washerTank=" + washerTank() + " washerSlot=" + washerSlot()
      );
   }

   private static long energyOf(BlockPos pos) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof WireBlockEntity wire) {
         return wire.getEnergyWrapper(null).getEnergyStored();
      }
      if (be instanceof AbstractElectricGeneratorBlockEntity generator) {
         return generator.energy.getStored();
      }
      if (be instanceof AbstractElectricMachineBlockEntity machine) {
         return machine.energy.getStored();
      }
      return -1L;
   }

   private static long productionOf(BlockPos pos) {
      BlockEntity be = level.getBlockEntity(pos);
      return be instanceof SolarPanelBlockEntity solar ? solar.production : -1L;
   }

   private static long fuelOf(BlockPos pos) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof AbstractElectricGeneratorBlockEntity generator) {
         return generator.fuel;
      }
      return -1L;
   }

   private static String outputOf(BlockPos pos) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity<?> machine) {
         ItemStack out = machine.outputSlot.getItem(0);
         return out.isEmpty() ? "-" : out.getItem() + "x" + out.getCount();
      }
      return "-";
   }

   private static long washerTank() {
      BlockEntity be = level.getBlockEntity(WASHER);
      return be instanceof com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity washer
         ? washer.fluidTank.getFluidAmount() : -1L;
   }

   private static String washerSlot() {
      BlockEntity be = level.getBlockEntity(WASHER);
      if (be instanceof com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity washer) {
         ItemStack in = washer.fluidSlot.getItem(0);
         ItemStack out = washer.cellSlot.getItem(0);
         return "in=" + (in.isEmpty() ? "-" : in.getItem()) + " out=" + (out.isEmpty() ? "-" : out.getItem());
      }
      return "-";
   }

   private static boolean isDay() {
      return level.isDay();
   }
}
