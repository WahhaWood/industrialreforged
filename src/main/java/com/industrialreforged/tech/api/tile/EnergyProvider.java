package com.industrialreforged.tech.api.tile;

import net.minecraft.world.level.block.entity.BlockEntity;

public class EnergyProvider extends EnergyHandler {
   public EnergyProvider(BlockEntity blockEntity, int capacity, int extractTick) {
      super(blockEntity, capacity, 0L, extractTick);
   }

   public void setReceive(long value) {
   }

   @Override
   public long receiveEnergy(long amount, boolean simulate) {
      return 0L;
   }

   public boolean canReceive() {
      return false;
   }

   public boolean canExtract() {
      return true;
   }
}
