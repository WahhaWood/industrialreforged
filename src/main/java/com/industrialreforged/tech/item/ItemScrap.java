package com.industrialreforged.tech.item;

import com.industrialreforged.tech.blockentity.machine.MatterGeneratorBlockEntity;
import com.industrialreforged.utils.NumberUtils;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Scrap and scrap box: amplifier for the matter generator, the tooltip shows how much it takes off. */
public class ItemScrap extends Item {
   public ItemScrap(Properties properties) {
      super(properties);
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(
         Component.translatable(
               "tooltip.industrialreforged.scrap.discount",
               NumberUtils.formatToSi(MatterGeneratorBlockEntity.getAmplifierValue(stack)),
               NumberUtils.formatToSi(MatterGeneratorBlockEntity.MAX_DISCOUNT_PER_MB)
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
