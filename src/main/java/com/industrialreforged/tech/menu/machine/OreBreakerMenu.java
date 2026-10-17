package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.OreBreakerBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

/** Меню рудокрушителя: 9 руд, слот книги, 9 выходов, 4 улучшения. */
public class OreBreakerMenu extends AbstractElectricMachineMenu<OreBreakerBlockEntity> {
   public static final MenuType<OreBreakerMenu> TYPE = createType(OreBreakerMenu::new);

   public OreBreakerMenu(int containerId, Inventory inventory, OreBreakerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 20, 84, 8, 53);
      this.addGrid(entity.inputSlot, 34, 17, 3, 3, InputSlot::new);
      this.addSlot(new InputSlot(entity.bookSlot, 0, 93, 19));
      this.addGrid(entity.outputSlot, 116, 17, 3, 3, (container, id, x, y) -> {
         OutputSlot slot = new OutputSlot(container, id, x, y);
         slot.large = false;
         return slot;
      });
      this.addGrid(entity.upgradeSlot, 178, 6, 4, 1, InputSlot::new);
      this.addToSync(() -> entity.guiProgress, value -> entity.guiProgress = value);
      this.addToSync(() -> entity.opTime, value -> entity.opTime = value);
   }
}
