package com.industrialreforged.menu.sync;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.core.DataSlotSyncPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractCustomDataSlot {
   protected int index;
   protected boolean first = true;

   public final AbstractCustomDataSlot setIndex(int value) {
      this.index = value;
      return this;
   }

   public boolean check() {
      return this.first;
   }

   public void update(ServerPlayer player) {
      this.first = false;
      IndustrialReforged.sendTo(player, new DataSlotSyncPayload(this.index, this));
   }

   public abstract void write(RegistryFriendlyByteBuf var1);

   public abstract void apply(RegistryFriendlyByteBuf var1);
}
