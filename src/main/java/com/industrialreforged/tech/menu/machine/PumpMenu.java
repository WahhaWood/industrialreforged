package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.PumpBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class PumpMenu extends AbstractElectricMachineMenu<PumpBlockEntity> {
   public static final MenuType<PumpMenu> TYPE = createType(PumpMenu::new);

   public PumpMenu(int containerId, Inventory inventory, PumpBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 8, 44);
      this.addSlot(new InputSlot(entity.containerSlot, 0, 99, 17));
      this.addSlot(new OutputSlot(entity.outputSlot, 0, 132, 34));
      this.addGrid(entity.upgradeSlot, 152, 8, 4, 1, InputSlot::new);
      this.addSync(entity.tank);
      this.addToSync(() -> (int)(entity.guiProgress * 1000.0F), value -> entity.guiProgress = value * 1000.0F);
   }
}
