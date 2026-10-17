package com.industrialreforged.menu;

import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.menu.slots.BaseSlot;
import com.industrialreforged.menu.slots.FakeSlot;
import com.industrialreforged.menu.sync.AbstractCustomDataSlot;
import com.industrialreforged.menu.sync.BigEnergyStorageDataSlot;
import com.industrialreforged.menu.sync.DoubleDataSlot;
import com.industrialreforged.menu.sync.EnergyStorageDataSlot;
import com.industrialreforged.menu.sync.FluidTankDataSlot;
import com.industrialreforged.menu.sync.IntegerDataSlot;
import com.industrialreforged.menu.sync.LongDataSlot;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.slf4j.Logger;

public abstract class MenuBase<T extends BlockEntity> extends AbstractContainerMenu {
   private static final Logger LOGGER = LogUtils.getLogger();
   public final T entity;
   public final Player player;
   private final ArrayList<AbstractCustomDataSlot> syncCustoms = new ArrayList<>(2);
   private final NonNullList<ItemStack> remoteGhosts = NonNullList.create();
   private int inventoryStartIndexInclude = -1;
   private int inventoryEndIndexExclude;

   public MenuBase(Supplier<MenuType<?>> supplierType, int containerId, Inventory inv, T entity) {
      this(supplierType.get(), containerId, inv, entity);
   }

   public MenuBase(MenuType<?> type, int containerId, Inventory inv, T entity) {
      super(type, containerId);
      this.entity = entity;
      this.player = inv.player;
   }

   protected void addPlayerSlots(Inventory inv, int sX, int sY) {
      this.addPlayerSlots(inv, sX, sY, 18, 18);
   }

   protected void addPlayerSlots(Inventory inv, int sX, int sY, int stepX, int stepY) {
      this.addPlayerSlots(inv, sX, sY, stepX, stepY, 4);
   }

   protected void addPlayerSlots(Inventory inv, int sX, int sY, int stepX, int stepY, int space) {
      this.inventoryStartIndexInclude = this.slots.size();
      this.addGrid(inv, 9, sX, sY, 3, 9, stepX, stepY, Slot::new);
      this.addGrid(inv, 0, sX, sY + stepY * 3 + space, 1, 9, stepX, stepY, Slot::new);
      this.inventoryEndIndexExclude = this.slots.size();
   }

   protected void addGrid(Container container, int sX, int sY, int cols, MenuBase.ISlotFactory factory) {
      this.addGrid(container, sX, sY, 1, cols, factory);
   }

   protected void addGrid(Container container, int sX, int sY, int rows, int cols, MenuBase.ISlotFactory factory) {
      this.addGrid(container, 0, sX, sY, rows, cols, 18, 18, factory);
   }

   protected void addGrid(Container container, int sI, int sX, int sY, int rows, int cols, int stepX, int stepY, MenuBase.ISlotFactory factory) {
      for (int y = 0; y < rows; y++) {
         for (int x = 0; x < cols; x++) {
            this.addSlot(factory.create(container, sI + x + y * cols, sX + x * stepX, sY + y * stepY));
         }
      }
   }

   protected void addToSync(IntSupplier getter, IntConsumer setter) {
      this.addDataSlot(new IntegerDataSlot(getter, setter));
   }

   protected void addLongToSync(LongSupplier getter, LongConsumer setter) {
      this.addCustomSync(new LongDataSlot(getter, setter));
   }

   protected void addDoubleToSync(DoubleSupplier getter, DoubleConsumer setter) {
      this.addCustomSync(new DoubleDataSlot(getter, setter));
   }

   protected void addSync(Object o) {
      if (o instanceof FluidTank tank) {
         this.addCustomSync(new FluidTankDataSlot(tank));
      }

      if (o instanceof EnergyStorage storage) {
         this.addCustomSync(new EnergyStorageDataSlot(storage));
      }

      if (o instanceof BigEnergyStorage storage) {
         this.addCustomSync(new BigEnergyStorageDataSlot(storage));
      }
   }

   protected void addCustomSync(AbstractCustomDataSlot o) {
      this.syncCustoms.add(o.setIndex(this.syncCustoms.size()));
   }

   protected Slot addSlot(Slot slot) {
      this.remoteGhosts.add(ItemStack.EMPTY);
      return super.addSlot(slot);
   }

   public void setItem(int slotId, int stateId, ItemStack stack) {
      if (slotId < 0) {
         if (this.getSlot(-(slotId + 1)) instanceof BaseSlot baseSlot) {
            baseSlot.setGhostItem(stack);
         }

         this.stateId = stateId;
      } else {
         super.setItem(slotId, stateId, stack);
      }
   }

   public final void readSyncData(int index, RegistryFriendlyByteBuf buf) {
      if (index >= 0 && index < this.syncCustoms.size()) {
         this.syncCustoms.get(index).apply(buf);
      }
   }

   private int syncCooldown = 2;

   @Override
   public void broadcastChanges() {
      super.broadcastChanges();

      if (!(this.player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) || this.syncCustoms.isEmpty()) {
         return;
      }

      if (--this.syncCooldown > 0) {
         return;
      }

      this.syncCooldown = 2;

      for (AbstractCustomDataSlot slot : this.syncCustoms) {
         if (slot.check()) {
            slot.update(serverPlayer);
         }
      }
   }

   public boolean stillValid(Player player) {
      return !this.entity.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(this.entity.getBlockPos())) <= 64.0;
   }

   public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
      if (slotId >= 0 && slotId < this.slots.size() && this.getSlot(slotId) instanceof FakeSlot fakeSlot) {
         if (dragType == 1) {
            if (clickType == ClickType.QUICK_MOVE) {
               fakeSlot.set(ItemStack.EMPTY);
            } else {
               ItemStack slot = fakeSlot.getItem();
               if (!slot.isEmpty()) {
                  slot.shrink(1);
                  if (slot.isEmpty()) {
                     fakeSlot.set(ItemStack.EMPTY);
                  }
               }
            }
         } else {
            ItemStack slot = fakeSlot.getItem();
            ItemStack carried = this.getCarried();
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, carried)) {
               fakeSlot.set(slot.copyWithCount(slot.getCount() + carried.getCount()));
            } else if (carried.isEmpty()) {
               fakeSlot.set(ItemStack.EMPTY);
            } else {
               fakeSlot.set(carried);
            }
         }
      } else {
         super.clicked(slotId, dragType, clickType, player);
      }
   }

   public ItemStack quickMoveStack(Player player, int index) {
      if (this.inventoryStartIndexInclude == -1) {
         return ItemStack.EMPTY;
      }

      Slot slot = this.getSlot(index);
      if (!slot.hasItem()) {
         return ItemStack.EMPTY;
      }

      int size = this.slots.size();
      if (size == 36) {
         return ItemStack.EMPTY;
      }

      ItemStack slotItem = slot.getItem();
      ItemStack itemstack = slotItem.copy();
      Inventory inv = player.getInventory();
      if (index >= this.inventoryStartIndexInclude && index < this.inventoryEndIndexExclude) {
         if (this.inventoryStartIndexInclude == 0) {
            if (!this.moveItemStackTo(slotItem, this.inventoryEndIndexExclude, size, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.inventoryEndIndexExclude == size) {
            if (!this.moveItemStackTo(slotItem, 0, this.inventoryStartIndexInclude, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(slotItem, 0, this.inventoryStartIndexInclude, false)) {
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(slotItem, this.inventoryEndIndexExclude, size, false)) {
               return ItemStack.EMPTY;
            }
         }
      } else if (!this.moveItemStackTo(slotItem, this.inventoryStartIndexInclude, this.inventoryEndIndexExclude, true)) {
         return ItemStack.EMPTY;
      }

      if (slotItem.isEmpty()) {
         slot.setByPlayer(ItemStack.EMPTY);
      } else {
         slot.setChanged();
      }

      return itemstack;
   }

   public void setRemoteGhost(int index, ItemStack stack) {
      this.remoteGhosts.set(index, stack.copy());
   }

   public void setRemoteGhostNoCopy(int index, ItemStack stack) {
      if (index >= 0 && index < this.remoteGhosts.size()) {
         this.remoteGhosts.set(index, stack);
      } else {
         LOGGER.debug("Incorrect slot index: {} available slots: {}", index, this.remoteGhosts.size());
      }
   }

   public static <T extends BlockEntity, K extends MenuBase<T>> MenuType<K> createType(MenuBase.IMenuFactory<T, K> factory) {
      return IMenuTypeExtension.create((containerId, inv, data) -> factory.create(containerId, inv, (T)inv.player.level().getBlockEntity(data.readBlockPos())));
   }

   @FunctionalInterface
   public interface IMenuFactory<T extends BlockEntity, K extends MenuBase<T>> {
      K create(int var1, Inventory var2, T var3);
   }

   @FunctionalInterface
   public interface ISlotFactory {
      Slot create(Container var1, int var2, int var3, int var4);
   }
}
