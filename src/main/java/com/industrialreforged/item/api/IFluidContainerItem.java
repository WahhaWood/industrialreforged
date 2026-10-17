package com.industrialreforged.item.api;

import com.industrialreforged.init.GEDataComponents;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public interface IFluidContainerItem extends ItemLike {
   default FluidStack getFluid(ItemStack stack) {
      return this.getFluidContent(stack).copy();
   }

   default SimpleFluidContent getFluidContent(ItemStack stack) {
      return (SimpleFluidContent)stack.getOrDefault(GEDataComponents.FLUID, SimpleFluidContent.EMPTY);
   }

   int getFluidCapacity(ItemStack var1);

   default boolean isFluidValid(FluidStack stack) {
      return true;
   }

   default void appendFluid(ItemStack stack, List<Component> tooltipComponents) {
      SimpleFluidContent content = this.getFluidContent(stack);
      if (!content.isEmpty()) {
         tooltipComponents.add(Component.translatable("text.industrialreforged.fluid.name", new Object[]{content.copy().getHoverName()}));
         tooltipComponents.add(Component.translatable("text.industrialreforged.fluid.storage", new Object[]{content.getAmount(), this.getFluidCapacity(stack)}));
      } else {
         tooltipComponents.add(Component.translatable("text.industrialreforged.fluid.empty"));
      }
   }

   default float getRatio(ItemStack stack) {
      return (float)this.getFluidContent(stack).getAmount() / this.getFluidCapacity(stack);
   }
}
