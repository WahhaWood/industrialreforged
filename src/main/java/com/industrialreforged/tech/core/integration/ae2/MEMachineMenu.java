package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.menu.MenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/** ME machine GUI: no slots, only the recipe switches (sent as {@link MEMachineTogglePayload}) and status. */
public class MEMachineMenu extends MenuBase<MEMachineBlockEntity> {
   /** Client copy of the machine's AE buffer. */
   public double buffer;
   public int lastOperations;
   public boolean enabled = true;
   public boolean cableOnly;

   public MEMachineMenu(int containerId, Inventory inventory, MEMachineBlockEntity entity) {
      super(AE2Integration.ME_MACHINE_MENU.get(), containerId, inventory, entity);
      this.addDoubleToSync(entity::getAECurrentPower, value -> this.buffer = value);
      this.addToSync(() -> entity.lastOperations, value -> this.lastOperations = value);
      this.addToSync(() -> entity.enabled ? 1 : 0, value -> this.enabled = value != 0);
      this.addToSync(() -> entity.cableOnly ? 1 : 0, value -> this.cableOnly = value != 0);
   }

   @Override
   public boolean clickMenuButton(Player player, int id) {
      if (id == 1) {
         this.entity.toggleCableOnly();
      } else {
         this.entity.toggleEnabled();
      }

      return true;
   }
}
