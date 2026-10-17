package com.industrialreforged.item.energy;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.item.api.IEnergyContainerItem;
import com.google.common.primitives.Ints;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class BasicEnergyItem extends Item implements IEnergyContainerItem {
   private final int capacity;
   private final int receive;
   private final int extract;

   public BasicEnergyItem(Properties properties, int capacity) {
      this(properties, capacity, capacity, capacity);
   }

   public BasicEnergyItem(Properties properties, int capacity, int transfer) {
      this(properties, capacity, transfer, transfer);
   }

   public BasicEnergyItem(Properties properties, int capacity, int receive, int extract) {
      super(properties.component(GEDataComponents.ENERGY, 0));
      this.capacity = capacity;
      this.receive = receive;
      this.extract = extract;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      this.appendEnergy(stack, tooltipComponents);
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
   public int getEnergyCapacity(ItemStack stack) {
      return this.capacity;
   }

   @Override
   public int getEnergyReceiveTick(ItemStack stack) {
      return this.receive;
   }

   @Override
   public int getEnergyExtractTick(ItemStack stack) {
      return this.extract;
   }

   public static ItemStack getItemStack(IEnergyContainerItem item, int energy) {
      ItemStack stack = new ItemStack(item);
      stack.set(GEDataComponents.ENERGY, Ints.saturatedCast(energy));
      return stack;
   }
}
