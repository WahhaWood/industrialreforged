package com.industrialreforged.tech.container;

import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

public class InventoryRange {
   public Container inv;
   public Direction face;
   public WorldlyContainer sidedInv;
   public int[] slots;

   @Deprecated
   public InventoryRange(Container inv, int side) {
      this(inv, Direction.from3DDataValue(side));
   }

   public InventoryRange(Container inv, Direction side) {
      this.inv = inv;
      this.face = side;
      if (inv instanceof WorldlyContainer) {
         this.sidedInv = (WorldlyContainer)inv;
         this.slots = this.sidedInv.getSlotsForFace(this.face);
      } else {
         this.slots = new int[inv.getContainerSize()];
         int i = 0;

         while (i < this.slots.length) {
            this.slots[i] = i++;
         }
      }
   }

   public InventoryRange(Container inv) {
      this(inv, Direction.DOWN);
   }

   public InventoryRange(Container inv, int fslot, int lslot) {
      this.inv = inv;
      this.slots = new int[lslot - fslot];

      for (int i = 0; i < this.slots.length; i++) {
         this.slots[i] = fslot + i;
      }
   }

   public InventoryRange(Container inv, InventoryRange access) {
      this.inv = inv;
      this.slots = access.slots;
      this.face = access.face;
      if (inv instanceof WorldlyContainer) {
         this.sidedInv = (WorldlyContainer)inv;
      }
   }

   public boolean canInsertItem(int slot, @Nonnull ItemStack item) {
      return this.sidedInv == null ? this.inv.canPlaceItem(slot, item) : this.sidedInv.canPlaceItemThroughFace(slot, item, this.face);
   }

   public boolean canExtractItem(int slot, @Nonnull ItemStack item) {
      return this.sidedInv == null ? this.inv.canPlaceItem(slot, item) : this.sidedInv.canTakeItemThroughFace(slot, item, this.face);
   }

   public int lastSlot() {
      int last = 0;

      for (int slot : this.slots) {
         if (slot > last) {
            last = slot;
         }
      }

      return last;
   }
}
