package com.industrialreforged.menu.slots;

import java.util.function.IntConsumer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OutputSlot extends BaseSlot {
   public OutputSlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange) {
      super(inv, id, x, y, onChange);
      this.large = true;
   }

   public OutputSlot(Container inv, int id, int x, int y, @Nullable IntConsumer onChange, int limitStack) {
      super(inv, id, x, y, onChange, limitStack);
      this.large = true;
   }

   public OutputSlot(Container inv, int id, int x, int y) {
      super(inv, id, x, y);
      this.large = true;
   }

   public OutputSlot(Container inv, int id, int x, int y, int limitStack) {
      super(inv, id, x, y, limitStack);
      this.large = true;
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return false;
   }
}
