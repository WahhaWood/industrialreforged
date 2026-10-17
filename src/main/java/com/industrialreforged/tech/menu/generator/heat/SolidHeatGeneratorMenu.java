package com.industrialreforged.tech.menu.generator.heat;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.heat.SolidHeatGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SolidHeatGeneratorMenu extends IRMenuBase<SolidHeatGeneratorBlockEntity> {
   public static final MenuType<SolidHeatGeneratorMenu> TYPE = createType(SolidHeatGeneratorMenu::new);

   public SolidHeatGeneratorMenu(int containerId, Inventory inventory, SolidHeatGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addSlot(new InputSlot(entity.input, 0, 80, 45));
      this.addSlot(new InputSlot(entity.output, 0, 113, 45));
      this.addPlayerSlots(inventory, 8, 84);
      this.addToSync(entity::getHeat, entity::setHeat);
      // The fuel bar in the GUI is drawn from these two; nothing synced them, so it stayed
      // empty while the generator was happily burning coal.
      this.addToSync(() -> entity.fuel, value -> entity.fuel = value);
      this.addToSync(() -> entity.itemFuelTime, value -> entity.itemFuelTime = value);
   }
}
