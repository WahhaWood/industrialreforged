package com.industrialreforged.menu.sync;

import com.industrialreforged.energy.BigEnergyStorage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class BigEnergyStorageDataSlot extends AbstractCustomDataSlot {
   private final BigEnergyStorage energy;
   private long storage;
   private long capacity;
   private long receive;
   private long extract;

   public BigEnergyStorageDataSlot(BigEnergyStorage energy) {
      this.energy = energy;
   }

   @Override
   public boolean check() {
      return this.storage != this.energy.getStored()
         || this.capacity != this.energy.getCapacity()
         || this.receive != this.energy.getReceive()
         || this.extract != this.energy.getExtract();
   }

   @Override
   public void update(ServerPlayer player) {
      this.storage = this.energy.getStored();
      this.capacity = this.energy.getCapacity();
      this.receive = this.energy.getReceive();
      this.extract = this.energy.getExtract();
      super.update(player);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeVarLong(this.capacity);
      buf.writeVarLong(this.storage);
      buf.writeVarLong(this.receive);
      buf.writeVarLong(this.extract);
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.energy.setCapacity(buf.readVarLong());
      this.energy.setStored(buf.readVarLong());
      this.energy.setReceive(buf.readVarLong());
      this.energy.setExtract(buf.readVarLong());
   }
}
