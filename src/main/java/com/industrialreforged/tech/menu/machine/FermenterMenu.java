package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.FermenterBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class FermenterMenu extends FullInvMenu<FermenterBlockEntity> {
   public static final MenuType<FermenterMenu> TYPE = createType(FermenterMenu::new);

   public FermenterMenu(int containerId, Inventory inventory, FermenterBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 102);
      this.addSlot(new InputSlot(entity.fluidInputCellInSlot, 0, 14, 46));
      this.addSlot(new InputSlot(entity.fluidInputCellOutSlot, 0, 14, 64));
      this.addSlot(new OutputSlot(entity.fluidOutputCellInSlot, 0, 148, 43));
      this.addSlot(new OutputSlot(entity.fluidOutputCellOutSlot, 0, 148, 61));
      this.addSlot(new InputSlot(entity.fertiliserSlot, 0, 86, 83));
      this.addGrid(entity.upgradeSlot, 125, 83, 2, InputSlot::new);
      this.addSync(entity.getInputTank());
      this.addSync(entity.getOutputTank());
      this.addDataSlots(entity);
   }
}
