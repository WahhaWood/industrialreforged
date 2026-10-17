package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.blockentity.simple.EnergyBlockEntity;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.api.tile.IKineticSource;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.menu.generator.electric.KineticGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KineticGeneratorBlockEntity extends EnergyBlockEntity<EnergyStorage> implements IServerTick, MenuProvider {

   public static final int MAX_PRODUCTION = 250000;
   public static final double KINETIC_TO_FE_RATIO = 12.5;

   /**
    * The kinetic generator converts neighbour kinetic energy into FE.
    * Ratio is 1 kPS = 12.5 FE/t, reaching 125,000 FE/t on a tungsten rotor (tier 6 solar equivalent).
    */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      this.updateSource();
      net.minecraft.core.Direction facing = com.industrialreforged.block.RotateBlock.getHorizontalFacing(this);
      int maxKinetic = (int) Math.ceil(MAX_PRODUCTION / KINETIC_TO_FE_RATIO);
      int input = this.source == null ? 0 : this.source.extractKineticEnergy(facing.getOpposite(), maxKinetic, false);
      this.production = Math.min((int)(input * KINETIC_TO_FE_RATIO), MAX_PRODUCTION);

      if (this.production > 0) {
         int space = this.energy.getCapacity() - this.energy.getStored();
         if (space > 0) {
            this.energy.setStored(this.energy.getStored() + Math.min(space, this.production));
         }

         com.industrialreforged.utils.EnergyUtils.transferEnergyAllNear(this, this.energy);
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, this.production > 0);
   }

   protected IKineticSource source;
   public int production;

   public KineticGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.KINETIC_GENERATOR.get(), blockPos, blockState, new EnergyStorage(32000000, 0, MAX_PRODUCTION));
   }

   protected void updateSource() {
      if (this.source == null || ((BlockEntity)this.source).isRemoved()) {
         BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(RotateBlock.getHorizontalFacing(this)));
         this.source = blockEntity instanceof IKineticSource ? (IKineticSource)blockEntity : null;
      }
   }

   public void onLoad() {
      super.onLoad();
      this.updateSource();
   }

   public void setBlockState(BlockState blockState) {
      super.setBlockState(blockState);
      this.updateSource();
   }

   public Component getDisplayName() {
      return IRBlocks.KINETIC_GENERATOR.asItem().getDescription();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new KineticGeneratorMenu(containerId, playerInventory, this);
   }

   public void onNeighborChange(BlockPos neighborPos) {
      if (this.getBlockPos().relative(RotateBlock.getHorizontalFacing(this)).equals(neighborPos)) {
         this.updateSource();
      }
   }
}
