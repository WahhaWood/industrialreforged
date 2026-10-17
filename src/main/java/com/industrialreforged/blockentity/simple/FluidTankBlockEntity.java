package com.industrialreforged.blockentity.simple;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
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

public abstract class FluidTankBlockEntity extends BaseBlockEntity implements IFluidWrapper {
   public final FluidTank tank;

   public FluidTankBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, FluidTank tank) {
      this((BlockEntityType<?>)holder.value(), blockPos, state, tank);
   }

   public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, FluidTank tank) {
      super(type, blockPos, state);
      this.tank = tank;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.tank.writeToNBT(registries, tag);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.tank.readFromNBT(registries, tag);
   }

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }
}
