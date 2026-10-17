package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public class InvSlotConsumableLiquid extends InvSlotConsumable {
   private InvSlotConsumableLiquid.OpType opType;

   public InvSlotConsumableLiquid(BaseBlockEntity entity, String name, int size) {
      this(entity, name, size, InvSlotConsumableLiquid.OpType.Drain);
   }

   public InvSlotConsumableLiquid(BaseBlockEntity entity, String name, int size, InvSlotConsumableLiquid.OpType opType) {
      super(entity, name, size);
      this.opType = opType;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      IFluidHandlerItem handler = (IFluidHandlerItem)FluidUtil.getFluidHandler(stack).orElse(null);
      if (handler == null) {
         return false;
      }

      if (handler.getTanks() != 1) {
         return false;
      }

      FluidStack fluidStack = handler.getFluidInTank(0);

      return switch (this.opType) {
         case Drain -> !fluidStack.isEmpty() && this.acceptsLiquid(fluidStack.getFluid());
         case Fill -> {
            if (fluidStack.isEmpty()) {
               yield true;
            } else {
               Iterable<Fluid> iterable = this.getPossibleFluids();
               if (iterable != null) {
                  for (Fluid fluid : iterable) {
                     if (handler.isFluidValid(0, new FluidStack(fluid, 1))) {
                        yield true;
                     }
                  }
               }

               yield false;
            }
         }
         case Both -> {
            if (fluidStack.isEmpty()) {
               yield true;
            } else if (this.acceptsLiquid(fluidStack.getFluid())) {
               yield true;
            } else {
               Iterable<Fluid> iterable = this.getPossibleFluids();
               if (iterable != null) {
                  for (Fluid fluid : iterable) {
                     if (handler.isFluidValid(0, new FluidStack(fluid, 1))) {
                        yield true;
                     }
                  }
               }

               yield false;
            }
         }
         case None -> false;
      };
   }

   public void setOpType(InvSlotConsumableLiquid.OpType opType) {
      this.opType = opType;
   }

   public InvSlotConsumableLiquid.OpType getOpType() {
      return this.opType;
   }

   protected boolean acceptsLiquid(Fluid fluid) {
      return true;
   }

   @Nullable
   protected Iterable<Fluid> getPossibleFluids() {
      return null;
   }

   public enum OpType {
      Drain,
      Fill,
      Both,
      None;
   }
}
