package com.industrialreforged.menu.sync;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class LongDataSlot extends AbstractCustomDataSlot {
   private final LongSupplier getter;
   private final LongConsumer setter;
   private long value;

   public LongDataSlot(LongSupplier getter, LongConsumer setter) {
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   public boolean check() {
      return super.check() || this.getter.getAsLong() != this.value;
   }

   @Override
   public void update(ServerPlayer player) {
      this.value = this.getter.getAsLong();
      super.update(player);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeVarLong(this.value);
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.setter.accept(buf.readVarLong());
   }
}
