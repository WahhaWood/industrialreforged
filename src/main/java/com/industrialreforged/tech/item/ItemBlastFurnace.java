package com.industrialreforged.tech.item;

import com.industrialreforged.tech.blockentity.machine.BlastFurnaceBlockEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * Доменная печь: в тултипе — откуда она берёт тепло и чего это стоит.
 * <p>
 * Без этих строк печь выглядит «сломанной»: она молча стоит холодной, потому что игрок не знает,
 * что тепло берётся только с той грани, куда печь повёрнута, и что источник должен быть повёрнут
 * навстречу. На разборе этого ушла час работы.
 */
public class ItemBlastFurnace extends BlockItem {
   public ItemBlastFurnace(net.minecraft.world.level.block.Block block) {
      super(block, new Properties());
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(Component.translatable("tooltip.industrialreforged.blast_furnace.heat", BlastFurnaceBlockEntity.maxHeat)
         .withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable("tooltip.industrialreforged.blast_furnace.facing").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable("tooltip.industrialreforged.blast_furnace.cells").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable("tooltip.industrialreforged.blast_furnace.hint").withStyle(ChatFormatting.DARK_GRAY));
   }
}