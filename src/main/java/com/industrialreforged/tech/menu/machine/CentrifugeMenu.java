package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.CentrifugeBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.menu.base.AbstractStandardMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CentrifugeMenu extends AbstractStandardMachineMenu<CentrifugeBlockEntity> {
   public static final MenuType<CentrifugeMenu> TYPE = createType(CentrifugeMenu::new);

   public CentrifugeMenu(int containerId, Inventory inventory, CentrifugeBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 11, 60, 11, 21, 124, 18, 152, 8);
      this.addToSync(() -> entity.heat, value -> entity.heat = (short)value);
      this.addToSync(() -> entity.workheat, value -> entity.workheat = (short)value);
      this.addToSync(() -> entity.guiProgress, value -> entity.guiProgress = value);
   }

   @Override
   protected void addOutputSlot(InvSlotOutput container, int x, int y) {
      this.addGrid(container, x, y, 3, 1, OutputSlot::new);
   }
}
