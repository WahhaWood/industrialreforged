package com.industrialreforged.menu.slots;

import java.util.function.IntConsumer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class BaseSlot extends Slot {
   @Nullable
   protected IntConsumer onChange;
   protected ItemStack ghostItem = ItemStack.EMPTY;
   protected int limitStack;
   public boolean large;
   public boolean background = true;

   public BaseSlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange) {
      this(inv, id, x, y, onChange, -1);
   }

   public BaseSlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange, int limitStack) {
      this(inv, id, x, y, limitStack);
      this.onChange = onChange;
   }

   public BaseSlot(Container inv, int id, int x, int y) {
      this(inv, id, x, y, -1);
   }

   public BaseSlot(Container inv, int id, int x, int y, int limitStack) {
      super(inv, id, x, y);
      this.limitStack = limitStack;
   }

   public int getMaxStackSize() {
      return this.limitStack < 0 ? super.getMaxStackSize() : this.limitStack;
   }

   public void setChanged() {
      super.setChanged();
      if (this.onChange != null) {
         this.onChange.accept(this.getSlotIndex());
      }
   }

   public BaseSlot setOnChange(@Nullable IntConsumer onChange) {
      this.onChange = onChange;
      return this;
   }

   public BaseSlot setGhostItem(ItemLike item) {
      return this.setGhostItem(new ItemStack(item));
   }

   public BaseSlot setGhostItem(ItemStack item) {
      this.ghostItem = item;
      return this;
   }

   public ItemStack getGhostItem() {
      return this.ghostItem;
   }
}
