package com.industrialreforged.tech.menu.multiblock;

import com.industrialreforged.tech.multiblock.tank.TankControllerBlockEntity;
import com.industrialreforged.menu.MenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

/**
 * Меню контроллера резервуара: без слотов — только показания воды, пара и тепла
 * (в стиле контроллера Extreme Reactors, без лишнего для пара).
 */
public class TankControllerMenu extends MenuBase<TankControllerBlockEntity> {
   public static final MenuType<TankControllerMenu> TYPE = createType(TankControllerMenu::new);

   /** Id кнопки «как собрать» в GUI. */
   public static final int BUTTON_HELP = 0;

   public TankControllerMenu(int containerId, Inventory inventory, TankControllerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addToSync(() -> entity.displayWater, value -> entity.displayWater = value);
      this.addToSync(() -> entity.displaySteam, value -> entity.displaySteam = value);
      this.addToSync(() -> entity.displayHeat, value -> entity.displayHeat = value);
      this.addToSync(() -> entity.displayCapacity, value -> entity.displayCapacity = value);
      this.addToSync(() -> entity.displayFormed, value -> entity.displayFormed = value);
   }

   @Override
   public boolean clickMenuButton(net.minecraft.world.entity.player.Player player, int id) {
      if (id == BUTTON_HELP && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
         net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
            serverPlayer, new com.industrialreforged.tech.core.network.OpenJeiPagePayload("tank")
         );
         return true;
      }
      return super.clickMenuButton(player, id);
   }
}
