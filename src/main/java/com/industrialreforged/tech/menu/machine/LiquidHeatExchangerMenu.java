package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.LiquidHeatExchangerBlockEntity;
import com.industrialreforged.tech.menu.FullInvMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class LiquidHeatExchangerMenu extends FullInvMenu<LiquidHeatExchangerBlockEntity> {
   public static final MenuType<LiquidHeatExchangerMenu> TYPE = createType(LiquidHeatExchangerMenu::new);

   public LiquidHeatExchangerMenu(int containerId, Inventory inventory, LiquidHeatExchangerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 122);
      this.addSlot(new InputSlot(entity.hotfluidinputSlot, 0, 8, 103));
      this.addSlot(new OutputSlot(entity.cooloutputSlot, 0, 152, 103));
      this.addSlot(new InputSlot(entity.coolfluidinputSlot, 0, 134, 103));
      this.addSlot(new OutputSlot(entity.hotoutputSlot, 0, 26, 103));
      this.addGrid(entity.upgradeSlot, 62, 103, 3, InputSlot::new);
      this.addGrid(entity.heatexchangerslots, 0, 46, 50, 2, 5, 17, 22, InputSlot::new);
      this.addSync(entity.inputTank);
      this.addSync(entity.outputTank);
      this.addToSync(entity::getHeat, entity::setHeat);
   }
}
