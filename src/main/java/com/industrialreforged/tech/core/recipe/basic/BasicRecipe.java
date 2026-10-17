package com.industrialreforged.tech.core.recipe.basic;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public abstract class BasicRecipe implements Recipe<RecipeInput> {

   @Override
   public boolean isSpecial() {
      return true;
   }

   public boolean matches(RecipeInput machine, Level level) {
      return false;
   }

   public ItemStack assemble(RecipeInput input, Provider registries) {
      return ItemStack.EMPTY;
   }

   public final boolean canCraftInDimensions(int width, int height) {
      return false;
   }

   public ItemStack getResultItem(Provider registries) {
      return ItemStack.EMPTY;
   }

   public abstract boolean assemble(RecipeInput var1, int var2);

   public abstract NonNullList<ItemStack> getResultItems();
}
