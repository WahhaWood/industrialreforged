package com.industrialreforged.tech.datagen.builder;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.recipe.BlastFurnaceRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class BlastFurnaceBuilder extends com.industrialreforged.datagen.builder.AbstractRecipeBuilder<BlastFurnaceBuilder> {
   private final CountIngredient ingredient;
   private final NonNullList<ItemStack> result;
   private final int cells;
   private final int duration;

   private BlastFurnaceBuilder(CountIngredient ingredient, int cells, int duration) {
      this.ingredient = ingredient;
      this.result = NonNullList.create();
      this.cells = cells;
      this.duration = duration;
   }

   public BlastFurnaceBuilder result(ItemLike like) {
      return this.result(like, 1);
   }

   public BlastFurnaceBuilder result(ItemLike like, int count) {
      return this.result(new ItemStack(like, count));
   }

   public BlastFurnaceBuilder result(ItemStack stack) {
      this.result.add(stack);
      return this;
   }

   protected Recipe<?> build() {
      if (this.result.isEmpty()) {
         throw new IllegalArgumentException("result is empty");
      } else {
         return new BlastFurnaceRecipe(this.ingredient, this.result, this.cells, this.duration);
      }
   }

   public static BlastFurnaceBuilder blast(ItemLike like, int count) {
      return blast(like, count, 6, 6000);
   }

   public static BlastFurnaceBuilder blast(ItemLike like, int count, int cells, int duration) {
      return blast(Ingredient.of(new ItemLike[]{like}), count, cells, duration);
   }

   public static BlastFurnaceBuilder blast(TagKey<Item> tagKey, int count) {
      return blast(tagKey, count, 6, 6000);
   }

   public static BlastFurnaceBuilder blast(TagKey<Item> tagKey, int count, int cells, int duration) {
      return blast(Ingredient.of(tagKey), count, cells, duration);
   }

   public static BlastFurnaceBuilder blast(Ingredient ingredient, int count, int cells, int duration) {
      return new BlastFurnaceBuilder(new CountIngredient(ingredient, count), cells, duration);
   }
}
