package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.machine.ScannerBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ScannerMenu extends AbstractElectricMachineMenu<ScannerBlockEntity> {
   public static final MenuType<ScannerMenu> TYPE = createType(ScannerMenu::new);

   public ScannerMenu(int n, Inventory inventory, ScannerBlockEntity entity) {
      super(TYPE, n, inventory, entity, 84, 8, 43);
      this.addSlot(new InputSlot(entity.inputSlot, 0, 55, 35));
      this.addSlot(new InputSlot(entity.diskSlot, 0, 152, 65));
      this.addDataSlots(entity);
   }

   public boolean clickMenuButton(Player player, int id) {
      ((ScannerBlockEntity)this.entity).onNetworkEvent(id);
      return true;
   }
}
