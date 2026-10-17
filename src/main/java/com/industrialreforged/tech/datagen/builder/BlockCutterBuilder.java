package com.industrialreforged.tech.datagen.builder;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.recipe.BlockCutterRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class BlockCutterBuilder extends com.industrialreforged.datagen.builder.AbstractRecipeBuilder<BlockCutterBuilder> {
   private final CountIngredient ingredient;
   private final ItemStack result;
   private final int hardness;

   private BlockCutterBuilder(CountIngredient ingredient, ItemStack result, int hardness) {
      this.ingredient = ingredient;
      this.result = result;
      this.hardness = hardness;
   }

   protected Recipe<?> build() {
      if (this.result.isEmpty()) {
         throw new IllegalArgumentException("result is empty");
      } else {
         return new BlockCutterRecipe(this.ingredient, this.result, this.hardness);
      }
   }

   public static BlockCutterBuilder cutter(ItemLike input, int inputCount, ItemLike result, int resultCount, int hardness) {
      return cutter(Ingredient.of(new ItemLike[]{input}), inputCount, result, resultCount, hardness);
   }

   public static BlockCutterBuilder cutter(TagKey<Item> tagKey, int count, ItemLike result, int resultCount, int hardness) {
      return cutter(Ingredient.of(tagKey), count, result, resultCount, hardness);
   }

   public static BlockCutterBuilder cutter(Ingredient ingredient, int count, ItemLike result, int resultCount, int hardness) {
      return new BlockCutterBuilder(new CountIngredient(ingredient, count), new ItemStack(result, resultCount), hardness);
   }
}
