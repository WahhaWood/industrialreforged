package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.machine.BlockCutterBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractStandardMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class BlockCutterMenu extends AbstractStandardMachineMenu<BlockCutterBlockEntity> {
   public static final MenuType<BlockCutterMenu> TYPE = createType(BlockCutterMenu::new);

   public BlockCutterMenu(int containerId, Inventory inventory, BlockCutterBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84, 26, 52, 26, 16, 111, 34, 151, 7);
      InputSlot slot = new InputSlot(entity.cutterSlot, 0, 70, 34);
      slot.background = false;
      this.addSlot(slot);
   }
}
