package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorVent extends ItemReactorHeatStorage {
   public final int selfVent;
   public final int reactorVent;

   public ItemReactorVent(Properties properties, int n, int n2, int n3) {
      super(properties, n);
      this.selfVent = n2;
      this.reactorVent = n3;
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
      if (heatRun) {
         if (this.reactorVent > 0) {
            int n3 = reactor.getHeat();
            int n4 = n3;
            if (n4 > this.reactorVent) {
               n4 = this.reactorVent;
            }

            n3 -= n4;
            if (this.alterHeat(reactor, stack, x, y, n4) > 0) {
               return;
            }

            reactor.setHeat(n3);
         }

         this.alterHeat(reactor, stack, x, y, -this.selfVent);
      }
   }
}
