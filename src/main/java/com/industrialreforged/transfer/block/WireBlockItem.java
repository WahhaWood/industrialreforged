package com.industrialreforged.transfer.block;

import com.industrialreforged.utils.NumberUtils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class WireBlockItem extends BlockItem {
   public WireBlockItem(WireBlock block, Rarity rarity) {
      super(block, new Properties().rarity(rarity));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.wire.0"));
      WireBlock block = (WireBlock)this.getBlock();
      if (block.transfer == -1) {
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.transfer.infinity"));
      } else {
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.transfer", new Object[]{"§e" + NumberUtils.formatInt(block.transfer)}));
      }

      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.wire.1"));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.transfer.connection"));
   }
}
