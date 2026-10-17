package com.industrialreforged.tech.menu.generator.heat;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.heat.RTHeatGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class RTHeatGeneratorMenu extends IRMenuBase<RTHeatGeneratorBlockEntity> {
   public static final MenuType<RTHeatGeneratorMenu> TYPE = createType(RTHeatGeneratorMenu::new);

   public RTHeatGeneratorMenu(int containerId, Inventory inventory, RTHeatGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addGrid(entity.fuelSlot, 62, 27, 2, 3, InputSlot::new);
      this.addPlayerSlots(inventory, 8, 84);
      this.addToSync(entity::getHeat, entity::setHeat);
   }
}
