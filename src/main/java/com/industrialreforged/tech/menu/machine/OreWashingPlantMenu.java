package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.menu.base.AbstractStandardMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class OreWashingPlantMenu extends AbstractStandardMachineMenu<OreWashingPlantBlockEntity> {
   public static final MenuType<OreWashingPlantMenu> TYPE = createType(OreWashingPlantMenu::new);

   public OreWashingPlantMenu(int containerId, Inventory inventory, OreWashingPlantBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 8, 62, 104, 17, 86, 62, 152, 8);
      this.addSlot(new InputSlot(entity.fluidSlot, 0, 38, 17));
      this.addSlot(new OutputSlot(entity.cellSlot, 0, 38, 62));
      this.addSync(entity.fluidTank);
      this.addDataSlots(entity);
   }

   @Override
   protected void addOutputSlot(InvSlotOutput container, int x, int y) {
      this.addGrid(container, x, y, container.getContainerSize(), OutputSlot::new);
   }
}
