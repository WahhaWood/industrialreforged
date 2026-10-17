package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorVentSpread extends AbstractReactorComponent {
   public final int sideVent;

   public ItemReactorVentSpread(Properties properties, int n) {
      super(properties);
      this.sideVent = n;
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
      if (heatRun) {
         this.cool(reactor, x - 1, y);
         this.cool(reactor, x + 1, y);
         this.cool(reactor, x, y - 1);
         this.cool(reactor, x, y + 1);
      }
   }

   private void cool(IReactor iReactor, int n, int n2) {
      ItemStack itemStack = iReactor.getItemAt(n, n2);
      IReactorComponent iReactorComponent;
      if (itemStack.getItem() instanceof IReactorComponent
         && (iReactorComponent = (IReactorComponent)itemStack.getItem()).canStoreHeat(iReactor, itemStack, n, n2)) {
         iReactorComponent.alterHeat(iReactor, itemStack, n, n2, -this.sideVent);
      }
   }
}
