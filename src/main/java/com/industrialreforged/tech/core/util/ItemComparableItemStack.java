package com.industrialreforged.tech.core.util;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemComparableItemStack {
   private final Item item;
   private final DataComponentPatch components;
   private final int hashCode;

   public ItemComparableItemStack(ItemStack itemStack, boolean bl) {
      this.item = itemStack.getItem();
      this.components = itemStack.getComponentsPatch().forget(StackUtil.ignoredNbtKeys::contains);
      this.hashCode = this.calculateHashCode();
   }

   private ItemComparableItemStack(ItemComparableItemStack stack) {
      this.item = stack.item;
      this.components = stack.components.forget(dataComponentType -> false);
      this.hashCode = stack.hashCode;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof ItemComparableItemStack itemComparableItemStack)) {
         return false;
      } else if (itemComparableItemStack.hashCode != this.hashCode) {
         return false;
      } else {
         return itemComparableItemStack == this
            ? true
            : itemComparableItemStack.item == this.item
               && (
                  itemComparableItemStack.components == null && this.components == null
                     || itemComparableItemStack.components != null && this.components != null && itemComparableItemStack.components.equals(this.components)
               );
      }
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   private int calculateHashCode() {
      int n = 0;
      if (this.item != null) {
 n = System.identityHashCode(this.item);
      }

      if (this.components != null) {
 n = n * 31 + this.components.hashCode();
      }

      return n;
   }

   public ItemComparableItemStack copy() {
      return this.components == null ? this : new ItemComparableItemStack(this);
   }

   public ItemStack toStack() {
      return this.toStack(1);
   }

   public ItemStack toStack(int n) {
      return this.item == null ? null : new ItemStack(this.item.builtInRegistryHolder(), n, this.components);
   }
}
