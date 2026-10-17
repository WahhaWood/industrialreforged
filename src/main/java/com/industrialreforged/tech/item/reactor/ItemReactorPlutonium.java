package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ItemLike;

/**
 * Плутониевый стержень: заметно мощнее MOX (2 FE за импульс), но греется сильнее
 * и быстрее выгорает. Отработанный стержень перерабатывается в центрифуге обратно
 * в плутоний.
 */
public class ItemReactorPlutonium extends ItemReactorUranium {
   public ItemReactorPlutonium(Properties properties, int cells) {
      super(properties, cells, 12000);
   }

   @Override
   protected ItemStack getDepletedStack(ItemStack itemStack, IReactor iReactor) {
      return new ItemStack(switch (this.numberOfCells) {
         case 1 -> (Item)IRItems.DEPLETED_PLUTONIUM_FUEL_ROD.get();
         case 2 -> (Item)IRItems.DEPLETED_DUAL_PLUTONIUM_FUEL_ROD.get();
         default -> throw new RuntimeException("invalid cell count: " + this.numberOfCells);
         case 4 -> (Item)IRItems.DEPLETED_QUAD_PLUTONIUM_FUEL_ROD.get();
      });
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (!heatRun) {
         reactor.addOutput(2.0F);
      }

      return true;
   }

   @Override
   protected int getFinalHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
      return (int)(heat * 1.25F);
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(Component.translatable("tooltip.industrialreforged.fuel_rod.plutonium").withStyle(ChatFormatting.RED));
   }
}
