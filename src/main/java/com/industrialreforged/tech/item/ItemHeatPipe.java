package com.industrialreforged.tech.item;

import com.industrialreforged.tech.block.heat.HeatPipeBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Тепловая труба: в тултипе — пропускная способность, буфер и рассеивание. */
public class ItemHeatPipe extends BlockItem {
   public ItemHeatPipe(HeatPipeBlock block) {
      super(block, new Properties());
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      if (this.getBlock() instanceof HeatPipeBlock pipe) {
         tooltip.add(Component.translatable("tooltip.industrialreforged.heat_pipe.rate", HeatPipeBlock.rate(pipe.tier)).withStyle(ChatFormatting.GRAY));
         tooltip.add(Component.translatable("tooltip.industrialreforged.heat_pipe.capacity", HeatPipeBlock.capacity(pipe.tier)).withStyle(ChatFormatting.GRAY));
         tooltip.add(Component.translatable("tooltip.industrialreforged.heat_pipe.loss", HeatPipeBlock.ambientLoss(pipe.tier)).withStyle(ChatFormatting.GRAY));
         tooltip.add(Component.translatable("tooltip.industrialreforged.heat_pipe.hint").withStyle(ChatFormatting.DARK_GRAY));
      }
   }
}
