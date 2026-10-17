package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorCondensator extends AbstractDamageableReactorComponent {
   public ItemReactorCondensator(Properties properties, int durability) {
      super(properties, durability);
   }

   @Override
   public boolean canStoreHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return this.getCurrentHeat(stack) < this.getMaxHeat(reactor, stack, x, y);
   }

   @Override
   public int getMaxHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return this.getMaxDamage(stack);
   }

   private int getCurrentHeat(ItemStack itemStack) {
      return this.getDamage(itemStack);
   }

   @Override
   public int alterHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
      if (heat < 0) {
         return heat;
      }

      int n4 = this.getCurrentHeat(stack);
      int n5 = Math.min(heat, this.getMaxHeat(reactor, stack, x, y) - n4);
      this.setDamage(stack, n4 + n5);
      return heat - n5;
   }
}
