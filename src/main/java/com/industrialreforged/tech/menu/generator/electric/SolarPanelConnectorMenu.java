package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelConnectorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SolarPanelConnectorMenu extends IRMenuBase<SolarPanelConnectorBlockEntity> {
   public static final MenuType<SolarPanelConnectorMenu> TYPE = createType(SolarPanelConnectorMenu::new);

   public SolarPanelConnectorMenu(int containerId, Inventory inv, SolarPanelConnectorBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addGrid(entity.inventory, 17, 59, 9, InputSlot::new);
      this.addPlayerSlots(inv, 17, 86);
      this.addSync(entity.energy);
      this.addDoubleToSync(() -> entity.production, value -> entity.production = (long)value);
      this.addToSync(() -> (entity.sunIsVisible ? 1 : 0) | (entity.sunIsUp ? 2 : 0), value -> {
         entity.sunIsVisible = (value & 1) == 1;
         entity.sunIsUp = (value >> 1 & 1) == 1;
      });
   }
}
