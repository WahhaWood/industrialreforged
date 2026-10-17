package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ItemLike;

/**
 * Тритиевый стержень: не крафтится, а нарабатывается из литиевого стержня прямо
 * в реакторе (литий ловит нейтроны). Даёт больше энергии за импульс и греется
 * слабее урана, но и выгорает не мгновенно. Отработанный — сырьё для РИТЭГ-таблетки.
 */
public class ItemReactorTritium extends ItemReactorUranium {
   public ItemReactorTritium() {
      super(new Properties(), 1, 30000);
   }

   @Override
   protected ItemStack getDepletedStack(ItemStack itemStack, IReactor iReactor) {
      return new ItemStack((ItemLike)IRItems.DEPLETED_TRITIUM_FUEL_ROD.get());
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (!heatRun) {
         reactor.addOutput(3.0F);
      }

      return true;
   }

   @Override
   protected int getFinalHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
      return (int)(heat * 0.6F);
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(Component.translatable("tooltip.industrialreforged.fuel_rod.tritium").withStyle(ChatFormatting.YELLOW));
   }
}
