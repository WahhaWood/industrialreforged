package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.electric.SemifluidGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SemifluidGeneratorMenu extends IRMenuBase<SemifluidGeneratorBlockEntity> {
   public static final MenuType<SemifluidGeneratorMenu> TYPE = createType(SemifluidGeneratorMenu::new);

   public SemifluidGeneratorMenu(int containerId, Inventory inv, SemifluidGeneratorBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addSlot(new InputSlot(entity.input, 0, 27, 21));
      this.addSlot(new InputSlot(entity.output, 0, 27, 54));
      this.addSlot(new InputSlot(entity.container, 0, 117, 49));
      this.addPlayerSlots(inv, 8, 84);
      this.addSync(entity.energy);
      this.addSync(entity.tank);
   }
}
