package com.industrialreforged.tech.api.tile;

import com.industrialreforged.fluid.FluidTank;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

public class FluidHandler implements IFluidHandler {
   private final FluidTank filled;
   private final FluidTank drained;

   public FluidHandler(FluidTank filled, FluidTank drained) {
      this.filled = filled;
      this.drained = drained;
   }

   public int getTanks() {
      return 2;
   }

   @NotNull
   public FluidStack getFluidInTank(int tank) {
      return tank == 0 ? this.filled.getFluidInTank(0) : this.drained.getFluidInTank(0);
   }

   public int getTankCapacity(int tank) {
      return tank == 0 ? this.filled.getTankCapacity(0) : this.drained.getTankCapacity(0);
   }

   public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
      return tank == 0 ? this.filled.isFluidValid(0, stack) : this.drained.isFluidValid(0, stack);
   }

   public int fill(FluidStack resource, FluidAction action) {
      return this.filled.fill(resource, action);
   }

   @NotNull
   public FluidStack drain(FluidStack resource, FluidAction action) {
      return this.drained.drain(resource, action);
   }

   @NotNull
   public FluidStack drain(int maxDrain, FluidAction action) {
      return this.drained.drain(maxDrain, action);
   }
}
