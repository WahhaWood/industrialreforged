package com.industrialreforged.tech.menu.reactor;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.tech.blockentity.reactor.NuclearReactorElectricBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class NuclearReactorMenu extends IRMenuBase<NuclearReactorElectricBlockEntity> {
   public static final MenuType<NuclearReactorMenu> TYPE = createType(NuclearReactorMenu::new);

   /** Id кнопки «как собрать» в GUI. */
   public static final int BUTTON_HELP = 0;

   public NuclearReactorMenu(int containerId, Inventory inventory, NuclearReactorElectricBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addGrid(entity.reactorSlot, 26, 25, 6, 9, InputSlot::new);
      this.addPlayerSlots(inventory, 26, 161);
      this.addDataSlots(entity);
   }

   @Override
   public boolean clickMenuButton(net.minecraft.world.entity.player.Player player, int id) {
      if (id == BUTTON_HELP && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
         net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
            serverPlayer, new com.industrialreforged.tech.core.network.OpenJeiPagePayload("reactor")
         );
         return true;
      }
      return super.clickMenuButton(player, id);
   }
}
