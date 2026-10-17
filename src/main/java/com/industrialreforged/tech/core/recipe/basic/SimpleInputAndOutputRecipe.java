package com.industrialreforged.tech.core.recipe.basic;

import com.industrialreforged.recipe.CountIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleInputAndOutputRecipe extends SimpleInputRecipe {
   protected final ItemStack result;
   private final NonNullList<ItemStack> results;

   public SimpleInputAndOutputRecipe(CountIngredient ingredient, ItemStack result) {
      super(ingredient);
      this.result = result;
      this.results = NonNullList.withSize(1, this.result);
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   @Override
   public NonNullList<ItemStack> getResultItems() {
      return this.results;
   }

   @Override
   public ItemStack getResultItem(Provider registries) {
      return this.result;
   }
}
