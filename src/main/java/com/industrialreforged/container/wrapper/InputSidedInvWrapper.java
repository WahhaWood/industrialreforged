package com.industrialreforged.container.wrapper;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputSidedInvWrapper extends SidedInvWrapper {
   public InputSidedInvWrapper(WorldlyContainer inv, @Nullable Direction side) {
      super(inv, side);
   }

   @NotNull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return ItemStack.EMPTY;
   }
}
