package com.industrialreforged.utils;

import com.industrialreforged.energy.BigEnergyStorage;
import com.google.common.primitives.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyUtils {
   public static void transferEnergyAllNear(BlockEntity entity, IEnergyStorage basic) {
      transferEnergyAllNear(entity.getLevel(), entity.getBlockPos(), basic);
   }

   public static void transferEnergyAllNear(Level level, BlockPos blockPos, IEnergyStorage basic) {
      if (basic instanceof BigEnergyStorage big) {
         long extracting = Math.min(big.getStored(), big.getExtract());
         if (extracting > 0L) {
            big.extractForceEnergy(transferBigEnergyAllNear(level, blockPos, extracting, false));
         }
      } else if (basic.canExtract()) {
         int storage = basic.getEnergyStored();
         if (storage > 0) {
            basic.extractEnergy(transferEnergyAllNear(level, blockPos, basic.extractEnergy(storage, true), false), false);
         }
      }
   }

   public static int transferEnergyAllNear(BlockEntity entity, int amount, boolean simulation) {
      return transferEnergyAllNear(entity.getLevel(), entity.getBlockPos(), amount, simulation);
   }

   public static int transferEnergyAllNear(Level level, BlockPos blockPos, int amount, boolean simulation) {
      return Ints.saturatedCast(transferBigEnergyAllNear(level, blockPos, amount, simulation));
   }

   public static long transferBigEnergyAllNear(BlockEntity entity, long amount, boolean simulation) {
      return transferBigEnergyAllNear(entity.getLevel(), entity.getBlockPos(), amount, simulation);
   }

   public static long transferBigEnergyAllNear(Level level, BlockPos blockPos, long amount, boolean simulation) {
      long startAmount = amount;

      for (Direction direction : Direction.values()) {
         amount -= transferBigEnergyToSide(level, blockPos, direction, amount, simulation);
      }

      return startAmount - amount;
   }

   public static void transferEnergyToSide(BlockEntity entity, Direction direction, IEnergyStorage basic) {
      transferEnergyToSide(entity.getLevel(), entity.getBlockPos(), direction, basic);
   }

   public static void transferEnergyToSide(Level level, BlockPos blockPos, Direction direction, IEnergyStorage basic) {
      if (basic instanceof BigEnergyStorage big) {
         long extracting = Math.min(big.getStored(), big.getExtract());
         if (extracting > 0L) {
            big.extractForceEnergy(transferBigEnergyToSide(level, blockPos, direction, extracting, false));
         }
      } else if (basic.canExtract()) {
         int storage = basic.getEnergyStored();
         if (storage > 0) {
            basic.extractEnergy(transferEnergyToSide(level, blockPos, direction, basic.extractEnergy(storage, true), false), false);
         }
      }
   }

   public static int transferEnergyToSide(BlockEntity entity, Direction direction, int amount, boolean simulation) {
      return transferEnergyToSide(entity.getLevel(), entity.getBlockPos(), direction, amount, simulation);
   }

   public static int transferEnergyToSide(Level level, BlockPos blockPos, Direction direction, int amount, boolean simulation) {
      return Ints.saturatedCast(transferBigEnergyToSide(level, blockPos, direction, amount, simulation));
   }

   public static long transferBigEnergyToSide(BlockEntity entity, Direction direction, long amount, boolean simulation) {
      return transferBigEnergyToSide(entity.getLevel(), entity.getBlockPos(), direction, amount, simulation);
   }

   public static long transferBigEnergyToSide(Level level, BlockPos blockPos, Direction direction, long amount, boolean simulation) {
      IEnergyStorage energy = getEnergyStorage(level, blockPos, direction);
      if (energy == null || !energy.canReceive()) {
         return 0L;
      } else {
         return energy instanceof BigEnergyStorage bigStorage
            ? bigStorage.receiveEnergy(amount, simulation)
            : energy.receiveEnergy((int)Math.min(amount, 2147483647L), simulation);
      }
   }

   @Nullable
   public static IEnergyStorage getEnergyStorage(BlockEntity entity, Direction direction) {
      return getEnergyStorage(entity.getLevel(), entity.getBlockPos(), direction);
   }

   @Nullable
   public static IEnergyStorage getEnergyStorage(Level level, BlockPos blockPos, Direction direction) {
      return level != null ? (IEnergyStorage)level.getCapability(EnergyStorage.BLOCK, blockPos.relative(direction), direction) : null;
   }
}
