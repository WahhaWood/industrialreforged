package com.industrialreforged.tech.item.tool;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemToolCrafting extends Item {
   public ItemToolCrafting(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack itemStack, Level level, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("industrialreforged.tooltip.tool.uses_left", new Object[]{getRemainingUses(itemStack)}));
   }

   protected static int getRemainingUses(ItemStack itemStack) {
      return itemStack.getMaxDamage() - itemStack.getDamageValue() + 1;
   }
}
