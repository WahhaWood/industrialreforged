package com.industrialreforged.tech.core.util;

import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotOutput;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

/**
 * Moves fluid between a machine tank and the containers sitting in its fluid
 * slot (buckets, cells, ...).  Works in both directions depending on the
 * slot's {@link InvSlotConsumableLiquid.OpType}: drain containers into the
 * tank, fill containers from the tank, or both.
 */
public final class FluidSlotHelper {
   private FluidSlotHelper() {
   }

   public static void tick(InvSlotConsumableLiquid slot, @Nullable InvSlotOutput output, FluidTank tank) {
      InvSlotConsumableLiquid.OpType opType = slot.getOpType();
      if (opType == InvSlotConsumableLiquid.OpType.None) {
         return;
      }

      for (int i = 0; i < slot.getContainerSize(); i++) {
         // One iteration moves exactly one container.  The old version replaced
         // the whole stack with the single used container, which voided the
         // rest of a stack of capsules.
         int budget = 64;

         while (budget-- > 0) {
            ItemStack stack = slot.getItem(i);
            if (stack.isEmpty()) {
               break;
            }

            // Everything happens on a copy first: the original stack is only
            // touched once we know the used container has a place to go.
            ItemStack probe = stack.copyWithCount(1);
            IFluidHandlerItem handler = probe.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler == null || handler.getTanks() != 1) {
               break;
            }

            FluidStack toTank = FluidStack.EMPTY;
            FluidStack fromTank = FluidStack.EMPTY;

            if (opType == InvSlotConsumableLiquid.OpType.Drain || opType == InvSlotConsumableLiquid.OpType.Both) {
               FluidStack content = handler.getFluidInTank(0);
               if (!content.isEmpty() && tank.hasSpace() && tank.isFluidValid(content)) {
                  int room = tank.fill(content, FluidAction.SIMULATE);
                  // заливаем сразу минимум 1000 мБ (или весь остаток, если его меньше):
                  // контейнер не должен «капать» по мере освобождения места
                  int pour = Math.min(room, content.getAmount());
                  if (pour >= Math.min(1000, content.getAmount())) {
                     toTank = handler.drain(pour, FluidAction.EXECUTE);
                  }
               }
            }

            if (toTank.isEmpty() && (opType == InvSlotConsumableLiquid.OpType.Fill || opType == InvSlotConsumableLiquid.OpType.Both)) {
               FluidStack tankFluid = tank.getFluid();
               if (!tankFluid.isEmpty() && handler.getFluidInTank(0).isEmpty() && handler.isFluidValid(0, tankFluid)) {
                  FluidStack available = tank.drainForce(
                     Math.min(handler.getTankCapacity(0), tankFluid.getAmount()), FluidAction.SIMULATE
                  );
                  if (!available.isEmpty()) {
                     int accepted = handler.fill(available, FluidAction.EXECUTE);
                     if (accepted > 0) {
                        fromTank = available.copyWithAmount(accepted);
                     }
                  }
               }
            }

            if (toTank.isEmpty() && fromTank.isEmpty()) {
               break;
            }

            ItemStack result = handler.getContainer();
            ItemStack remaining = stack.copyWithCount(stack.getCount() - 1);
            boolean resultToOutput = !result.isEmpty() && output != null && output.canAdd(result);

            if (!result.isEmpty() && !resultToOutput && !remaining.isEmpty()) {
               // the used container has nowhere to go: stop before voiding it
               break;
            }

            if (!toTank.isEmpty()) {
               tank.fill(toTank, FluidAction.EXECUTE);
            } else {
               tank.drainForce(fromTank.getAmount(), FluidAction.EXECUTE);
            }

            if (result.isEmpty()) {
               slot.setItem(i, remaining);
            } else if (resultToOutput) {
               output.add(result);
               slot.setItem(i, remaining);
            } else {
               slot.setItem(i, result);
            }

            slot.setChanged();
         }
      }
   }
}
