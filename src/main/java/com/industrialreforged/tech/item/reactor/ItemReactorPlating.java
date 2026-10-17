package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorPlating extends AbstractReactorComponent {
   private final int maxHeatAdd;
   private final float effectModifier;

   public ItemReactorPlating(Properties properties, int n, float f) {
      super(properties);
      this.maxHeatAdd = n;
      this.effectModifier = f;
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
      if (heatRun) {
         reactor.setMaxHeat(reactor.getMaxHeat() + this.maxHeatAdd);
         reactor.setHeatEffectModifier(reactor.getHeatEffectModifier() * this.effectModifier);
      }
   }

   @Override
   public float influenceExplosion(IReactor reactor, ItemStack stack) {
      return this.effectModifier >= 1.0F ? 0.0F : this.effectModifier;
   }
}
