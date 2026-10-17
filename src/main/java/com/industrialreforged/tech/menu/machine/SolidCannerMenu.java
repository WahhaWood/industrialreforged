package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.machine.SolidCannerBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractStandardMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SolidCannerMenu extends AbstractStandardMachineMenu<SolidCannerBlockEntity> {
   public static final MenuType<SolidCannerMenu> TYPE = createType(SolidCannerMenu::new);

   public SolidCannerMenu(int containerId, Inventory inventory, SolidCannerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 8, 62, 37, 36, 116, 36, 152, 8);
      this.addSlot(new InputSlot(entity.canInputSlot, 0, 67, 36));
   }
}
