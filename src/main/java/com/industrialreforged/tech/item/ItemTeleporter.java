package com.industrialreforged.tech.item;

import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

/** Телепортер: подсказки про связывание передатчиком частоты и ПКМ-телепорт. */
public class ItemTeleporter extends BlockItem {
   public ItemTeleporter() {
      super(IRBlocks.TELEPORTER.get(), new Properties().rarity(Rarity.RARE));
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(Component.translatable("tooltip.industrialreforged.teleporter.link").withStyle(ChatFormatting.AQUA));
      tooltip.add(Component.translatable("tooltip.industrialreforged.teleporter.hint").withStyle(ChatFormatting.GRAY));
   }
}
