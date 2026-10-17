package com.industrialreforged.tech.core.util;

import com.industrialreforged.fluid.FluidTank;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

public class LiquidUtil {
   public static boolean isFluidTile(BlockEntity blockEntity, Direction direction) {
      return blockEntity != null && FluidUtil.getFluidHandler(blockEntity.getLevel(), blockEntity.getBlockPos(), direction).isPresent();
   }

   public static int fillTile(BlockEntity blockEntity, Direction direction, FluidStack fluidStack, FluidAction action) {
      if (fluidStack == null) {
         throw new IllegalArgumentException("invalid fill medium");
      }

      if (fluidStack.isEmpty()) {
         return 0;
      }

      if (blockEntity == null) {
         return 0;
      }

      IFluidHandler fluidHandler = (IFluidHandler)FluidUtil.getFluidHandler(blockEntity.getLevel(), blockEntity.getBlockPos(), direction).orElse(null);
      return fluidHandler == null ? 0 : Math.max(fluidHandler.fill(fluidStack, action), 0);
   }

   public static List<LiquidUtil.AdjacentFluidHandler> getAdjacentHandlers(BlockEntity blockEntity) {
      ArrayList<LiquidUtil.AdjacentFluidHandler> arrayList = new ArrayList<>();

      for (Direction direction : Util.ALL_DIRS) {
         BlockEntity blockEntity2 = blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos().relative(direction));
         if (isFluidTile(blockEntity2, direction.getOpposite())) {
            arrayList.add(new LiquidUtil.AdjacentFluidHandler(blockEntity2, direction));
         }
      }

      return arrayList;
   }

   public static int distribute(BlockEntity blockEntity, FluidStack irFluidStack, FluidAction action) {
      int n = 0;

      for (LiquidUtil.AdjacentFluidHandler adjacentFluidHandler : getAdjacentHandlers(blockEntity)) {
         int n2 = fillTile(adjacentFluidHandler.handler, adjacentFluidHandler.dir.getOpposite(), irFluidStack, action);
 n += n2;
         irFluidStack.shrink(n2);
         if (irFluidStack.isEmpty()) {
            break;
         }
      }

      irFluidStack.grow(n);
      return n;
   }

   @NotNull
   public static FluidStack tryFluidTransferForce(FluidTank fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
      FluidStack drainable = fluidSource.drain(maxAmount, FluidAction.SIMULATE);
      return !drainable.isEmpty() ? tryFluidTransferForce_Internal(fluidDestination, fluidSource, drainable, doTransfer) : FluidStack.EMPTY;
   }

   @NotNull
   private static FluidStack tryFluidTransferForce_Internal(FluidTank fluidDestination, IFluidHandler fluidSource, FluidStack drainable, boolean doTransfer) {
      int fillableAmount = fluidDestination.fillForce(drainable, FluidAction.SIMULATE);
      if (fillableAmount > 0) {
         drainable.setAmount(fillableAmount);
         if (!doTransfer) {
            return drainable;
         }

         FluidStack drained = fluidSource.drain(drainable, FluidAction.EXECUTE);
         if (!drained.isEmpty()) {
            drained.setAmount(fluidDestination.fillForce(drained, FluidAction.EXECUTE));
            return drained;
         }
      }

      return FluidStack.EMPTY;
   }

   @NotNull
   public static FluidStack tryFluidTransferForce(IFluidHandler fluidDestination, FluidTank fluidSource, int maxAmount, boolean doTransfer) {
      FluidStack drainable = fluidSource.drainForce(maxAmount, FluidAction.SIMULATE);
      if (!drainable.isEmpty()) {
         int fillableAmount = fluidDestination.fill(drainable, FluidAction.SIMULATE);
         if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);
            if (!doTransfer) {
               return drainable;
            }

            FluidStack drained = fluidSource.drainForce(drainable.getAmount(), FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
               drained.setAmount(fluidDestination.fill(drained, FluidAction.EXECUTE));
               return drained;
            }
         }
      }

      return FluidStack.EMPTY;
   }

   public static class AdjacentFluidHandler {
      public final BlockEntity handler;
      public final Direction dir;

      AdjacentFluidHandler(BlockEntity blockEntity, Direction direction) {
         this.handler = blockEntity;
         this.dir = direction;
      }
   }
}
