package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorReflector extends AbstractDamageableReactorComponent {
   public ItemReactorReflector(Properties properties, int durability) {
      super(properties, durability);
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (!heatRun) {
         IReactorComponent iReactorComponent = (IReactorComponent)pulsingStack.getItem();
         iReactorComponent.acceptUraniumPulse(reactor, pulsingStack, stack, pulseX, pulseY, youX, youY, heatRun);
      } else {
         stack.hurtAndBreak(1, (ServerLevel)reactor.getWorldObj(), null, item -> reactor.setItemAt(youX, youY, ItemStack.EMPTY));
      }

      return true;
   }

   @Override
   public float influenceExplosion(IReactor reactor, ItemStack stack) {
      return -1.0F;
   }
}
