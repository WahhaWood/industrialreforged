package com.industrialreforged.tech.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class FullInvMenu<T extends BlockEntity> extends IRMenuBase<T> {
   public FullInvMenu(MenuType<?> type, int containerId, Inventory inventory, T entity, int pY) {
      this(type, containerId, inventory, entity, 8, pY);
   }

   public FullInvMenu(MenuType<?> type, int containerId, Inventory inventory, T entity, int pX, int pY) {
      super(type, containerId, inventory, entity);
      if (pX >= 0 && pY >= 0) {
         this.addPlayerSlots(inventory, pX, pY);
      }
   }
}
