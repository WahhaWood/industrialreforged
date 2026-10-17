package com.industrialreforged.menu.sync;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.world.inventory.DataSlot;

public class IntegerDataSlot extends DataSlot {
   private final IntSupplier getter;
   private final IntConsumer setter;

   public IntegerDataSlot(IntSupplier getter, IntConsumer setter) {
      this.getter = getter;
      this.setter = setter;
   }

   public int get() {
      return this.getter.getAsInt();
   }

   public void set(int value) {
      this.setter.accept(value);
   }
}
