package com.industrialreforged.tech.menu.generator.kinetic;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.kinetic.WaterKineticGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class WaterKineticGeneratorMenu extends IRMenuBase<WaterKineticGeneratorBlockEntity> {
   public static final MenuType<WaterKineticGeneratorMenu> TYPE = createType(WaterKineticGeneratorMenu::new);

   public WaterKineticGeneratorMenu(int containerId, Inventory inventory, WaterKineticGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addSlot(new InputSlot(entity.rotorSlot, 0, 80, 26));
      this.addPlayerSlots(inventory, 8, 84);
      this.addToSync(entity::getPower, entity::setPower);
      this.addToSync(entity::getBiomeStateData, entity::setBiomeStateData);
   }
}
