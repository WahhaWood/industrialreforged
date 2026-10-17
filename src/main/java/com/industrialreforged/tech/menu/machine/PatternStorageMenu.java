package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.sync.ItemStackDataSlot;
import com.industrialreforged.tech.blockentity.machine.PatternStorageBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class PatternStorageMenu extends FullInvMenu<PatternStorageBlockEntity> {
   public static final MenuType<PatternStorageMenu> TYPE = createType(PatternStorageMenu::new);

   public PatternStorageMenu(int n, Inventory inventory, PatternStorageBlockEntity entity) {
      super(TYPE, n, inventory, entity, 84);
      this.addSlot(new InputSlot(entity.diskSlot, 0, 18, 20));
      this.addDataSlots(entity);
      this.addCustomSync(new ItemStackDataSlot(() -> entity.pattern, stack -> entity.pattern = stack));
   }

   public boolean clickMenuButton(Player player, int id) {
      ((PatternStorageBlockEntity)this.entity).onNetworkEvent(id);
      return true;
   }
}
