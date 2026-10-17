package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.machine.IronFurnaceBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class IronFurnaceMenu extends FullInvMenu<IronFurnaceBlockEntity> {
   public static final MenuType<IronFurnaceMenu> TYPE = createType(IronFurnaceMenu::new);

   public IronFurnaceMenu(int containerId, Inventory inventory, IronFurnaceBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84);
      this.addSlot(new InputSlot(entity.inputSlot, 0, 56, 17));
      this.addSlot(new InputSlot(entity.fuelSlot, 0, 56, 53));
      this.addSlot(new InputSlot(entity.outputSlot, 0, 116, 35));
      this.addDataSlots(entity);
   }
}
