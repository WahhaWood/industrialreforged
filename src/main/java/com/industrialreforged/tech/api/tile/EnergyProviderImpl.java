package com.industrialreforged.tech.api.tile;

import net.neoforged.neoforge.energy.IEnergyStorage;

class EnergyProviderImpl implements IEnergyStorage {
   private final EnergyHandler handler;

   EnergyProviderImpl(EnergyHandler handler) {
      this.handler = handler;
   }

   public int receiveEnergy(int amount, boolean simulate) {
      return 0;
   }

   public int extractEnergy(int amount, boolean simulate) {
      return this.handler.extractEnergy(amount, simulate);
   }

   public int getEnergyStored() {
      return this.handler.getEnergyStored();
   }

   public int getMaxEnergyStored() {
      return this.handler.getMaxEnergyStored();
   }

   public boolean canReceive() {
      return false;
   }

   public boolean canExtract() {
      return true;
   }
}
