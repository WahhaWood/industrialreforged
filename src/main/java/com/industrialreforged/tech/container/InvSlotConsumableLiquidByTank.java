package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.fluid.FluidTank;
import java.util.Collections;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class InvSlotConsumableLiquidByTank extends InvSlotConsumableLiquid {
   public FluidTank tank;

   public InvSlotConsumableLiquidByTank(BaseBlockEntity entity, String string, int size, InvSlotConsumableLiquid.OpType opType, FluidTank tank) {
      super(entity, string, size, opType);
      this.tank = tank;
   }

   public void setTank(FluidTank tank) {
      this.tank = tank;
   }

   @Override
   protected boolean acceptsLiquid(Fluid fluid) {
      return this.tank.isEmpty() || this.tank.getFluid().getFluid() == fluid;
   }

   @Override
   protected Iterable<Fluid> getPossibleFluids() {
      FluidStack fluidStack = this.tank.getFluid();
      return !fluidStack.isEmpty() ? Collections.singletonList(fluidStack.getFluid()) : null;
   }
}
