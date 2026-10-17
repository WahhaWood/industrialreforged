package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.menu.base.AbstractStandardMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class StandardLevelMachineMenu extends AbstractStandardMachineMenu<AbstractStandardMachineBlockEntity<?>> {
   public static final MenuType<StandardLevelMachineMenu> TYPE = createType(StandardLevelMachineMenu::new);

   public StandardLevelMachineMenu(int containerId, Inventory inventory, AbstractStandardMachineBlockEntity<?> entity) {
      super(TYPE, containerId, inventory, entity, 84, 8, 63, 52, 35, 118, 35, 184, 6);
      this.addDataSlots(entity);
   }

   @Override
   protected void addInputSlot(InvSlotProcessable<?, ?> container, int x, int y) {
      switch (container.getContainerSize()) {
         case 1:
            super.addInputSlot(container, x, y);
            break;
         case 2:
            this.addGrid(container, 0, x, y - 12, 2, 1, 0, 28, InputSlot::new);
            break;
         case 3:
            x -= 20;
            this.addSlot(new InputSlot(container, 0, x, y));
            this.addGrid(container, 1, x + 20, y - 9, 2, 1, 0, 20, InputSlot::new);
            break;
         case 4:
            x -= 24;
            this.addGrid(container, 0, x, y - 9, 2, 1, 0, 20, InputSlot::new);
            this.addGrid(container, 2, x + 20, y - 9, 2, 1, 0, 20, InputSlot::new);
      }
   }

   @Override
   protected void addOutputSlot(InvSlotOutput container, int x, int y) {
      switch (container.getContainerSize()) {
         case 1:
            super.addOutputSlot(container, x, y);
            break;
         case 2:
            this.addGrid(container, 0, x, y - 12, 2, 1, 0, 28, OutputSlot::new);
            break;
         case 3:
            x -= 10;
            this.addSlot(new OutputSlot(container, 0, x + 8, y));
            this.addGrid(container, 1, x + 36, y - 12, 2, 1, 0, 28, OutputSlot::new);
            break;
         case 4:
            this.addGrid(container, 0, 123, y - 14, 2, 1, 0, 28, OutputSlot::new);
            this.addGrid(container, 2, 151, y - 14, 2, 1, 0, 28, OutputSlot::new);
      }
   }
}
