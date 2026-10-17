package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.MatterGeneratorBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class MatterGeneratorMenu extends FullInvMenu<MatterGeneratorBlockEntity> {
   public static final MenuType<MatterGeneratorMenu> TYPE = createType(MatterGeneratorMenu::new);

   public MatterGeneratorMenu(int containerId, Inventory inventory, MatterGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84);
      this.addSlot(new OutputSlot(entity.outputSlot, 0, 125, 59));
      this.addSlot(new InputSlot(entity.inputSlot, 0, 125, 23));
      this.addSlot(new InputSlot(entity.amplifierSlot, 0, 8, 49));
      this.addGrid(entity.upgradeSlot, 152, 8, 4, 1, InputSlot::new);
      this.addSync(entity.tank);
      this.addSync(entity.energy);
      this.addLongToSync(() -> entity.amplifier, value -> entity.amplifier = value);
      this.addLongToSync(() -> entity.cycleCost, value -> entity.cycleCost = value);
   }
}
