package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorHeatpack extends AbstractReactorComponent {
   protected final int maxPer;
   protected final int heatPer;

   public ItemReactorHeatpack(Properties properties, int n, int n2) {
      super(properties);
      this.maxPer = n;
      this.heatPer = n2;
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
      if (heatRun) {
         int n3 = stack.getCount();
         this.heat(reactor, n3, x + 1, y);
         this.heat(reactor, n3, x - 1, y);
         this.heat(reactor, n3, x, y + 1);
         this.heat(reactor, n3, x, y - 1);
      }
   }

   private void heat(IReactor iReactor, int n, int n2, int n3) {
      int n4 = this.maxPer * n;
      if (iReactor.getHeat() < n4) {
         ItemStack itemStack = iReactor.getItemAt(n2, n3);
         IReactorComponent iReactorComponent;
         if (!itemStack.isEmpty()
            && itemStack.getItem() instanceof IReactorComponent
            && (iReactorComponent = (IReactorComponent)itemStack.getItem()).canStoreHeat(iReactor, itemStack, n2, n3)) {
            int n5 = this.heatPer * n;
            int n6 = iReactorComponent.getCurrentHeat(iReactor, itemStack, n2, n3);
            if (n5 > n4 - n6) {
               n5 = n4 - n6;
            }

            if (n5 > 0) {
               iReactorComponent.alterHeat(iReactor, itemStack, n2, n3, n5);
            }
         }
      }
   }

   @Override
   public float influenceExplosion(IReactor reactor, ItemStack stack) {
      return stack.getCount() / 10.0F;
   }
}
