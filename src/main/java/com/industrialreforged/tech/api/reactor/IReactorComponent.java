package com.industrialreforged.tech.api.reactor;

import net.minecraft.world.item.ItemStack;

public interface IReactorComponent extends IBaseReactorComponent {
   void processChamber(IReactor var1, ItemStack var2, int var3, int var4, boolean var5);

   boolean acceptUraniumPulse(IReactor var1, ItemStack var2, ItemStack var3, int var4, int var5, int var6, int var7, boolean var8);

   boolean canStoreHeat(IReactor var1, ItemStack var2, int var3, int var4);

   int getMaxHeat(IReactor var1, ItemStack var2, int var3, int var4);

   int getCurrentHeat(IReactor var1, ItemStack var2, int var3, int var4);

   int alterHeat(IReactor var1, ItemStack var2, int var3, int var4, int var5);

   float influenceExplosion(IReactor var1, ItemStack var2);
}
