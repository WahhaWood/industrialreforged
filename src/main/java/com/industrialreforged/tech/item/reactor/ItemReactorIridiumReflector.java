package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorIridiumReflector extends AbstractReactorComponent {
   public ItemReactorIridiumReflector(Properties properties) {
      super(properties);
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (!heatRun) {
         IReactorComponent iReactorComponent = (IReactorComponent)pulsingStack.getItem();
         iReactorComponent.acceptUraniumPulse(reactor, pulsingStack, stack, pulseX, pulseY, youX, youY, heatRun);
      }

      return true;
   }

   @Override
   public float influenceExplosion(IReactor reactor, ItemStack stack) {
      return -1.0F;
   }
}
