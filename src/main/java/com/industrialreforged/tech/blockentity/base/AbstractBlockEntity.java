package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.BaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBlockEntity extends BaseBlockEntity {
   private boolean enableRedstoneSignal;
   private int delayUnActive;
   protected boolean redstoneSignal;

   public AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (this.enableRedstoneSignal) {
         tag.putBoolean("redstone_signal", this.redstoneSignal);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (this.enableRedstoneSignal) {
         this.redstoneSignal = tag.getBoolean("redstone_signal");
      }
   }

   public void updateRedstoneSignal(BlockState state, BlockPos neighbor) {
      if (this.enableRedstoneSignal && this.level != null) {
         this.redstoneSignal = this.level.hasNeighborSignal(this.worldPosition);
      }
   }

   public boolean isRedstoneSignal() {
      return this.redstoneSignal;
   }

   protected void enableRedstoneSignal() {
      this.enableRedstoneSignal = true;
   }

   protected void setActive(boolean value) {
      if (!this.level.isClientSide) {
         if (value) {
            this.delayUnActive = 20;
         } else if (this.delayUnActive > 0) {
            this.delayUnActive--;
            return;
         }

         ActiveBlock.setActive(this, value);
      }
   }

   public boolean isActive() {
      return ActiveBlock.isActive(this);
   }

   public Direction getHorizontalFacing() {
      return RotateBlock.getHorizontalFacing(this);
   }

   public Direction getFacing() {
      return RotateBlock.getFacing(this);
   }
}
