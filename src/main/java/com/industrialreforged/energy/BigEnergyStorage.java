package com.industrialreforged.energy;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class BigEnergyStorage implements IEnergyStorage, INBTSerializable<Tag> {
   private final long defCapacity;
   private final long defReceive;
   private final long defExtract;
   protected long storage;
   protected long capacity;
   protected long receive;
   protected long extract;

   public BigEnergyStorage(long value) {
      this(value, value, value);
   }

   public BigEnergyStorage(long capacity, long transfer) {
      this(capacity, transfer, transfer);
   }

   public BigEnergyStorage(long capacity, long receive, long extract) {
      this.defCapacity = this.capacity = capacity;
      this.defReceive = this.receive = receive;
      this.defExtract = this.extract = extract;
   }

   public int receiveEnergy(int request, boolean simulate) {
      return (int)this.receiveEnergy((long)request, simulate);
   }

   public int extractEnergy(int request, boolean simulate) {
      return (int)this.extractEnergy((long)request, simulate);
   }

   public int getEnergyStored() {
      return (int)Math.min(this.storage, 2147483647L);
   }

   public int getMaxEnergyStored() {
      return (int)Math.min(this.capacity, 2147483647L);
   }

   public boolean canExtract() {
      return this.extract > 0L;
   }

   public boolean canReceive() {
      return this.receive > 0L;
   }

   public long receiveEnergy(long request, boolean simulate) {
      if (this.canReceive()) {
         // A capacity lowered under the stored amount (e.g. a molecular transformer whose
         // recipe was dropped) would otherwise hand out a negative "accepted" value, which
         // callers add to their budget instead of subtracting.
         request = Math.max(0L, Math.min(request, Math.min(this.capacity - this.storage, this.receive)));
         if (!simulate) {
            this.storage += request;
         }

         return request;
      } else {
         return 0L;
      }
   }

   public long extractEnergy(long request, boolean simulate) {
      if (this.canExtract()) {
         request = Math.max(0L, Math.min(request, Math.min(this.storage, this.extract)));
         if (!simulate) {
            this.storage -= request;
         }

         return request;
      } else {
         return 0L;
      }
   }

   public boolean isEmpty() {
      return this.storage <= 0L;
   }

   public boolean isFull() {
      return this.getSpace() <= 0L;
   }

   public boolean hasSpace() {
      return this.getSpace() > 0L;
   }

   public long getSpace() {
      return this.capacity - this.storage;
   }

   public long getStored() {
      return this.storage;
   }

   public long getCapacity() {
      return this.capacity;
   }

   public void setStored(long value) {
      this.storage = value;
   }

   public void injectEnergy(long value) {
      this.injectForceEnergy(value);
      this.checkSpace();
   }

   public void extractEnergy(long value) {
      this.extractForceEnergy(value);
      this.checkSpace();
   }

   public void injectForceEnergy(long value) {
      this.storage += value;
   }

   public void extractForceEnergy(long value) {
      this.storage -= value;
   }

   public void setCapacity(long value) {
      this.capacity = Math.max(value, 0L);
   }

   public void setTransfer(long value) {
      this.setReceive(value);
      this.setExtract(value);
   }

   public void setReceive(long value) {
      this.receive = value;
   }

   public void setExtract(long value) {
      this.extract = value;
   }

   public long getReceive() {
      return this.receive;
   }

   public long getExtract() {
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
      return LongTag.valueOf(this.getStored());
   }

   public void deserializeNBT(@NotNull Provider provider, @NotNull Tag nbt) {
      if (nbt instanceof NumericTag tag) {
         this.storage = tag.getAsLong();
      } else {
         throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
      }
   }

   public long getDefCapacity() {
      return this.defCapacity;
   }

   public long getDefReceive() {
      return this.defReceive;
   }

   public long getDefExtract() {
      return this.defExtract;
   }

   private void checkSpace() {
      if (this.storage > this.capacity) {
         this.storage = this.capacity;
      } else if (this.storage < 0L) {
         this.storage = 0L;
      }
   }

   public static BigEnergyStorage asSource(long capacity) {
      return asSource(capacity, capacity);
   }

   public static BigEnergyStorage asSource(long capacity, long extract) {
      return new BigEnergyStorage(capacity, 0L, extract);
   }

   public static BigEnergyStorage asRecipient(long capacity) {
      return asRecipient(capacity, capacity);
   }

   public static BigEnergyStorage asRecipient(long capacity, long receive) {
      return new BigEnergyStorage(capacity, receive, 0L);
   }

   public static BigEnergyStorage as(long capacity) {
      return as(capacity, capacity);
   }

   public static BigEnergyStorage as(long capacity, long transfer) {
      return new BigEnergyStorage(capacity, transfer, transfer);
   }

   public static BigEnergyStorage as(long capacity, long receive, long extract) {
      return new BigEnergyStorage(capacity, receive, extract);
   }

   public static long getStorage(IEnergyStorage storage) {
      return storage instanceof BigEnergyStorage big ? big.getStored() : storage.getEnergyStored();
   }

   public static long getCapacity(IEnergyStorage storage) {
      return storage instanceof BigEnergyStorage big ? big.getCapacity() : storage.getMaxEnergyStored();
   }
}
