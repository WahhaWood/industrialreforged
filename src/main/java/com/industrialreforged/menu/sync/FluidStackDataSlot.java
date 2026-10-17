package com.industrialreforged.menu.sync;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidStackDataSlot extends AbstractCustomDataSlot {
   private final Supplier<FluidStack> getter;
   private final Consumer<FluidStack> setter;
   private FluidStack stack = FluidStack.EMPTY;

   public FluidStackDataSlot(Supplier<FluidStack> getter, Consumer<FluidStack> setter) {
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   public boolean check() {
      FluidStack stack = this.getter.get();
      return !FluidStack.isSameFluid(stack, this.stack) || stack.getAmount() != this.stack.getAmount() || super.check();
   }

   @Override
   public void update(ServerPlayer player) {
      this.stack = this.getter.get();
      super.update(player);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, this.stack);
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.stack = (FluidStack)FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
      this.setter.accept(this.stack);
   }
}
