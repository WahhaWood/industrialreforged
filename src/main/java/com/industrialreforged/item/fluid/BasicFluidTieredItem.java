package com.industrialreforged.item.fluid;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.item.api.IFluidContainerItem;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class BasicFluidTieredItem extends TieredItem implements IFluidContainerItem {
   private final int capacity;
   private final Set<Fluid> list;

   public BasicFluidTieredItem(Tier tier, Properties properties, int capacity) {
      this(tier, properties, capacity, new Fluid[0]);
   }

   public BasicFluidTieredItem(Tier tier, Properties properties, int capacity, Fluid... validates) {
      super(tier, properties.stacksTo(1).component(GEDataComponents.FLUID, SimpleFluidContent.EMPTY));
      this.capacity = capacity;
      this.list = Collections.newSetFromMap(new IdentityHashMap<>());
      Collections.addAll(this.list, validates);
   }

   public Set<Fluid> getValidateList() {
      return this.list;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      this.appendFluid(stack, tooltipComponents);
   }

   public boolean isBarVisible(ItemStack itemStack) {
      return true;
   }

   public int getBarWidth(ItemStack stack) {
      return Math.round(13.0F * this.getRatio(stack));
   }

   public int getBarColor(ItemStack stack) {
      return Mth.hsvToRgb(this.getRatio(stack) / 3.0F, 1.0F, 1.0F);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged || oldStack.getItem() != newStack.getItem();
   }

   public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
      return oldStack.getItem() != newStack.getItem();
   }

   @Override
   public int getFluidCapacity(ItemStack stack) {
      return this.capacity;
   }

   @Override
   public boolean isFluidValid(FluidStack stack) {
      return this.list.isEmpty() || this.list.contains(stack.getFluid());
   }
}
