package com.industrialreforged.tech.menu.item;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbstractHandHeldMenu<T extends AbstractHandHeldContainer> extends AbstractContainerMenu {
   public T container;
   private int slotNumberDisable;
   private final int slotIndexDisable;
   private int inventoryStartIndexInclude = -1;
   private int inventoryEndIndexExclude;

   public AbstractHandHeldMenu(MenuType<?> menuType, int containerId, Inventory inventory, T container) {
      super(menuType, containerId);
      this.container = container;
      this.slotIndexDisable = inventory.selected;
   }

   protected void addPlayerInventorySlots(Inventory inventory, int height) {
      this.addPlayerInventorySlots(inventory, 178, height);
   }

   protected void addPlayerInventorySlots(Inventory inventory, int width, int height) {
      this.inventoryStartIndexInclude = this.slots.size();
      int n4 = (width - 162) / 2;

      for (int n3 = 0; n3 < 3; n3++) {
         for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i + n3 * 9 + 9, n4 + i * 18, height - 82 + n3 * 18));
         }
      }

      for (int var7 = 0; var7 < 9; var7++) {
         Slot slot = new Slot(inventory, var7, n4 + var7 * 18, height - 24);
         this.addSlot(slot);
         if (inventory.selected == var7) {
            this.slotNumberDisable = slot.index;
         }
      }

      this.inventoryEndIndexExclude = this.slots.size();
   }

   public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
      if (slotId != this.slotNumberDisable && (clickType != ClickType.SWAP || dragType != this.slotIndexDisable)) {
         super.clicked(slotId, dragType, clickType, player);
      }
   }

   public ItemStack quickMoveStack(Player player, int index) {
      if (this.inventoryStartIndexInclude == -1) {
         return ItemStack.EMPTY;
      }

      Slot slot = this.getSlot(index);
      if (!slot.hasItem()) {
         return ItemStack.EMPTY;
      }

      int size = this.slots.size();
      if (size == 36) {
         return ItemStack.EMPTY;
      }

      ItemStack slotItem = slot.getItem();
      ItemStack itemstack = slotItem.copy();
      Inventory inv = player.getInventory();
      if (index >= this.inventoryStartIndexInclude && index < this.inventoryEndIndexExclude) {
         if (this.inventoryStartIndexInclude == 0) {
            if (!this.moveItemStackTo(slotItem, this.inventoryEndIndexExclude, size, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.inventoryEndIndexExclude == size) {
            if (!this.moveItemStackTo(slotItem, 0, this.inventoryStartIndexInclude, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(slotItem, 0, this.inventoryStartIndexInclude, false)) {
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(slotItem, this.inventoryEndIndexExclude, size, false)) {
               return ItemStack.EMPTY;
            }
         }
      } else if (!this.moveItemStackTo(slotItem, this.inventoryStartIndexInclude, this.inventoryEndIndexExclude, true)) {
         return ItemStack.EMPTY;
      }

      if (slotItem.isEmpty()) {
         slot.setByPlayer(ItemStack.EMPTY);
      } else {
         slot.setChanged();
      }

      return itemstack;
   }

   public void removed(Player player) {
      this.container.stopOpen(player);
      super.removed(player);
   }

   public boolean stillValid(Player player) {
      Inventory inventory = player.getInventory();
      return inventory.selected == this.slotIndexDisable && inventory.getSelected() == this.container.container;
   }
}
