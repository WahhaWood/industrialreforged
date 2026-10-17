package com.industrialreforged.tech.menu.generator.kinetic;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.kinetic.StirlingKineticGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class StirlingKineticGeneratorMenu extends IRMenuBase<StirlingKineticGeneratorBlockEntity> {
   public static final MenuType<StirlingKineticGeneratorMenu> TYPE = createType(StirlingKineticGeneratorMenu::new);

   public StirlingKineticGeneratorMenu(int containerId, Inventory inventory, StirlingKineticGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addSlot(new InputSlot(entity.coolfluidinputSlot, 0, 8, 103));
      this.addSlot(new InputSlot(entity.cooloutputSlot, 0, 26, 103));
      this.addSlot(new InputSlot(entity.hotfluidinputSlot, 0, 134, 103));
      this.addSlot(new InputSlot(entity.hotoutputSlot, 0, 152, 103));
      this.addGrid(entity.upgradeSlot, 62, 103, 1, 3, InputSlot::new);
      this.addPlayerSlots(inventory, 8, 122);
      this.addToSync(entity::getPower, entity::setPower);
      // Both fluid columns of the GUI are drawn from these tanks.
      this.addSync(entity.inputTank);
      this.addSync(entity.outputTank);
   }
}
