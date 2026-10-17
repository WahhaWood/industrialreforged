package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.LatheBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class LatheMenu extends FullInvMenu<LatheBlockEntity> {
   public static final MenuType<LatheMenu> TYPE = createType(LatheMenu::new);

   public LatheMenu(int containerId, Inventory inventory, LatheBlockEntity turning) {
      super(TYPE, containerId, inventory, turning, 84);
      this.addSlot(new InputSlot(turning.toolSlot, 0, 10, 30));
      this.addSlot(new InputSlot(turning.latheSlot, 0, 10, 12));
      this.addSlot(new OutputSlot(turning.outputSlot, 0, 10, 57));
   }

   public boolean clickMenuButton(Player player, int id) {
      return ((LatheBlockEntity)this.entity).process(id);
   }
}
