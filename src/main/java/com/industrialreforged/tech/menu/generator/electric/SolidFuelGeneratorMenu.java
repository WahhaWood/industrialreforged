package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.electric.SolidFuelGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SolidFuelGeneratorMenu extends IRMenuBase<SolidFuelGeneratorBlockEntity> {
   public static final MenuType<SolidFuelGeneratorMenu> TYPE = createType(SolidFuelGeneratorMenu::new);

   public SolidFuelGeneratorMenu(int containerId, Inventory inv, SolidFuelGeneratorBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addSlot(new InputSlot(entity.input, 0, 56, 52));
      this.addSlot(new InputSlot(entity.container, 0, 56, 16));
      this.addPlayerSlots(inv, 8, 84);
      this.addSync(entity.energy);
      this.addToSync(() -> entity.fuel, value -> entity.fuel = value);
      this.addToSync(() -> entity.totalFuel, value -> entity.totalFuel = value);
   }
}
