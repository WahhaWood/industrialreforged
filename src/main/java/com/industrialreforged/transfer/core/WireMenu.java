package com.industrialreforged.transfer.core;

import com.industrialreforged.menu.MenuBase;
import com.industrialreforged.transfer.block.WireBlockEntity;
import com.industrialreforged.transfer.init.GTMenuTypes;
import com.industrialreforged.transfer.util.ConnectionData;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WireMenu extends MenuBase<WireBlockEntity> {
   public final ConnectionData.Resistor resistor;
   public final Direction direction;
   private int gridSize;
   public long throwThisLong;
   public long outputValue;
   public boolean replaceFlag;
   public int throwThisInt;

   public WireMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
      this(containerId, inventory, (WireBlockEntity)inventory.player.level().getBlockEntity(buf.readBlockPos()), Direction.values()[buf.readByte()]);
   }

   public WireMenu(int containerId, Inventory inventory, WireBlockEntity cable, Direction direction) {
      super(GTMenuTypes.ENERGY_CABLE.get(), containerId, inventory, cable);
      ConnectionData connectionData = cable.getOrCreate(direction);
      // Only the universal (level 10) wire offers flow control.  Every other
      // wire exposes a detached sentinel with mode -1, so its screen shows the
      // plain input/output readings without the limit controls.
      if (cable.getBlock().level >= 10) {
         if (connectionData.resistor == null) {
            connectionData.resistor = new ConnectionData.Resistor(0, -1L);
         }

         this.resistor = connectionData.resistor;
      } else {
         this.resistor = new ConnectionData.Resistor(-1, -1L);
      }
      this.direction = direction;

      for (int x = 0; x < 9; x++) {
         this.addSlot(new Slot(inventory, x, 8 + x * 18, 142));
      }

      for (int y = 0; y < 3; y++) {
         for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, 9 + x + y * 9, 8 + x * 18, 84 + 18 * y));
         }
      }

      if (cable.getLevel() != null && !cable.getLevel().isClientSide) {
         this.gridSize = cable.collectGrid().size();
      }

      this.addToSync(() -> this.gridSize, value -> this.throwThisInt = value);
      this.addToSync(() -> this.replaceFlag ? 1 : 0, value -> this.replaceFlag = value == 1);
      this.addToSync(() -> this.resistor.mode, value -> this.resistor.mode = value);
      this.addDoubleToSync(() -> this.resistor.value, value -> this.resistor.value = (long)value);
      this.addDoubleToSync(() -> (double)cable.getInputRate(), value -> this.throwThisLong = (long)value);
      this.addDoubleToSync(() -> (double)cable.getOutputRate(), value -> this.outputValue = (long)value);
   }

   public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
      if (this.replaceFlag) {
         if (!player.level().isClientSide) {
            ;
         }
      } else {
         super.clicked(slotId, dragType, clickType, player);
      }
   }

   public ItemStack quickMoveStack(Player player, int slotId) {
      return ItemStack.EMPTY;
   }

   /** Applies a resistor value coming from the client edit box. */
   public void applyResistor(long value) {
      this.resistor.value = value;
      this.entity.setChanged();
   }

   @Override
   public boolean clickMenuButton(Player player, int id) {
      switch (id) {
         case 0 -> {
            this.entity.breakGrid();
            player.closeContainer();
         }
         case 1 -> this.upgradeGrid(player);
         case 2 -> this.toggleResistorMode();
         case 3 -> this.addResistor(1L);
         case 4 -> this.addResistor(10L);
         case 5 -> this.addResistor(100L);
         case 6 -> this.addResistor(-1L);
         case 7 -> this.addResistor(-10L);
         case 8 -> this.addResistor(-100L);
         default -> {
            return super.clickMenuButton(player, id);
         }
      }

      return true;
   }

   private void upgradeGrid(Player player) {
      int gridSize = this.entity.collectGrid().size();
      com.industrialreforged.transfer.block.WireBlock target = this.heldWire(player);

      if (target == null) {
         target = this.bestStockedWire(player, gridSize);
      }

      if (target == null) {
         target = com.industrialreforged.transfer.init.GTBlocks.getWire(this.entity.getBlock().level + 1);
      }

      if (target == null) {
         return;
      }

      if (!this.entity.upgradeGrid(target, player)) {
         int have = com.industrialreforged.transfer.block.WireBlockEntity.countItem(player, target.asItem());
         player.displayClientMessage(
            net.minecraft.network.chat.Component.translatable(
               "msg.industrialreforged.grid.upgrade", gridSize, have
            ), false
         );
      } else {
         player.closeContainer();
      }
   }

   /**
    * Самый низкий уровень провода выше установленного, которого у игрока хватает на
    * всю ветку. Поведение «+1 уровень» сохраняется, когда следующий уровень в наличии,
    * а пропущенные уровни перепрыгиваются: запаса проводов высокого уровня достаточно,
    * чтобы кнопка сработала сразу на него.
    */
   private com.industrialreforged.transfer.block.WireBlock bestStockedWire(Player player, int gridSize) {
      int current = this.entity.getBlock().level;
      for (int level = current + 1; ; level++) {
         com.industrialreforged.transfer.block.WireBlock wire =
            com.industrialreforged.transfer.init.GTBlocks.getWire(level);
         if (wire == null) {
            return null;
         }
         if (com.industrialreforged.transfer.block.WireBlockEntity.countItem(player, wire.asItem()) >= gridSize) {
            return wire;
         }
      }
   }

   private com.industrialreforged.transfer.block.WireBlock heldWire(Player player) {
      for (net.minecraft.world.InteractionHand hand : net.minecraft.world.InteractionHand.values()) {
         ItemStack stack = player.getItemInHand(hand);
         if (stack.getItem() instanceof com.industrialreforged.transfer.block.WireBlockItem item) {
            return item.getBlock() instanceof com.industrialreforged.transfer.block.WireBlock wire ? wire : null;
         }
      }

      return null;
   }

   private void toggleResistorMode() {
      this.resistor.mode = this.resistor.mode == 0 ? 1 : 0;
      this.entity.setChanged();
   }

   private void addResistor(long delta) {
      long value = this.resistor.value;
      if (value == -1L) {
         value = 0L;
      }

      this.resistor.value = Math.max(0L, value + delta);
      this.entity.setChanged();
   }
}
