package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.core.ref.IRItems;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemReactorLithiumCell extends AbstractDamageableReactorComponent {
   public ItemReactorLithiumCell(Properties properties) {
      super(properties, 10000);
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (heatRun) {
         // литий ловит нейтроны и по мере «выгорания» превращается в тритиевый стержень
         int damage = this.getDamage(stack) + reactor.getHeat() / 3000;
         if (damage >= this.getMaxDamage(stack)) {
            reactor.setItemAt(youX, youY, new ItemStack((ItemLike)IRItems.TRITIUM_FUEL_ROD.get()));
         } else {
            this.setDamage(stack, damage);
         }
      }

      return true;
   }
}
