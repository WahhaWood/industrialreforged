package com.industrialreforged.tech.item.tool;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** Карьер как предмет: в описании — цена за руду и назначение слотов фильтров. */
public class ItemQuantumQuarry extends BlockItem {
   public ItemQuantumQuarry(Block block, Properties properties) {
      super(block, properties);
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.translatable("text.industrialreforged.quantum_quarry.price"));
      tooltip.add(Component.translatable("text.industrialreforged.quantum_quarry.filters"));
      super.appendHoverText(stack, context, tooltip, flag);
   }
}
