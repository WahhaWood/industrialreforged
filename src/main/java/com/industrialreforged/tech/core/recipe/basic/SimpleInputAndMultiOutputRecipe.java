package com.industrialreforged.tech.core.recipe.basic;

import com.industrialreforged.recipe.CountIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleInputAndMultiOutputRecipe extends SimpleInputRecipe {
   protected final NonNullList<ItemStack> result;

   public SimpleInputAndMultiOutputRecipe(CountIngredient ingredient, NonNullList<ItemStack> result) {
      super(ingredient);
      this.result = result;
   }

   @Override
   public NonNullList<ItemStack> getResultItems() {
      return this.result;
   }
}
