package com.industrialreforged.menu.sync;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemStackDataSlot extends AbstractCustomDataSlot {
   private final Supplier<ItemStack> getter;
   private final Consumer<ItemStack> setter;
   private ItemStack stack = ItemStack.EMPTY;

   public ItemStackDataSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   public boolean check() {
      ItemStack stack = this.getter.get();
      return !ItemStack.isSameItem(stack, this.stack) || stack.getCount() != this.stack.getCount() || super.check();
   }

   @Override
   public void update(ServerPlayer player) {
      this.stack = this.getter.get();
      super.update(player);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, this.stack);
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.stack = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
      this.setter.accept(this.stack);
   }
}
