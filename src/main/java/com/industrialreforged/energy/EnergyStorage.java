package com.industrialreforged.energy;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class EnergyStorage implements IEnergyStorage, INBTSerializable<Tag> {
   private final int defCapacity;
   private final int defReceive;
   private final int defExtract;
   protected int storage;
   protected int capacity;
   protected int receive;
   protected int extract;

   public EnergyStorage(int value) {
      this(value, value, value);
   }

   public EnergyStorage(int capacity, int transfer) {
      this(capacity, transfer, transfer);
   }

   public EnergyStorage(int capacity, int receive, int extract) {
      this.defCapacity = this.capacity = capacity;
      this.defReceive = this.receive = receive;
      this.defExtract = this.extract = extract;
   }

   public int receiveEnergy(int request, boolean simulate) {
      if (this.canReceive()) {
         request = Math.min(request, Math.min(this.capacity - this.storage, this.receive));
         if (!simulate) {
            this.storage += request;
         }

         return request;
      } else {
         return 0;
      }
   }

   public int extractEnergy(int request, boolean simulate) {
      if (this.canExtract()) {
         request = Math.min(request, Math.min(this.storage, this.extract));
         if (!simulate) {
            this.storage -= request;
         }

         return request;
      } else {
         return 0;
      }
   }

   public int getEnergyStored() {
      return this.storage;
   }

   public int getMaxEnergyStored() {
      return this.capacity;
   }

   public boolean canExtract() {
      return this.extract > 0;
   }

   public boolean canReceive() {
      return this.receive > 0;
   }

   public boolean isEmpty() {
      return this.storage <= 0;
   }

   public boolean isFull() {
      return this.getSpace() <= 0;
   }

   public boolean hasSpace() {
      return this.getSpace() > 0;
   }

   public int getSpace() {
      return this.capacity - this.storage;
   }

   public int getStored() {
      return this.storage;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public void setStored(int value) {
      this.storage = value;
   }

   public void injectEnergy(int value) {
      this.injectForceEnergy(value);
      this.checkSpace();
   }

   public void extractEnergy(int value) {
      this.extractForceEnergy(value);
      this.checkSpace();
   }

   public void injectForceEnergy(int value) {
      this.storage += value;
   }

   public void extractForceEnergy(int value) {
      this.storage -= value;
   }

   public void setCapacity(int value) {
      this.capacity = Math.max(value, 0);
   }

   public void setTransfer(int value) {
      this.setReceive(value);
      this.setExtract(value);
   }

   public void setReceive(int value) {
      this.receive = value;
   }

   public void setExtract(int value) {
      this.extract = value;
   }

   public int getReceive() {
      return this.receive;
   }

   public int getExtract() {
      return this.extract;
   }

   public void load(CompoundTag tag, Provider provider) {
      Tag energyTag = tag.get("energy");
      if (energyTag instanceof NumericTag) {
         this.deserializeNBT(provider, energyTag);
      }
   }

   public void save(CompoundTag tag, Provider provider) {
      tag.put("energy", this.serializeNBT(provider));
   }

   public Tag serializeNBT(@NotNull Provider provider) {
      return IntTag.valueOf(this.getEnergyStored());
   }

   public void deserializeNBT(@NotNull Provider provider, @NotNull Tag nbt) {
      if (nbt instanceof NumericTag tag) {
         this.storage = tag.getAsInt();
      } else {
         throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
      }
   }

   public int getDefCapacity() {
      return this.defCapacity;
   }

   public int getDefReceive() {
      return this.defReceive;
   }

   public int getDefExtract() {
      return this.defExtract;
   }

   private void checkSpace() {
      if (this.storage > this.capacity) {
         this.storage = this.capacity;
      } else if (this.storage < 0) {
         this.storage = 0;
      }
   }

   public static EnergyStorage asSource(int capacity) {
      return asSource(capacity, capacity);
   }

   public static EnergyStorage asSource(int capacity, int extract) {
      return new EnergyStorage(capacity, 0, extract);
   }

   public static EnergyStorage asRecipient(int capacity) {
      return asRecipient(capacity, capacity);
   }

   public static EnergyStorage asRecipient(int capacity, int receive) {
      return new EnergyStorage(capacity, receive, 0);
   }

   public static EnergyStorage as(int capacity) {
      return as(capacity, capacity);
   }

   public static EnergyStorage as(int capacity, int transfer) {
      return new EnergyStorage(capacity, transfer, transfer);
   }

   public static EnergyStorage as(int capacity, int receive, int extract) {
      return new EnergyStorage(capacity, receive, extract);
   }
}
