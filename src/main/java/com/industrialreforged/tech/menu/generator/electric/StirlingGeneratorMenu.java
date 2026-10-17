package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.tech.blockentity.generator.electric.StirlingGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class StirlingGeneratorMenu extends IRMenuBase<StirlingGeneratorBlockEntity> {
   public static final MenuType<StirlingGeneratorMenu> TYPE = createType(StirlingGeneratorMenu::new);

   public StirlingGeneratorMenu(int containerId, Inventory inv, StirlingGeneratorBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addPlayerSlots(inv, 8, 84);
      this.addSync(entity.energy);
      // The "FE/t" line reads the production counter, which the buffer alone never carried.
      this.addToSync(() -> entity.production, value -> entity.production = value);
   }
}
