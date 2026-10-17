package com.industrialreforged.blockentity.mix;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
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
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyAndContainerBlockEntity<T extends IEnergyStorage & INBTSerializable<Tag>> extends EnergyBlockEntity<T> implements IContainerWrapper {
   public final BasicSimpleContainer container;
   protected IItemHandlerModifiable wrapper;

   public EnergyAndContainerBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, T storage, int size) {
      this(holder, blockPos, state, storage, size, 64);
   }

   public EnergyAndContainerBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, T storage, int size, int limit) {
      this((BlockEntityType<?>)holder.value(), blockPos, state, storage, size, limit);
   }

   public EnergyAndContainerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, T storage, int size) {
      this(type, blockPos, state, storage, size, 64);
   }

   public EnergyAndContainerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, T storage, int size, int limit) {
      super(type, blockPos, state, storage);
      this.container = this.createContainer(size, limit);
   }

   protected BasicSimpleContainer createContainer(int size, int limit) {
      return ContainerUtils.simple(this, size, limit);
   }

   @Override
   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.container.save(tag, registries);
   }

   @Override
   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
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
}
