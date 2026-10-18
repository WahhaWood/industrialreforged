package com.industrialreforged.tech.item;

import com.industrialreforged.utils.NumberUtils;
import com.industrialreforged.tech.block.generator.electric.SolarPanelBlock;
import com.industrialreforged.tech.core.IRConfig;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;

public class SolarPanelBlockItem extends BlockItem {

   public SolarPanelBlockItem(Block block) {
      super(block, new Properties());
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (IRConfig.CONFIG_SPEC.isLoaded() && this.getBlock() instanceof SolarPanelBlock panel) {
         long day = IRConfig.CONFIG.getProduction(panel.getLevel());
         long night = Math.max(day * 6L / 10L, 1L);
         long buffer = Math.max(day * 6400L, 64000L);
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.solar.day", new Object[]{NumberUtils.formatToSi(day)}));
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.solar.night", new Object[]{NumberUtils.formatToSi(night)}));
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.solar.buffer", new Object[]{NumberUtils.formatToSi(buffer)}));
      }
   }
}
