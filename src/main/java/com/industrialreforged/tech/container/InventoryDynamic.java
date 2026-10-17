package com.industrialreforged.tech.container;

import java.util.LinkedList;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryDynamic implements Container {
   private LinkedList<ItemStack> stacks = new LinkedList<>();
   public int xp = 0;

   public void setItem(int index, ItemStack stack) {
      if (index >= 0) {
         if (stack.isEmpty()) {
            if (index < this.stacks.size()) {
               this.stacks.remove(index);
            }
         } else if (index < this.stacks.size()) {
            this.stacks.set(index, stack);
         } else {
            this.stacks.add(stack);
         }
      }
   }

   public int getContainerSize() {
      return this.stacks.size() + 1;
   }

   public boolean isEmpty() {
      return this.stacks.isEmpty();
   }

   public ItemStack getItem(int index) {
      return index >= 0 && index < this.stacks.size() ? this.stacks.get(index) : ItemStack.EMPTY;
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack itemstack = this.getItem(index);
      if (!itemstack.isEmpty()) {
         if (itemstack.getCount() <= count) {
            this.setItem(index, ItemStack.EMPTY);
         } else {
            itemstack = itemstack.split(count);
            if (itemstack.getCount() == 0) {
               this.setItem(index, ItemStack.EMPTY);
            }
         }
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      ItemStack stack = this.getItem(index);
      if (!stack.isEmpty()) {
         this.setItem(index, ItemStack.EMPTY);
      }

      return stack;
   }

   public int getMaxStackSize() {
      return 64;
   }

   public void setChanged() {
      this.stacks.removeIf(ItemStack::isEmpty);
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public void clearContent() {
      this.stacks.clear();
   }

   public void removeIf(Predicate<ItemStack> filter) {
      this.stacks.removeIf(filter);
   }

   public LinkedList<ItemStack> getStacks() {
      return this.stacks;
   }

   public void setStacks(LinkedList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   public ItemStack addItem(ItemStack stack) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      }

      ItemStack itemstack = stack.copy();
      this.moveItemToOccupiedSlotsWithSameType(itemstack);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      }

      this.moveItemToEmptySlots(itemstack);
      return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
   }

   private void moveItemToEmptySlots(ItemStack stack) {
      for (int i = 0; i < this.getContainerSize(); i++) {
         ItemStack itemstack = this.getItem(i);
         if (itemstack.isEmpty()) {
            this.setItem(i, stack.copyAndClear());
            return;
         }
      }
   }

   private void moveItemToOccupiedSlotsWithSameType(ItemStack stack) {
      for (int i = 0; i < this.getContainerSize(); i++) {
         ItemStack itemstack = this.getItem(i);
         if (ItemStack.isSameItemSameComponents(itemstack, stack)) {
            this.moveItemsBetweenStacks(stack, itemstack);
            if (stack.isEmpty()) {
               return;
            }
         }
      }
   }

   private void moveItemsBetweenStacks(ItemStack stack, ItemStack other) {
      int i = this.getMaxStackSize(other);
      int j = Math.min(stack.getCount(), i - other.getCount());
      if (j > 0) {
         other.grow(j);
         stack.shrink(j);
         this.setChanged();
      }
   }
}
