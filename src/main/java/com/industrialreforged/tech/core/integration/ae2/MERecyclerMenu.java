package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.tech.core.recipe.RecyclerRecipe;
import com.industrialreforged.menu.MenuBase;
import com.industrialreforged.menu.slots.FakeSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** ME recycler GUI: 18 sample slots (ghosts: a click copies one item, the hand keeps it) and the inventory. */
public class MERecyclerMenu extends MenuBase<MERecyclerBlockEntity> {
   public double buffer;
   public int lastOperations;
   public long recycled;

   public MERecyclerMenu(int containerId, Inventory inventory, MERecyclerBlockEntity entity) {
      super(AE2Integration.ME_RECYCLER_MENU.get(), containerId, inventory, entity);
      for (int row = 0; row < 2; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new SampleSlot(entity.samples, col + row * 9, 8 + col * 18, 18 + row * 18));
         }
      }

      this.addPlayerSlots(inventory, 8, 67);
      this.addDoubleToSync(entity::getAECurrentPower, value -> this.buffer = value);
      this.addToSync(() -> entity.lastOperations, value -> this.lastOperations = value);
      this.addLongToSync(() -> entity.recycled, value -> this.recycled = value);
   }

   /** A sample: one item, never anything from the recycler blacklist. */
   private static final class SampleSlot extends FakeSlot {
      SampleSlot(Container container, int index, int x, int y) {
         super(container, index, x, y, 1);
      }

      @Override
      public void set(ItemStack stack) {
         if (stack.isEmpty() || RecyclerRecipe.accepts(stack)) {
            super.set(stack);
         }
      }
   }
}
