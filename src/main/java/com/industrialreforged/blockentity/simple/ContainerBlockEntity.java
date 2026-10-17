package com.industrialreforged.blockentity.simple;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.utils.ContainerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class ContainerBlockEntity extends BaseBlockEntity implements IContainerWrapper {
   public final BasicSimpleContainer container;
   protected IItemHandlerModifiable wrapper;

   public ContainerBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, int size) {
      this(holder, blockPos, state, size, 64);
   }

   public ContainerBlockEntity(Holder<BlockEntityType<?>> type, BlockPos blockPos, BlockState state, int size, int limit) {
      this((BlockEntityType<?>)type.value(), blockPos, state, size, limit);
   }

   public ContainerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, int size) {
      this(type, blockPos, state, size, 64);
   }

   public ContainerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, int size, int limit) {
      super(type, blockPos, state);
      this.container = this.createContainer(size, limit);
   }

   protected BasicSimpleContainer createContainer(int size, int limit) {
      return ContainerUtils.simple(this, size, limit);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.container.save(tag, registries);
   }

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
