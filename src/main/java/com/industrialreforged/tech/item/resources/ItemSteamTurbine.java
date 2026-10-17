package com.industrialreforged.tech.item.resources;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ItemLike;

/** Материальная турбина для парового кинетического генератора: множитель мощности + износ. */
public class ItemSteamTurbine extends Item {
   private final float efficiency;
   private final Supplier<? extends ItemLike> repairItem;

   public ItemSteamTurbine(Properties properties, float efficiency, Supplier<? extends ItemLike> repairItem) {
      super(properties);
      this.efficiency = efficiency;
      this.repairItem = repairItem;
   }

   /** Множитель кинетической мощности турбины. */
   public float getEfficiency() {
      return this.efficiency;
   }

   @Override
   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return !repair.isEmpty() && repair.is(this.repairItem.get().asItem());
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(Component.translatable("text.industrialreforged.tooltip.turbine.efficiency").append(" ×" + this.efficiency));
      if (stack.getMaxDamage() > 0) {
         tooltip.add(Component.translatable("text.industrialreforged.tooltip.rotor.durability")
            .append(": " + (stack.getMaxDamage() - stack.getDamageValue()) + " / " + stack.getMaxDamage()));
      }
   }
}
