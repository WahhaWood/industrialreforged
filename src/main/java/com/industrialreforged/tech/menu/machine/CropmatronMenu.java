package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.CropmatronBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CropmatronMenu extends AbstractElectricMachineMenu<CropmatronBlockEntity> {
   public static final MenuType<CropmatronMenu> TYPE = createType(CropmatronMenu::new);

   public CropmatronMenu(int containerId, Inventory inventory, CropmatronBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 110, 134, 80);
      this.addGrid(entity.fertilizerSlot, 26, 50, entity.fertilizerSlot.getContainerSize(), InputSlot::new);
      this.addGrid(entity.exInputSlot, 26, 27, entity.exInputSlot.getContainerSize(), InputSlot::new);
      this.addSlot(new InputSlot(entity.wasserinputSlot, 0, 26, 71));
      this.addSlot(new OutputSlot(entity.wasseroutputSlot, 0, 26, 89));
      this.addSync(entity.waterTank);
   }
}
