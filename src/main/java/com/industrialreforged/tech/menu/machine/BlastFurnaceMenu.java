package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.BlastFurnaceBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class BlastFurnaceMenu extends FullInvMenu<BlastFurnaceBlockEntity> {
   public static final MenuType<BlastFurnaceMenu> TYPE = createType(BlastFurnaceMenu::new);

   public BlastFurnaceMenu(int containerId, Inventory inventory, BlastFurnaceBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84);
      this.addSlot(new InputSlot(entity.inputSlot, 0, 35, 33));
      this.addGrid(entity.outputSlot, 134, 56, 1, 2, OutputSlot::new);
      this.addSlot(new InputSlot(entity.tankInputSlot, 0, 26, 56));
      this.addSlot(new OutputSlot(entity.tankOutputSlot, 0, 44, 56));
      this.addGrid(entity.upgradeSlot, 152, 8, 2, 1, InputSlot::new);
      this.addDataSlots(entity);
   }
}
