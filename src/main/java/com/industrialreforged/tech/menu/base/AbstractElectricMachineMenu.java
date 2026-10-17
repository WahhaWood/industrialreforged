package com.industrialreforged.tech.menu.base;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class AbstractElectricMachineMenu<T extends AbstractElectricMachineBlockEntity> extends AbstractMachineMenu<T> {
   public AbstractElectricMachineMenu(MenuType<?> type, int containerId, Inventory inventory, T t, int eX, int eY) {
      this(type, containerId, inventory, t, 84, eX, eY);
   }

   public AbstractElectricMachineMenu(MenuType<?> type, int containerId, Inventory inventory, T t, int pY, int eX, int eY) {
      this(type, containerId, inventory, t, 8, pY, eX, eY);
   }

   public AbstractElectricMachineMenu(MenuType<?> type, int containerId, Inventory inventory, T t, int pX, int pY, int eX, int eY) {
      super(type, containerId, inventory, t, pX, pY);
      this.addSlot(new InputSlot(t.energyContainer, 0, eX, eY));
      this.addSync(t.energy);
   }
}
