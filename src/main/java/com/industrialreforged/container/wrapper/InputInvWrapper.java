package com.industrialreforged.container.wrapper;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class InputInvWrapper extends InvWrapper {
   public InputInvWrapper(Container inv) {
      super(inv);
   }

   @NotNull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return ItemStack.EMPTY;
   }
}
