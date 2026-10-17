package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.machine.CannerBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractStandardMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class CannerMenu extends AbstractStandardMachineMenu<CannerBlockEntity> {
   public static final MenuType<CannerMenu> TYPE = createType(CannerMenu::new);

   public CannerMenu(int containerId, Inventory inventory, CannerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 102, 8, 80, 80, 44, 119, 17, 152, 26);
      this.addSlot(new InputSlot(entity.canInputSlot, 0, 41, 17));
      this.addSync(entity.inputTank);
      this.addSync(entity.outputTank);
      this.addDataSlots(entity);
   }

   public boolean clickMenuButton(Player player, int id) {
      ((CannerBlockEntity)this.entity).onNetworkEvent(player, id);
      return true;
   }
}
