package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.CondenserBlockEntity;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CondenserMenu extends AbstractElectricMachineMenu<CondenserBlockEntity> {
   public static final MenuType<CondenserMenu> TYPE = createType(CondenserMenu::new);

   public CondenserMenu(int containerId, Inventory inventory, CondenserBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 102, 8, 44);
      this.addSlot(new InputSlot(entity.waterInputSlot, 0, 26, 73));
      this.addSlot(new OutputSlot(entity.waterOutputSlot, 0, 134, 73));
      this.addSlot(new InputSlot(entity.upgradeSlot, 0, 152, 73));

      // В слоты теплоотводов кладём призрачную иконку: видно, что сюда ставить
      for (int i = 0; i < 2; i++) {
         this.addSlot(new InputSlot(entity.ventSlots, i, 26 + i * 108, 26).setGhostItem(IRItems.HEAT_VENT.get()));
         this.addSlot(new InputSlot(entity.ventSlots, i + 2, 26 + i * 108, 44).setGhostItem(IRItems.HEAT_VENT.get()));
      }

      this.addSync(entity.getInputTank());
      this.addSync(entity.getOutputTank());
      this.addToSync(() -> entity.progress, value -> entity.progress = value);
   }
}
