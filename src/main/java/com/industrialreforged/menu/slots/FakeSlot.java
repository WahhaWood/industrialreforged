package com.industrialreforged.menu.slots;

import java.util.function.IntConsumer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeSlot extends BaseSlot {
   public FakeSlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange) {
      super(inv, id, x, y, onChange);
   }

   public FakeSlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange, int limitStack) {
      super(inv, id, x, y, onChange, limitStack);
   }

   public FakeSlot(Container inv, int id, int x, int y) {
      super(inv, id, x, y);
   }

   public FakeSlot(Container inv, int id, int x, int y, int limitStack) {
      super(inv, id, x, y, limitStack);
   }

   public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
   }

   @NotNull
   public ItemStack remove(int amount) {
      return ItemStack.EMPTY;
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return false;
   }

   public void set(ItemStack stack) {
      if (!stack.isEmpty()) {
         stack = stack.copy();
         stack.setCount(Math.min(stack.getCount(), this.getMaxStackSize()));
      }

      super.set(stack);
   }

   public boolean mayPickup(@NotNull Player player) {
      return false;
   }
}
