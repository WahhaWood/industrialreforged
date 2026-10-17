package com.industrialreforged.tech.menu.generator.kinetic;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.kinetic.ElectricKineticGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ElectricKineticGeneratorMenu extends IRMenuBase<ElectricKineticGeneratorBlockEntity> {
   public static final MenuType<ElectricKineticGeneratorMenu> TYPE = createType(ElectricKineticGeneratorMenu::new);

   public ElectricKineticGeneratorMenu(int containerId, Inventory inventory, ElectricKineticGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addGrid(entity.motors, 44, 27, 2, 5, InputSlot::new);
      this.addSlot(new InputSlot(entity.discharge, 0, 8, 62));
      this.addPlayerSlots(inventory, 8, 84);
      this.addSync(entity.energy);
      this.addToSync(entity::getPower, entity::setPower);
   }
}
