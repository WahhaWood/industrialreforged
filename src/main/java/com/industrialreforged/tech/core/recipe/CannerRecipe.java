package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.tech.core.recipe.basic.BasicRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CannerRecipe extends BasicRecipe {
   @Override
   public boolean assemble(RecipeInput machine, int indexSlot) {
      return false;
   }

   @Override
   public NonNullList<ItemStack> getResultItems() {
      return NonNullList.create();
   }

   public RecipeSerializer<?> getSerializer() {
      return null;
   }

   public RecipeType<?> getType() {
      return null;
   }
}
