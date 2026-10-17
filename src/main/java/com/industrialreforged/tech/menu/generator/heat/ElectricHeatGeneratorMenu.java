package com.industrialreforged.tech.menu.generator.heat;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.heat.ElectricHeatGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ElectricHeatGeneratorMenu extends IRMenuBase<ElectricHeatGeneratorBlockEntity> {
   public static final MenuType<ElectricHeatGeneratorMenu> TYPE = createType(ElectricHeatGeneratorMenu::new);

   public ElectricHeatGeneratorMenu(int containerId, Inventory inventory, ElectricHeatGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addGrid(entity.coilSlot, 44, 27, 2, 5, InputSlot::new);
      this.addSlot(new InputSlot(entity.dischargeSlot, 0, 8, 62));
      this.addPlayerSlots(inventory, 8, 84);
      this.addSync(entity.energy);
      this.addToSync(entity::getHeat, entity::setHeat);
   }
}
