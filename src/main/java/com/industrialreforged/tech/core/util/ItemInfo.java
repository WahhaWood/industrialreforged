package com.industrialreforged.tech.core.util;

import com.industrialreforged.tech.core.ref.IRItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class ItemInfo {
   public static final ItemInfo INSTANCE = new ItemInfo();

   public int getEnergyValue(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return 0;
      } else if (StackUtil.checkItemEquality(itemStack, Items.REDSTONE)) {
         return 800;
      } else {
         return StackUtil.checkItemEquality(itemStack, (ItemLike)IRItems.ENERGIUM_DUST.get()) ? 16000 : 0;
      }
   }

   public int getFuelValue(ItemStack container, boolean canLava) {
      if (container.isEmpty()) {
         return 0;
      }

      FluidStack fluidStack = FluidUtil.getFluidContained(container).orElse(FluidStack.EMPTY);
      boolean isLava = !fluidStack.isEmpty() && fluidStack.getFluid() == Fluids.LAVA;
      if (isLava && !canLava) {
         return 0;
      }

      int n = container.getItem().getBurnTime(container, null);
      return isLava ? n / 10 : n;
   }
}
