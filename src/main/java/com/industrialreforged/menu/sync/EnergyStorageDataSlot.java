package com.industrialreforged.menu.sync;

import com.industrialreforged.energy.EnergyStorage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class EnergyStorageDataSlot extends AbstractCustomDataSlot {
   private final EnergyStorage energy;
   private int storage;
   private int capacity;
   private int receive;
   private int extract;

   public EnergyStorageDataSlot(EnergyStorage energy) {
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
      buf.writeVarInt(this.capacity);
      buf.writeVarInt(this.storage);
      buf.writeVarInt(this.receive);
      buf.writeVarInt(this.extract);
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.energy.setCapacity(buf.readVarInt());
      this.energy.setStored(buf.readVarInt());
      this.energy.setReceive(buf.readVarInt());
      this.energy.setExtract(buf.readVarInt());
   }
}
