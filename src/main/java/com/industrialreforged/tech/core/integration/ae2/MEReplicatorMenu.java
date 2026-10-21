package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.tech.core.recipe.manager.UuRecipeManager;
import com.industrialreforged.menu.MenuBase;
import com.industrialreforged.menu.slots.FakeSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** ME replicator GUI: 18 sample slots (ghosts: a click copies one item, the hand keeps it) and the inventory. */
public class MEReplicatorMenu extends MenuBase<MEReplicatorBlockEntity> {
   public double buffer;
   public int lastOperations;
   public boolean enabled = true;
   public long replicated;

   public MEReplicatorMenu(int containerId, Inventory inventory, MEReplicatorBlockEntity entity) {
      super(AE2Integration.ME_REPLICATOR_MENU.get(), containerId, inventory, entity);
      for (int row = 0; row < 2; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new SampleSlot(entity.samples, col + row * 9, 8 + col * 18, 18 + row * 18, inventory.player.level()));
         }
      }

      this.addPlayerSlots(inventory, 8, 67);
      this.addDoubleToSync(entity::getAECurrentPower, value -> this.buffer = value);
      this.addToSync(() -> entity.lastOperations, value -> this.lastOperations = value);
      this.addToSync(() -> entity.enabled ? 1 : 0, value -> this.enabled = value != 0);
      this.addLongToSync(() -> entity.replicated, value -> this.replicated = value);
   }

   @Override
   public boolean clickMenuButton(Player player, int id) {
      this.entity.toggleEnabled();
      return true;
   }

   /** A sample: one item that has a scanner recipe. */
   private static final class SampleSlot extends FakeSlot {
      private final Level level;

      SampleSlot(Container container, int index, int x, int y, Level level) {
         super(container, index, x, y, 1);
         this.level = level;
      }

      @Override
      public void set(ItemStack stack) {
         if (stack.isEmpty() || UuRecipeManager.instance.getCost(this.level, stack) > 0) {
            super.set(stack);
         }
      }
   }
}
