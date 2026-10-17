package com.industrialreforged.tech.menu.storage;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.storage.ElectricBlockEntity;
import com.industrialreforged.tech.container.slot.SlotArmor;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ElectricBlockMenu extends AbstractElectricMachineMenu<ElectricBlockEntity> {
   public static final MenuType<ElectricBlockMenu> TYPE = createType(ElectricBlockMenu::new);

   public ElectricBlockMenu(int containerId, Inventory inventory, ElectricBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 56, 53);

      for (int i = 0; i < 4; i++) {
         this.addSlot(new SlotArmor(inventory, 3 - i, 8, 8 + 18 * i));
      }

      this.addSlot(new InputSlot(entity.charge, 0, 56, 17));
      this.addToSync(() -> entity.redstoneMode, value -> entity.redstoneMode = (byte)value);
   }
}
