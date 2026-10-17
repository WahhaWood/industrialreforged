package com.industrialreforged.tech.menu.storage;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.storage.ChargepadBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ChargepadBlockMenu extends AbstractElectricMachineMenu<ChargepadBlockEntity> {
   public static final MenuType<ChargepadBlockMenu> TYPE = createType(ChargepadBlockMenu::new);

   public ChargepadBlockMenu(int containerId, Inventory inventory, ChargepadBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 56, 53);
      this.addSlot(new InputSlot(entity.charge, 0, 56, 17));
      // The redstone button's tooltip reads the mode; the toggle event only changed the
      // server copy, so the button appeared to do nothing.
      this.addToSync(() -> entity.redstoneMode, value -> entity.redstoneMode = (byte)value);
   }
}
