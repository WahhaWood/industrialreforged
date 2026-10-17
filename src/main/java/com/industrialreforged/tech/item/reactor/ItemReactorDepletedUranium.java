package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.core.ref.IRItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;

public class ItemReactorDepletedUranium extends AbstractDamageableReactorComponent {
   public ItemReactorDepletedUranium(Properties properties) {
      super(properties, 10000);
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (heatRun) {
         int n5 = this.getDamage(stack) + 1 + reactor.getHeat() / 3000;
         if (n5 >= this.getMaxDamage(stack)) {
            reactor.setItemAt(youX, youY, new ItemStack((ItemLike)IRItems.RE_ENRICHED_URANIUM.get()));
         } else {
            this.setDamage(stack, n5);
         }
      }

      return true;
   }
}
