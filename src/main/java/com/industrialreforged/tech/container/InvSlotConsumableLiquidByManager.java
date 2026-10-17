package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.api.recipe.ILiquidAcceptManager;
import net.minecraft.world.level.material.Fluid;

public class InvSlotConsumableLiquidByManager extends InvSlotConsumableLiquid {
   private final ILiquidAcceptManager manager;

   public InvSlotConsumableLiquidByManager(BaseBlockEntity entity, String name, int size, ILiquidAcceptManager manager) {
      super(entity, name, size);
      this.manager = manager;
   }

   public InvSlotConsumableLiquidByManager(BaseBlockEntity entity, String name, int size, InvSlotConsumableLiquid.OpType opType, ILiquidAcceptManager manager) {
      super(entity, name, size, opType);
      this.manager = manager;
   }

   @Override
   protected boolean acceptsLiquid(Fluid fluid) {
      return this.manager.acceptsFluid(fluid);
   }

   @Override
   protected Iterable<Fluid> getPossibleFluids() {
      return this.manager.getAcceptedFluids();
   }
}
