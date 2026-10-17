package com.industrialreforged.tech.menu.generator.electric;

import com.industrialreforged.tech.blockentity.generator.electric.KineticGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class KineticGeneratorMenu extends IRMenuBase<KineticGeneratorBlockEntity> {
   public static final MenuType<KineticGeneratorMenu> TYPE = createType(KineticGeneratorMenu::new);

   public KineticGeneratorMenu(int containerId, Inventory inv, KineticGeneratorBlockEntity entity) {
      super(TYPE, containerId, inv, entity);
      this.addPlayerSlots(inv, 8, 84);
      this.addSync(entity.energy);
      this.addToSync(() -> entity.production, value -> entity.production = value);
   }
}
