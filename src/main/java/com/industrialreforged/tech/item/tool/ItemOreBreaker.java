package com.industrialreforged.tech.item.tool;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** Рудокрушитель как предмет: в описании — что он делает и зачем слот книги. */
public class ItemOreBreaker extends BlockItem {
   public ItemOreBreaker(Block block, Properties properties) {
      super(block, properties);
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.translatable("text.industrialreforged.ore_breaker.info"));
      tooltip.add(Component.translatable("text.industrialreforged.ore_breaker.book"));
      super.appendHoverText(stack, context, tooltip, flag);
   }
}
