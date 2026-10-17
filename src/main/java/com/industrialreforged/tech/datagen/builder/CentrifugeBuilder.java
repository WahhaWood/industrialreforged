package com.industrialreforged.tech.datagen.builder;

import com.industrialreforged.recipe.CountIngredient;
import com.industrialreforged.tech.core.recipe.CentrifugeRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class CentrifugeBuilder extends com.industrialreforged.datagen.builder.AbstractRecipeBuilder<CentrifugeBuilder> {
   private final CountIngredient ingredient;
   private final NonNullList<ItemStack> result;
   private final short minHeat;

   private CentrifugeBuilder(CountIngredient ingredient, int minHeat) {
      this.ingredient = ingredient;
      this.result = NonNullList.create();
      this.minHeat = (short)minHeat;
   }

   public CentrifugeBuilder result(ItemLike like) {
      return this.result(like, 1);
   }

   public CentrifugeBuilder result(ItemLike like, int count) {
      return this.result(new ItemStack(like, count));
   }

   public CentrifugeBuilder result(ItemStack stack) {
      this.result.add(stack);
      return this;
   }

   protected Recipe<?> build() {
      if (this.result.isEmpty()) {
         throw new IllegalArgumentException("result is empty");
      } else {
         return new CentrifugeRecipe(this.ingredient, this.result, this.minHeat);
      }
   }

   public static CentrifugeBuilder centrifuge(ItemLike like, int count, int minHeat) {
      return centrifuge(Ingredient.of(new ItemLike[]{like}), count, minHeat);
   }

   public static CentrifugeBuilder centrifuge(TagKey<Item> tagKey, int count, int minHeat) {
      return centrifuge(Ingredient.of(tagKey), count, minHeat);
   }

   public static CentrifugeBuilder centrifuge(Ingredient ingredient, int count, int minHeat) {
      return new CentrifugeBuilder(new CountIngredient(ingredient, count), minHeat);
   }
}
