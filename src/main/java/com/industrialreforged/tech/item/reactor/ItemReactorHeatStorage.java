package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class ItemReactorHeatStorage extends AbstractDamageableReactorComponent {
   public ItemReactorHeatStorage(Properties properties, int durability) {
      super(properties, durability);
   }

   @Override
   public boolean canStoreHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return true;
   }

   @Override
   public int getMaxHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return this.getMaxDamage(stack);
   }

   @Override
   public int getCurrentHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return this.getDamage(stack);
   }

   @Override
   public int alterHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
      int currentHeat = this.getCurrentHeat(reactor, stack, x, y);
      int maxHeat = this.getMaxHeat(reactor, stack, x, y);
      if ((currentHeat = currentHeat + heat) > maxHeat) {
         reactor.setItemAt(x, y, ItemStack.EMPTY);
         heat = maxHeat - currentHeat + 1;
      } else {
         if (currentHeat < 0) {
            heat = currentHeat;
            currentHeat = 0;
         } else {
            heat = 0;
         }

         this.setDamage(stack, currentHeat);
      }

      return heat;
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (this.getDamage(stack) > 0) {
         tooltipComponents.add(Component.translatable("industrialreforged.tooltip.heat.warning.0"));
         tooltipComponents.add(Component.translatable("industrialreforged.tooltip.heat.warning.1"));
      }
   }
}
