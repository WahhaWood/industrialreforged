package com.industrialreforged.menu.sync;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class DoubleDataSlot extends AbstractCustomDataSlot {
   private final DoubleSupplier getter;
   private final DoubleConsumer setter;
   private double value;

   public DoubleDataSlot(DoubleSupplier getter, DoubleConsumer setter) {
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   public boolean check() {
      return super.check() || this.getter.getAsDouble() != this.value;
   }

   @Override
   public void update(ServerPlayer player) {
      this.value = this.getter.getAsDouble();
      super.update(player);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeDouble(this.value);
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.setter.accept(buf.readDouble());
   }
}
