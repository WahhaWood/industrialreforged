package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.SolarDestillerBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SolarDestillerMenu extends FullInvMenu<SolarDestillerBlockEntity> {
   public static final MenuType<SolarDestillerMenu> TYPE = createType(SolarDestillerMenu::new);

   public SolarDestillerMenu(int containerId, Inventory inventory, SolarDestillerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 102);
      this.addSlot(new InputSlot(entity.waterInputSlot, 0, 17, 27));
      this.addSlot(new InputSlot(entity.destilledWaterInputSlot, 0, 136, 64));
      this.addSlot(new OutputSlot(entity.waterOutputSlot, 0, 17, 45));
      this.addSlot(new OutputSlot(entity.destilledWaterOutputSlot, 0, 136, 82));
      this.addGrid(entity.upgradeSlot, 152, 8, 2, 1, InputSlot::new);
      this.addSync(entity.inputTank);
      this.addSync(entity.outputTank);
      // The "sun is up" strip is drawn from canWork(), which reads the sky light measured on
      // the server.  Per mille, because the sync channel is integral.
      this.addToSync(
         () -> Math.round(entity.getSkyLightValue() * 1000.0F), value -> entity.setSkyLightValue(value / 1000.0F)
      );
   }
}
