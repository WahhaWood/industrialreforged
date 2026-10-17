package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.InductionFurnaceBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class InductionFurnaceMenu extends AbstractElectricMachineMenu<InductionFurnaceBlockEntity> {
   public static final MenuType<InductionFurnaceMenu> TYPE = createType(InductionFurnaceMenu::new);

   public InductionFurnaceMenu(int containerId, Inventory inventory, InductionFurnaceBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 56, 53);
      this.addSlot(new InputSlot(entity.inputSlot, 0, 47, 17));
      this.addSlot(new InputSlot(entity.inputSlot, 1, 63, 17));
      this.addSlot(new OutputSlot(entity.outputSlot, 0, 113, 35));
      this.addSlot(new OutputSlot(entity.outputSlot, 1, 131, 35));
      this.addGrid(entity.upgradeSlot, 153, 26, 2, 1, InputSlot::new);
      // The induction furnace is not a standard machine and does not inherit the progress
      // sync of AbstractStandardMachineMenu — without this the smelt arrow never moved.
      this.addToSync(() -> entity.progress, value -> entity.progress = (short)value);
   }
}
