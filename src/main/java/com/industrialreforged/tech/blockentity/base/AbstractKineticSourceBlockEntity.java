package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.api.tile.IKineticSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractKineticSourceBlockEntity extends BaseBlockEntity implements IKineticSource, IServerTick, MenuProvider {
   protected int power;

   public AbstractKineticSourceBlockEntity(
      BlockEntityType<? extends AbstractKineticSourceBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(blockEntityType, blockPos, blockState);
   }

   @Override
   public int getKineticEnergy(Direction direction) {
      return this.facingMatchesDirection(direction) ? this.getPower() : 0;
   }

   @Override
   public int extractKineticEnergy(Direction direction, int amount, boolean simulation) {
      if (this.facingMatchesDirection(direction) && this.getPower() > 0) {
         int extract = Math.min(amount, this.getPower());
         if (!simulation) {
            this.power -= extract;
            this.markForSave();
         }

         return extract;
      } else {
         return 0;
      }
   }

   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.power = tag.getInt("power");
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("power", this.power);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().asItem().getDescription();
   }

   public int getPower() {
      return this.power;
   }

   public void setPower(int value) {
      this.power = value;
   }

   protected boolean facingMatchesDirection(Direction direction) {
      return direction == RotateBlock.getHorizontalFacing(this);
   }
}
