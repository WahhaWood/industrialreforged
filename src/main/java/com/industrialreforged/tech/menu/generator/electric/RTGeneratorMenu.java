package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.electric.RTGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class RTGeneratorMenu extends IRMenuBase<RTGeneratorBlockEntity> {
   public static final MenuType<RTGeneratorMenu> TYPE = createType(RTGeneratorMenu::new);

   public RTGeneratorMenu(int containerId, Inventory inv, RTGeneratorBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addGrid(entity.input, 30, 25, 2, 3, InputSlot::new);
      this.addPlayerSlots(inv, 8, 84);
      this.addSync(entity.energy);
   }
}
