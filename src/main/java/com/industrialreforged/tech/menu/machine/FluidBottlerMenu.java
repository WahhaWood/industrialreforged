package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.FluidBottlerBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class FluidBottlerMenu extends AbstractElectricMachineMenu<FluidBottlerBlockEntity> {
   public static final MenuType<FluidBottlerMenu> TYPE = createType(FluidBottlerMenu::new);

   public FluidBottlerMenu(int containerId, Inventory inventory, FluidBottlerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 102, 8, 53);
      this.addSlot(new InputSlot(entity.drainInputSlot, 0, 44, 35));
      this.addSlot(new InputSlot(entity.fillInputSlot, 0, 44, 72));
      if (entity.outputSlot != null) {
         this.addSlot(new OutputSlot(entity.outputSlot, 0, 117, 53));
      }

      this.addGrid(entity.upgradeSlot, 152, 26, 4, 1, InputSlot::new);
      this.addSync(entity.tank);
      this.addToSync(() -> (int)(entity.guiProgress * 1000.0F), value -> entity.guiProgress = value * 1000.0F);
   }
}
