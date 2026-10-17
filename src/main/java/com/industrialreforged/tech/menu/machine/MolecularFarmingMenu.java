package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.MolecularFarmingBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class MolecularFarmingMenu extends AbstractElectricMachineMenu<MolecularFarmingBlockEntity> {
   public static final MenuType<MolecularFarmingMenu> TYPE = createType(MolecularFarmingMenu::new);

   public MolecularFarmingMenu(int containerId, Inventory inventory, MolecularFarmingBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 110, 152, 89);
      this.addSlot(new InputSlot(entity.inputSlot, 0, 28, 26));
      this.addSlot(new OutputSlot(entity.outputSlot, 0, 88, 27));
      this.addSlot(new InputSlot(entity.fluidInputSlot, 0, 26, 71));
      this.addSlot(new OutputSlot(entity.fluidOutputSlot, 0, 26, 89));
      this.addGrid(entity.upgradeSlot, 152, 7, 4, 1, InputSlot::new);
      this.addSync(entity.tank);
      this.addToSync(() -> entity.progress, value -> entity.progress = value);
      this.addToSync(entity::getTimeCreation, entity::setTimeCreation);
      this.addToSync(entity::getConsumption, entity::setConsumption);
   }
}
