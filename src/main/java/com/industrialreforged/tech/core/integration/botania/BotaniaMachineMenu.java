package com.industrialreforged.tech.core.integration.botania;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import com.industrialreforged.tech.core.integration.ae2.METoggleMenu;
import com.industrialreforged.tech.core.integration.ae2.MEMachineTogglePayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


/** ME machine GUI: no slots, only the recipe switches (sent as {@link MEMachineTogglePayload}) and status. */
public class BotaniaMachineMenu extends AbstractContainerMenu implements METoggleMenu {
   public final BotaniaMachineBlockEntity entity;
   private final DataSlot operations = DataSlot.standalone();
   private final DataSlot queued = DataSlot.standalone();
   private final DataSlot online = DataSlot.standalone();

   public BotaniaMachineMenu(int containerId, Inventory inventory, BotaniaMachineBlockEntity entity) {
      super(BotaniaMachines.MACHINE_MENU.get(), containerId);
      this.entity = entity;
      addDataSlot(operations);
      addDataSlot(queued);
      addDataSlot(online);
   }

   /** Client side: the server wrote the block position when it opened the menu. */
   public BotaniaMachineMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
      this(containerId, inventory, (BotaniaMachineBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()));
   }

   @Override
   public void broadcastChanges() {
      operations.set(entity.lastOperations);
      queued.set((int) Math.min(Integer.MAX_VALUE, entity.queuedOperations()));
      online.set(entity.getMainNode().isActive() ? 1 : 0);
      super.broadcastChanges();
   }

   public int lastOperations() {
      return operations.get();
   }

   public int queuedOperations() {
      return queued.get();
   }

   public boolean isOnline() {
      return online.get() != 0;
   }

   @Override
   public void applyToggle(MEMachineTogglePayload.Action action, @Nullable ResourceLocation recipe) {
      entity.applyToggle(action, recipe);
   }

   @Override
   public ItemStack quickMoveStack(Player player, int index) {
      return ItemStack.EMPTY;
   }

   @Override
   public boolean stillValid(Player player) {
      return !entity.isRemoved() && player.distanceToSqr(entity.getBlockPos().getX() + 0.5, entity.getBlockPos().getY() + 0.5,
            entity.getBlockPos().getZ() + 0.5) <= 64.0;
   }
}
