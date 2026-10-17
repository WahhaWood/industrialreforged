package com.industrialreforged.tech.item.armor.jetpack;

import com.industrialreforged.tech.api.item.IArmorTick;
import net.minecraft.world.item.ItemStack;

public interface IJetpack extends IArmorTick {
   boolean drainEnergy(ItemStack var1, int var2);

   float getPower(ItemStack var1);

   float getDropPercentage(ItemStack var1);

   double getChargeLevel(ItemStack var1);

   float getHoverMultiplier(ItemStack var1, boolean var2);

   float getWorldHeightDivisor(ItemStack var1);
}
