package com.industrialreforged.container.wrapper;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class OutputInvWrapper extends InvWrapper {
   public OutputInvWrapper(Container inv) {
      super(inv);
   }

   @NotNull
   public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
      return stack;
   }
}
