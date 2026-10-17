package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public abstract class AbstractReactorComponent extends Item implements IReactorComponent {
   protected AbstractReactorComponent(Properties properties) {
      super(properties);
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      return false;
   }

   @Override
   public boolean canStoreHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return false;
   }

   @Override
   public int getMaxHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return 0;
   }

   @Override
   public int getCurrentHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return 0;
   }

   @Override
   public int alterHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
      return heat;
   }

   @Override
   public float influenceExplosion(IReactor reactor, ItemStack stack) {
      return 0.0F;
   }

   @Override
   public boolean canBePlacedIn(ItemStack itemStack, IReactor iReactor) {
      return true;
   }
}
