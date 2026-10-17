package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.machine.CannerBlockEntity;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import com.industrialreforged.tech.item.ItemCell;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class InvSlotConsumableCanner extends InvSlotConsumableLiquid {
   private final CannerBlockEntity canner;

   public InvSlotConsumableCanner(CannerBlockEntity canner, String name, int size) {
      super(canner, name, size);
      this.canner = canner;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return switch (this.canner.getMode()) {
         case EmptyLiquid -> FluidUtil.getFluidHandler(stack).isPresent() && !FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY).isEmpty();
         case BottleLiquid -> FluidUtil.getFluidHandler(stack).isPresent() && FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY).isEmpty();
         case BottleSolid -> {
            ItemStack center = this.canner.inputSlot.getItem();
            Predicate<SolidCannerRecipe> predicate = center.isEmpty() ? r -> r.input.test(stack) : r -> r.center.test(center) && r.input.test(stack);
            yield SolidCannerRecipe.gets().stream().anyMatch(predicate);
         }
         case EnrichLiquid -> stack.getItem() instanceof ItemCell && FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY).isEmpty();
      };
   }
}
