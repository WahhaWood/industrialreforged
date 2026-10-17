package com.industrialreforged.menu.slots;

import java.util.function.IntConsumer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReadOnlySlot extends BaseSlot {
   public ReadOnlySlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange) {
      super(inv, id, x, y, onChange);
   }

   public ReadOnlySlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange, int limitStack) {
      super(inv, id, x, y, onChange, limitStack);
   }

   public ReadOnlySlot(Container inv, int id, int x, int y) {
      super(inv, id, x, y);
   }

   public ReadOnlySlot(Container inv, int id, int x, int y, int limitStack) {
      super(inv, id, x, y, limitStack);
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return false;
   }

   public boolean mayPickup(@NotNull Player player) {
      return false;
   }
}
