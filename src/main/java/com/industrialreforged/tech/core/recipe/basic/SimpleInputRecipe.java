package com.industrialreforged.tech.core.recipe.basic;

import com.industrialreforged.recipe.CountIngredient;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public abstract class SimpleInputRecipe extends BasicRecipe {
   protected final CountIngredient ingredient;
   protected final NonNullList<Ingredient> ingredients;

   public SimpleInputRecipe(CountIngredient ingredient) {
      this.ingredient = ingredient;
      this.ingredients = NonNullList.of(Ingredient.EMPTY, new Ingredient[]{ingredient.ingredient()});
   }

   public CountIngredient getIngredient() {
      return this.ingredient;
   }

   public List<ItemStack> getItemsJei() {
      return this.ingredient.toList();
   }

   @Override
   public boolean matches(RecipeInput machine, Level level) {
      return this.ingredient.test(machine.getItem(0));
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }
}
