package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.CropHarvesterBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CropHarvesterMenu extends AbstractElectricMachineMenu<CropHarvesterBlockEntity> {
   public static final MenuType<CropHarvesterMenu> TYPE = createType(CropHarvesterMenu::new);

   public CropHarvesterMenu(int containerId, Inventory inventory, CropHarvesterBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 16, 53);
      this.addGrid(entity.contentSlot, 48, 17, 3, 5, (container, i, x, y) -> {
         OutputSlot slot = new OutputSlot(container, i, x, y);
         slot.large = false;
         return slot;
      });
      this.addGrid(entity.upgradeSlot, 152, 8, 4, 1, InputSlot::new);
   }
}
