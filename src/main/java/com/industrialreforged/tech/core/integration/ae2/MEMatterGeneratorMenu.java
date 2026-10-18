package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.menu.MenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/** ME matter generator GUI: no slots, only status. */
public class MEMatterGeneratorMenu extends MenuBase<MEMatterGeneratorBlockEntity> {
   public double buffer;
   public int lastOperations;
   public long produced;
   public long saved;
   public boolean enabled = true;

   public MEMatterGeneratorMenu(int containerId, Inventory inventory, MEMatterGeneratorBlockEntity entity) {
      super(AE2Integration.ME_MATTER_MENU.get(), containerId, inventory, entity);
      this.addDoubleToSync(entity::getAECurrentPower, value -> this.buffer = value);
      this.addToSync(() -> entity.lastOperations, value -> this.lastOperations = value);
      this.addLongToSync(() -> entity.produced, value -> this.produced = value);
      this.addLongToSync(() -> entity.saved, value -> this.saved = value);
      this.addToSync(() -> entity.enabled ? 1 : 0, value -> this.enabled = value != 0);
   }

   @Override
   public boolean clickMenuButton(Player player, int id) {
      this.entity.toggleEnabled();
      return true;
   }
}
