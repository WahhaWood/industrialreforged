package com.industrialreforged.blockentity.mix;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.blockentity.simple.EnergyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyAndFluidTankBlockEntity<T extends IEnergyStorage & INBTSerializable<Tag>> extends EnergyBlockEntity<T> implements IFluidWrapper {
   public final FluidTank tank;

   public EnergyAndFluidTankBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, T storage, FluidTank tank) {
      this((BlockEntityType<?>)holder.value(), blockPos, state, storage, tank);
   }

   public EnergyAndFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, T storage, FluidTank tank) {
      super(type, blockPos, state, storage);
      this.tank = tank;
   }

   @Override
   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.tank.writeToNBT(registries, tag);
   }

   @Override
   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.tank.readFromNBT(registries, tag);
   }

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }
}
