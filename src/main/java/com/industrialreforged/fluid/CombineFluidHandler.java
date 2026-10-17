package com.industrialreforged.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class CombineFluidHandler implements IFluidHandler {
   private final IFluidHandler[] handlers;

   public CombineFluidHandler(IFluidHandler... handlers) {
      this.handlers = handlers;
   }

   public int getTanks() {
      return this.handlers.length;
   }

   public FluidStack getFluidInTank(int tank) {
      return this.handlers[tank].getFluidInTank(0);
   }

   public int getTankCapacity(int tank) {
      return this.handlers[tank].getTankCapacity(0);
   }

   public boolean isFluidValid(int tank, FluidStack stack) {
      return this.handlers[tank].isFluidValid(0, stack);
   }

   public int fill(FluidStack resource, FluidAction action) {
      FluidStack stack = resource.copy();

      for (IFluidHandler handler : this.handlers) {
         stack.shrink(handler.fill(stack, action));
         if (stack.isEmpty()) {
            break;
         }
      }

      return resource.getAmount() - stack.getAmount();
   }

   public FluidStack drain(FluidStack resource, FluidAction action) {
      FluidStack drain = resource.copy();

      for (IFluidHandler handler : this.handlers) {
         FluidStack stack = handler.drain(drain, action);
         int amount = stack.getAmount();
         if (amount > 0 && FluidStack.isSameFluidSameComponents(stack, drain)) {
            drain.shrink(amount);
            if (drain.isEmpty()) {
               break;
            }
         }
      }

      int drained = resource.getAmount() - drain.getAmount();
      return drained > 0 ? resource.copyWithAmount(drained) : FluidStack.EMPTY;
   }

   public FluidStack drain(int maxDrain, FluidAction action) {
      FluidStack drain = FluidStack.EMPTY;

      for (IFluidHandler handler : this.handlers) {
         FluidStack stack = handler.drain(maxDrain, action);
         int amount = stack.getAmount();
         if (amount > 0) {
            if (drain.isEmpty()) {
               drain = stack;
               maxDrain -= amount;
            } else if (FluidStack.isSameFluidSameComponents(stack, drain)) {
               drain.grow(amount);
               maxDrain -= amount;
            }

            if (maxDrain <= 0) {
               break;
            }
         }
      }

      return drain;
   }
}
