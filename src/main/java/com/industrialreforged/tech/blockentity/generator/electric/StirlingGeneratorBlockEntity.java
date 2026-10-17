package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.blockentity.simple.EnergyBlockEntity;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.api.tile.IHeatSource;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.menu.generator.electric.StirlingGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StirlingGeneratorBlockEntity extends EnergyBlockEntity<EnergyStorage> implements IServerTick, MenuProvider {

   private static final int MAX_PRODUCTION = 1000;

   /**
    * The Stirling generator had a source and a production field but nothing ever produced:
    * there was no tick converting the neighbour's heat/kinetic energy into FE.
    */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      this.updateSource();
      net.minecraft.core.Direction facing = com.industrialreforged.block.RotateBlock.getHorizontalFacing(this);
      // Never pull heat the buffer cannot take: a full generator used to drain its heat
      // source (and burn its fuel) for nothing.
      int space = this.energy.getCapacity() - this.energy.getStored();
      int wanted = Math.min(MAX_PRODUCTION, Math.max(0, space));
      int input = this.source == null || wanted <= 0 ? 0 : this.source.extractHeat(facing.getOpposite(), wanted, false);
      this.production = Math.min(input, wanted);

      if (this.production > 0) {
         this.energy.setStored(this.energy.getStored() + this.production);
         com.industrialreforged.utils.EnergyUtils.transferEnergyAllNear(this, this.energy);
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, this.production > 0);
   }

   protected IHeatSource source;
   public int production;

   public StirlingGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.STIRLING_GENERATOR.get(), blockPos, blockState, new EnergyStorage(480000, 0, 4096));
   }

   public int getProduction() {
      return this.production;
   }

   protected void updateSource() {
      if (this.source == null || ((BlockEntity)this.source).isRemoved()) {
         BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(RotateBlock.getHorizontalFacing(this)));
         this.source = blockEntity instanceof IHeatSource ? (IHeatSource)blockEntity : null;
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
      return IRBlocks.STIRLING_GENERATOR.asItem().getDescription();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new StirlingGeneratorMenu(containerId, playerInventory, this);
   }

   public void onNeighborChange(BlockPos neighborPos) {
      if (this.getBlockPos().relative(RotateBlock.getHorizontalFacing(this)).equals(neighborPos)) {
         this.updateSource();
      }
   }
}
