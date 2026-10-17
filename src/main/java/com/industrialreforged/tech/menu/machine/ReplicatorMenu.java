package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.menu.sync.ItemStackDataSlot;
import com.industrialreforged.tech.blockentity.machine.ReplicatorBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ReplicatorMenu extends AbstractElectricMachineMenu<ReplicatorBlockEntity> {
   public static final MenuType<ReplicatorMenu> TYPE = createType(ReplicatorMenu::new);

   public ReplicatorMenu(int containerId, Inventory inventory, ReplicatorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 102, 152, 83);
      this.addSlot(new OutputSlot(entity.outputSlot, 0, 90, 59));
      this.addSlot(new InputSlot(entity.fluidSlot, 0, 8, 27));
      this.addSlot(new InputSlot(entity.cellSlot, 0, 8, 72));

      for (int i = 0; i < 4; i++) {
         this.addSlot(new InputSlot(entity.upgradeSlot, i, 152, 8 + i * 18));
      }

      this.addSync(entity.tank);
      this.addDataSlots(entity);
      this.addCustomSync(new ItemStackDataSlot(() -> entity.pattern, stack -> entity.pattern = stack));
   }

   public boolean clickMenuButton(Player player, int id) {
      ((ReplicatorBlockEntity)this.entity).onNetworkEvent(id);
      return true;
   }
}
