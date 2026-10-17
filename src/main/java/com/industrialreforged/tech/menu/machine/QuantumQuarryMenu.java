package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.QuantumQuarryBlockEntity;
import com.industrialreforged.tech.menu.base.AbstractMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

/**
 * Меню квантового карьера.
 *
 * Слева сверху сканер, правее — кнопка режима (рисует экран), ниже два слота улучшений,
 * ещё ниже два фильтра; правее — выходной буфер 3x3.
 */
public class QuantumQuarryMenu extends AbstractMachineMenu<QuantumQuarryBlockEntity> {
   public static final MenuType<QuantumQuarryMenu> TYPE = createType(QuantumQuarryMenu::new);

   public QuantumQuarryMenu(int containerId, Inventory inventory, QuantumQuarryBlockEntity entity) {
      super(TYPE, containerId, inventory, entity, 84);
      this.addSync(entity.energy);
      this.addSlot(new InputSlot(entity.scannerSlot, 0, 8, 14));
      this.addSlot(new InputSlot(entity.upgradeSlot, 0, 8, 32));
      this.addSlot(new InputSlot(entity.upgradeSlot, 1, 26, 32));
      this.addSlot(new InputSlot(entity.filterWhiteSlot, 0, 8, 50));
      this.addSlot(new InputSlot(entity.filterBlackSlot, 0, 26, 50));
      this.addGrid(entity.outputSlot, 48, 14, 3, 6, (container, id, x, y) -> {
         OutputSlot slot = new OutputSlot(container, id, x, y);
         slot.large = false;
         return slot;
      });
      this.addToSync(() -> entity.blockMined, value -> entity.blockMined = value);
      this.addToSync(() -> entity.opsLastTick, value -> entity.opsLastTick = value);
      this.addToSync(() -> entity.silkMode ? 1 : 0, value -> entity.silkMode = value == 1);
   }

   @Override
   public boolean clickMenuButton(Player player, int id) {
      if (id == 0) {
         this.entity.silkMode = !this.entity.silkMode;
         return true;
      }

      return false;
   }
}
