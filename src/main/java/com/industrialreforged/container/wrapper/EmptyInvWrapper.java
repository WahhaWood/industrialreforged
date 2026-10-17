package com.industrialreforged.container.wrapper;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * Пустой предметный хендлер (0 слотов). Возвращается для контейнеров без доступа
 * на ввод/вывод, чтобы CombinedInvWrapper не падал с NPE на null-элементе.
 */
public final class EmptyInvWrapper implements IItemHandlerModifiable {
   public static final EmptyInvWrapper INSTANCE = new EmptyInvWrapper();

   private EmptyInvWrapper() {
   }

   @Override
   public void setStackInSlot(int slot, ItemStack stack) {
   }

   @Override
   public int getSlots() {
      return 0;
   }

   @Override
   public ItemStack getStackInSlot(int slot) {
      return ItemStack.EMPTY;
   }

   @Override
   public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
      return stack;
   }

   @Override
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return ItemStack.EMPTY;
   }

   @Override
   public int getSlotLimit(int slot) {
      return 0;
   }

   @Override
   public boolean isItemValid(int slot, ItemStack stack) {
      return false;
   }
}
