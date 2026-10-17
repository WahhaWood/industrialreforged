package com.industrialreforged.tech.core.integration.jei.wrapper;

import com.industrialreforged.tech.item.turning.ItemTurningMold;
import com.industrialreforged.tech.item.turning.ItemTurningProduct;
import com.industrialreforged.tech.item.turning.TurningProgram;
import net.minecraft.world.item.Item;

public class TurningWrapper {
   private final ItemTurningMold mold;
   private final Item output;
   private final TurningProgram program;

   public TurningWrapper(ItemTurningMold mold, TurningProgram program) {
      this.mold = mold;
      this.output = ItemTurningProduct.PROGRAMS.get(program);
      this.program = program;
   }

   public ItemTurningMold getInput() {
      return this.mold;
   }

   public Item getOutput() {
      return this.output;
   }

   public TurningProgram getProgram() {
      return this.program;
   }
}
