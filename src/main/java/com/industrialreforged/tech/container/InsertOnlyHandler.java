package com.industrialreforged.tech.container;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * Входной слот сверху — только вставка: сосед сверху больше не может выгребать
 * сырьё из входа (а выталкиватель/затягиватель ходят через контейнеры напрямую
 * и обёрток не видят). Всё остальное делегируется внутреннему обработчику.
 */
public class InsertOnlyHandler implements IItemHandlerModifiable {
   private final IItemHandlerModifiable inner;

   public InsertOnlyHandler(IItemHandlerModifiable inner) {
      this.inner = inner;
   }

   @Override
   public int getSlots() {
      return this.inner.getSlots();
   }

   @Override
   public @NotNull ItemStack getStackInSlot(int slot) {
      return this.inner.getStackInSlot(slot);
   }

   @Override
   public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
      return this.inner.insertItem(slot, stack, simulate);
   }

   @Override
   public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
      return ItemStack.EMPTY;
   }

   @Override
   public int getSlotLimit(int slot) {
      return this.inner.getSlotLimit(slot);
   }

   @Override
   public boolean isItemValid(int slot, @NotNull ItemStack stack) {
      return this.inner.isItemValid(slot, stack);
   }

   @Override
   public void setStackInSlot(int slot, @NotNull ItemStack stack) {
      this.inner.setStackInSlot(slot, stack);
   }
}
