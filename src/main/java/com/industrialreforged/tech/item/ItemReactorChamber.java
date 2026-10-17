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

/** Реакторная камера: подсказка про установку вплотную к ядру. */
public class ItemReactorChamber extends BlockItem {
   public ItemReactorChamber() {
      super(IRBlocks.REACTOR_CHAMBER.get(), new Properties().rarity(Rarity.UNCOMMON));
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(Component.translatable("tooltip.industrialreforged.reactor_chamber.hint").withStyle(ChatFormatting.GRAY));
   }
}
