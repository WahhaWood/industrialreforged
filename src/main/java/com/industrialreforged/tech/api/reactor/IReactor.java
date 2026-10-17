package com.industrialreforged.tech.api.reactor;

import com.industrialreforged.tech.api.info.ILocatable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IReactor extends ILocatable {
   int getHeat();

   void setHeat(int var1);

   void addHeat(int var1);

   int getMaxHeat();

   void setMaxHeat(int var1);

   float getHeatEffectModifier();

   void setHeatEffectModifier(float var1);

   float getReactorEnergyOutput();

   void addOutput(float var1);

   @NotNull
   ItemStack getItemAt(int var1, int var2);

   void setItemAt(int var1, int var2, @NotNull ItemStack var3);

   int getTickRate();

   boolean produceEnergy();
}
