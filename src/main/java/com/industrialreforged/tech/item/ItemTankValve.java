package com.industrialreforged.tech.item;

import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Клапан резервуара-котла: подсказка про режимы и переключение ключом. */
public class ItemTankValve extends BlockItem {
   public ItemTankValve() {
      super(IRBlocks.TANK_VALVE.get(), new Properties());
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(Component.translatable("tooltip.industrialreforged.tank_valve.modes").withStyle(ChatFormatting.AQUA));
      tooltip.add(Component.translatable("tooltip.industrialreforged.tank_valve.wrench").withStyle(ChatFormatting.GRAY));
   }
}
