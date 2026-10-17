package com.industrialreforged.blockentity.mix;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.blockentity.simple.ContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public abstract class ContainerAndFluidTankBlockEntity extends ContainerBlockEntity implements IFluidWrapper {
   public final FluidTank tank;

   public ContainerAndFluidTankBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, int size, FluidTank tank) {
      this(holder, blockPos, state, size, 64, tank);
   }

   public ContainerAndFluidTankBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, int size, int limit, FluidTank tank) {
      this((BlockEntityType<?>)holder.value(), blockPos, state, size, limit, tank);
   }

   public ContainerAndFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, int size, FluidTank tank) {
      this(type, blockPos, state, size, 64, tank);
   }

   public ContainerAndFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, int size, int limit, FluidTank tank) {
      super(type, blockPos, state, size, limit);
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
