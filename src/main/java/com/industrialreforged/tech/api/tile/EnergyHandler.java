package com.industrialreforged.tech.api.tile;

import com.industrialreforged.energy.BigEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyHandler extends BigEnergyStorage {
   private final BlockEntity blockEntity;
   public final long defaultCapacity;
   public final long defaultReceiveTick;
   public final long defaultExtractTick;
   private Direction directionOutput;
   private IEnergyStorage provider;

   public EnergyHandler(BlockEntity blockEntity, long capacity, long receiveTick, long extractTick) {
      super(capacity, receiveTick, extractTick);
      this.blockEntity = blockEntity;
      this.defaultCapacity = capacity;
      this.defaultReceiveTick = receiveTick;
      this.defaultExtractTick = extractTick;
   }

   public void setDirectionOutput(Direction value) {
      this.directionOutput = value;
      this.provider = value == null ? null : new EnergyProviderImpl(this);
   }

   @NotNull
   public IEnergyStorage getCapability(@Nullable Direction side) {
      if (this.directionOutput == null) {
         return this;
      } else {
         return (IEnergyStorage)(this.directionOutput == side ? this.provider : this);
      }
   }

   public void sendEnergyAllDirection() {
      if (this.storage > 0L && this.extract > 0L) {
         if (this.directionOutput != null) {
            this.storage = this.storage - this.sendEnergyFromDirection(this.directionOutput, Math.min(this.storage, this.extract), false);
         } else {
            this.storage = this.storage - this.sendEnergyAllDirection(Math.min(this.storage, this.extract), false);
         }
      }
   }

   public long sendEnergyAllDirection(long amount, boolean simulation) {
      long startAmount = amount;

      for (Direction direction : Direction.values()) {
         amount -= this.sendEnergyFromDirection(direction, amount, simulation);
      }

      return startAmount - amount;
   }

   public void sendEnergyFromDirection(Direction direction) {
      if (this.storage > 0L && this.extract > 0L) {
         this.storage = this.storage - this.sendEnergyFromDirection(direction, Math.min(this.storage, this.extract), false);
      }
   }

   public long sendEnergyFromDirection(Direction direction, long amount, boolean simulation) {
      IEnergyStorage energy = this.getEnergyStorage(direction);
      if (energy == null || !energy.canReceive()) {
         return 0L;
      } else {
         return com.industrialreforged.utils.EnergyUtils.receiveLong(energy, amount, simulation);
      }
   }

   public void load(CompoundTag compoundTag) {
      this.storage = compoundTag.getLong("energy");
   }

   public void save(CompoundTag compoundTag) {
      compoundTag.putLong("energy", this.storage);
   }

   public long receiveEnergy(long amount, boolean simulate) {
      if (this.storage >= this.capacity) {
         return 0L;
      }

      if (this.receive < amount) {
         amount = this.receive;
      }

      return this.receiveForceEnergy(amount, simulate);
   }

   public long receiveForceEnergy(long amount, boolean simulate) {
      long free = this.capacity - this.storage;
      if (free < amount) {
         amount = free;
      }

      if (!simulate) {
         this.storage += amount;
      }

      return amount;
   }

   public boolean hasReceiveEnergy(long amount, boolean simulate) {
      if (amount < this.storage) {
         return false;
      }

      if (!simulate) {
         this.storage += amount;
      }

      return true;
   }

   public long extractEnergy(long amount, boolean simulate) {
      if (this.storage <= 0L) {
         return 0L;
      }

      if (this.extract < amount) {
         amount = this.extract;
      }

      return this.extractForceEnergy(amount, simulate);
   }

   public long extractForceEnergy(long amount, boolean simulate) {
      if (this.storage < amount) {
         amount = this.storage;
      }

      if (!simulate) {
         this.storage -= amount;
      }

      return amount;
   }

   public boolean hasExtractEnergy(long amount, boolean simulate) {
      if (this.storage < amount) {
         return false;
      }

      if (!simulate) {
         this.storage -= amount;
      }

      return true;
   }

   @Nullable
   private IEnergyStorage getEnergyStorage(Direction direction) {
      Level level = this.blockEntity.getLevel();
      return level == null ? null : this.getEnergyStorage(level.getBlockEntity(this.blockEntity.getBlockPos().relative(direction)), direction);
   }

   public IEnergyStorage getEnergyStorage(BlockEntity blockEntity, Direction direction) {
      if (blockEntity == null) {
         return null;
      } else {
         return blockEntity instanceof IEnergyStorage energyStorage
            ? energyStorage
            : (IEnergyStorage)blockEntity.getLevel()
               .getCapability(EnergyStorage.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction.getOpposite());
      }
   }
}
