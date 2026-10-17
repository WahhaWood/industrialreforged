package com.industrialreforged.tech.menu.base;

import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class AbstractMachineMenu<T extends AbstractMachineBlockEntity> extends FullInvMenu<T> {
   public AbstractMachineMenu(MenuType<?> type, int containerId, Inventory inventory, T t) {
      super(type, containerId, inventory, t, 84);
   }

   public AbstractMachineMenu(MenuType<?> type, int containerId, Inventory inventory, T t, int pY) {
      super(type, containerId, inventory, t, pY);
   }

   public AbstractMachineMenu(MenuType<?> type, int containerId, Inventory inventory, T t, int pX, int pY) {
      super(type, containerId, inventory, t, pX, pY);
   }
}
