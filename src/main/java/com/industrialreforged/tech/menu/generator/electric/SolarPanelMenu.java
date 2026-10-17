package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SolarPanelMenu extends IRMenuBase<SolarPanelBlockEntity> {
   public static final MenuType<SolarPanelMenu> TYPE = createType(SolarPanelMenu::new);

   public SolarPanelMenu(int containerId, Inventory inv, SolarPanelBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addGrid(entity.container, 17, 59, 4, InputSlot::new);
      // слоты модулей — правее слотов зарядки (раньше были рядом над ними)
      this.addGrid(entity.moduleSlot, 97, 59, 4, InputSlot::new);
      this.addPlayerSlots(inv, 17, 86);
      this.addSync(entity.energy);
      this.addDoubleToSync(() -> entity.production, value -> entity.production = (long)value);
      this.addToSync(() -> (entity.sunIsVisible ? 1 : 0) | (entity.sunIsUp ? 2 : 0), value -> {
         entity.sunIsVisible = (value & 1) == 1;
         entity.sunIsUp = (value >> 1 & 1) == 1;
      });
   }
}
