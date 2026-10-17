package com.industrialreforged.tech.api.tile;

import net.minecraft.world.level.block.entity.BlockEntity;

public class EnergyReceive extends EnergyHandler {
   public EnergyReceive(BlockEntity blockEntity, int capacity, int receiveTick) {
      super(blockEntity, capacity, receiveTick, 0L);
   }

   public void setExtract(long value) {
   }

   @Override
   public long extractEnergy(long amount, boolean simulate) {
      return 0L;
   }

   public boolean canReceive() {
      return true;
   }

   public boolean canExtract() {
      return false;
   }
}
