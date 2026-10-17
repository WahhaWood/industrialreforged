package com.industrialreforged.tech.menu.base;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotProcessable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class AbstractStandardMachineMenu<T extends AbstractStandardMachineBlockEntity<?>> extends AbstractElectricMachineMenu<T> {
   public AbstractStandardMachineMenu(MenuType<? extends AbstractStandardMachineMenu<T>> type, int containerId, Inventory inventory, T entity) {
      this(type, containerId, inventory, entity, 84, 56, 53, 56, 17, 116, 35, 152, 8);
   }

   public AbstractStandardMachineMenu(
      MenuType<? extends AbstractStandardMachineMenu<T>> type,
      int containerId,
      Inventory inventory,
      T entity,
      int pY,
      int dischargeX,
      int dischargeY,
      int inputX,
      int inputY,
      int outputX,
      int outputY,
      int upgradeX,
      int upgradeY
   ) {
      this(
         type,
         containerId,
         inventory,
         entity,
         pY,
         dischargeX,
         dischargeY,
         inputX,
         inputY,
         outputX,
         outputY,
         entity.upgradeSlot.getContainerSize(),
         upgradeX,
         upgradeY
      );
   }

   public AbstractStandardMachineMenu(
      MenuType<? extends AbstractStandardMachineMenu<T>> type,
      int containerId,
      Inventory inventory,
      T entity,
      int pY,
      int dischargeX,
      int dischargeY,
      int inputX,
      int inputY,
      int outputX,
      int outputY,
      int upgrades,
      int upgradeX,
      int upgradeY
   ) {
      super(type, containerId, inventory, entity, pY, dischargeX, dischargeY);
      this.addToSync(() -> entity.guiProgress, value -> entity.guiProgress = value);
      this.addToSync(() -> entity.guiMachineHeat, value -> entity.guiMachineHeat = value);
      if (entity.inputSlot != null) {
         this.addInputSlot(entity.inputSlot, inputX, inputY);
      }

      if (entity.outputSlot != null) {
         this.addOutputSlot(entity.outputSlot, outputX, outputY);
      }

      this.addGrid(entity.upgradeSlot, upgradeX, upgradeY, upgrades, 1, InputSlot::new);
   }

   protected void addInputSlot(InvSlotProcessable<?, ?> container, int x, int y) {
      this.addSlot(new InputSlot(container, 0, x, y));
   }

   protected void addOutputSlot(InvSlotOutput container, int x, int y) {
      this.addSlot(new OutputSlot(container, 0, x, y));
   }
}
