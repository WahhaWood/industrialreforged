package com.industrialreforged.tech.menu.generator.kinetic;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.generator.kinetic.SteamKineticGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SteamKineticGeneratorMenu extends IRMenuBase<SteamKineticGeneratorBlockEntity> {
   public static final MenuType<SteamKineticGeneratorMenu> TYPE = createType(SteamKineticGeneratorMenu::new);

   public SteamKineticGeneratorMenu(int containerId, Inventory inventory, SteamKineticGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addSlot(new InputSlot(entity.upgradeSlot, 0, 152, 26));
      this.addSlot(new InputSlot(entity.turbineSlot, 0, 80, 26));
      this.addPlayerSlots(inventory, 8, 84);
      // Баки обязаны быть в списке синхронизации: экран рисует шкалу воды по
      // waterTank, и без этого клиент вечно видел пустой бак при полном на сервере.
      // Так же сделано в CondenserMenu, CannerMenu и остальных меню с баками.
      this.addSync(entity.waterTank);
      this.addSync(entity.steamTank);
      this.addToSync(entity::getPower, entity::setPower);
   }
}
