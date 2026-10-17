package com.industrialreforged.tech.api.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface IPatternStorage {
   boolean addPattern(ItemStack var1);

   NonNullList<ItemStack> getPatterns();
}
