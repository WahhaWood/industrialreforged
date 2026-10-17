package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.electric.GeothermalGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class GeothermalGeneratorMenu extends IRMenuBase<GeothermalGeneratorBlockEntity> {
   public static final MenuType<GeothermalGeneratorMenu> TYPE = createType(GeothermalGeneratorMenu::new);

   public GeothermalGeneratorMenu(int containerId, Inventory inv, GeothermalGeneratorBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addSlot(new InputSlot(entity.input, 0, 27, 21));
      this.addSlot(new InputSlot(entity.output, 0, 27, 54));
      this.addSlot(new InputSlot(entity.container, 0, 117, 49));
      this.addPlayerSlots(inv, 8, 84);
      this.addSync(entity.energy);
      this.addSync(entity.tank);
   }
}
