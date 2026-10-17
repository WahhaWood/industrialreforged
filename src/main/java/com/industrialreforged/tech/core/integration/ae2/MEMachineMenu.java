package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.menu.MenuBase;
import net.minecraft.world.entity.player.Inventory;

/** ME machine GUI: no slots, only the recipe switches (sent as {@link MEMachineTogglePayload}) and status. */
public class MEMachineMenu extends MenuBase<MEMachineBlockEntity> {
   /** Client copy of the machine's AE buffer. */
   public double buffer;
   public int lastOperations;

   public MEMachineMenu(int containerId, Inventory inventory, MEMachineBlockEntity entity) {
      super(AE2Integration.ME_MACHINE_MENU.get(), containerId, inventory, entity);
      this.addDoubleToSync(entity::getAECurrentPower, value -> this.buffer = value);
      this.addToSync(() -> entity.lastOperations, value -> this.lastOperations = value);
   }
}
