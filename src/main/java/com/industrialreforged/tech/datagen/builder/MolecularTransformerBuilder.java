package com.industrialreforged.tech.datagen.builder;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class MolecularTransformerBuilder extends com.industrialreforged.datagen.builder.AbstractRecipeBuilder<MolecularTransformerBuilder> {
   private final CountIngredient ingredient;
   private final ItemStack result;
   private final long energy;

   private MolecularTransformerBuilder(CountIngredient ingredient, ItemStack result, long energy) {
      this.ingredient = ingredient;
      this.result = result;
      this.energy = energy;
   }

   protected Recipe<?> build() {
      if (this.result.isEmpty()) {
         throw new IllegalArgumentException("result is empty");
      } else {
         return new MolecularTransformerRecipe(this.ingredient, this.result, this.energy);
      }
   }

   public static MolecularTransformerBuilder transform(ItemLike input, ItemLike result, long energy) {
      return transform(Ingredient.of(new ItemLike[]{input}), 1, result, 1, energy);
   }

   public static MolecularTransformerBuilder transform(ItemLike input, int inputCount, ItemLike result, int resultCount, long energy) {
      return transform(Ingredient.of(new ItemLike[]{input}), inputCount, result, resultCount, energy);
   }

   public static MolecularTransformerBuilder transform(TagKey<Item> tagKey, ItemLike result, long energy) {
      return transform(Ingredient.of(tagKey), 1, result, 1, energy);
   }

   public static MolecularTransformerBuilder transform(TagKey<Item> tagKey, int count, ItemLike result, int resultCount, long energy) {
      return transform(Ingredient.of(tagKey), count, result, resultCount, energy);
   }

   public static MolecularTransformerBuilder transform(Ingredient ingredient, int count, ItemLike result, int resultCount, long energy) {
      return new MolecularTransformerBuilder(new CountIngredient(ingredient, count), new ItemStack(result, resultCount), energy);
   }
}
