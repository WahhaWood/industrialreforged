package com.industrialreforged.blockentity.mix;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.blockentity.simple.EnergyBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.utils.ContainerUtils;
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
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyAndContainerAndFluidTankBlockEntity<T extends IEnergyStorage & INBTSerializable<Tag>>
   extends EnergyBlockEntity<T>
   implements IContainerWrapper,
   IFluidWrapper {
   public final FluidTank tank;
   public final BasicSimpleContainer container;
   protected IItemHandlerModifiable wrapper;

   public EnergyAndContainerAndFluidTankBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, T energy, int size, FluidTank tank) {
      this(holder, blockPos, state, energy, size, 64, tank);
   }

   public EnergyAndContainerAndFluidTankBlockEntity(
      Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, T energy, int size, int limit, FluidTank tank
   ) {
      this((BlockEntityType<?>)holder.value(), blockPos, state, energy, size, limit, tank);
   }

   public EnergyAndContainerAndFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, T energy, int size, FluidTank tank) {
      this(type, blockPos, state, energy, size, 64, tank);
   }

   public EnergyAndContainerAndFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, T energy, int size, int limit, FluidTank tank) {
      super(type, blockPos, state, energy);
      this.tank = tank;
      this.container = this.createContainer(size, limit);
   }

   protected BasicSimpleContainer createContainer(int size, int limit) {
      return ContainerUtils.simple(this, size, limit);
   }

   @Override
   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.tank.writeToNBT(registries, tag);
      this.container.save(tag, registries);
   }

   @Override
   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.tank.readFromNBT(registries, tag);
      this.container.load(tag, registries);
   }

   @Nullable
   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.wrapper == null) {
         this.wrapper = this.container.createWrapper();
      }

      return this.wrapper;
   }

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }
}
