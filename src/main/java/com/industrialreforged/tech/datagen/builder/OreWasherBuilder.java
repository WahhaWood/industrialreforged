package com.industrialreforged.tech.datagen.builder;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.recipe.OreWashingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class OreWasherBuilder extends com.industrialreforged.datagen.builder.AbstractRecipeBuilder<OreWasherBuilder> {
   private final CountIngredient ingredient;
   private final NonNullList<ItemStack> result;
   private final int amountWater;

   private OreWasherBuilder(CountIngredient ingredient, int amountWater) {
      this.ingredient = ingredient;
      this.result = NonNullList.create();
      this.amountWater = amountWater;
   }

   public OreWasherBuilder result(ItemLike like) {
      return this.result(like, 1);
   }

   public OreWasherBuilder result(ItemLike like, int count) {
      return this.result(new ItemStack(like, count));
   }

   public OreWasherBuilder result(ItemStack stack) {
      this.result.add(stack);
      return this;
   }

   protected Recipe<?> build() {
      if (this.result.isEmpty()) {
         throw new IllegalArgumentException("result is empty");
      } else {
         return new OreWashingRecipe(this.ingredient, this.result, this.amountWater);
      }
   }

   public static OreWasherBuilder washer(ItemLike like, int count, int amountWater) {
      return washer(Ingredient.of(new ItemLike[]{like}), count, amountWater);
   }

   public static OreWasherBuilder washer(TagKey<Item> tagKey, int count, int amountWater) {
      return washer(Ingredient.of(tagKey), count, amountWater);
   }

   public static OreWasherBuilder washer(Ingredient ingredient, int count, int amountWater) {
      return new OreWasherBuilder(new CountIngredient(ingredient, count), amountWater);
   }
}
