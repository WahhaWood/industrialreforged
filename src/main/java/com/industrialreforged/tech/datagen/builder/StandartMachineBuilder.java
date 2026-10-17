package com.industrialreforged.tech.datagen.builder;

import com.industrialreforged.recipe.CountIngredient;
import java.util.function.BiFunction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class StandartMachineBuilder extends com.industrialreforged.datagen.builder.AbstractRecipeBuilder<StandartMachineBuilder> {
   private final CountIngredient ingredient;
   private final ItemStack result;
   private final BiFunction<CountIngredient, ItemStack, Recipe<?>> factory;

   private StandartMachineBuilder(CountIngredient ingredient, ItemStack result, BiFunction<CountIngredient, ItemStack, Recipe<?>> factory) {
      this.ingredient = ingredient;
      this.result = result;
      this.factory = factory;
   }

   protected Recipe<?> build() {
      if (this.result.isEmpty()) {
         throw new IllegalArgumentException("result is empty");
      } else {
         return this.factory.apply(this.ingredient, this.result);
      }
   }

   public static StandartMachineBuilder general(ItemLike input, ItemLike result, BiFunction<CountIngredient, ItemStack, Recipe<?>> factory) {
      return general(input, 1, result, 1, factory);
   }

   public static StandartMachineBuilder general(
      ItemLike input, int inputCount, ItemLike result, int resultCount, BiFunction<CountIngredient, ItemStack, Recipe<?>> factory
   ) {
      return general(Ingredient.of(new ItemLike[]{input}), inputCount, new ItemStack(result, resultCount), factory);
   }

   public static StandartMachineBuilder general(TagKey<Item> input, ItemLike result, BiFunction<CountIngredient, ItemStack, Recipe<?>> factory) {
      return general(input, 1, result, 1, factory);
   }

   public static StandartMachineBuilder general(
      TagKey<Item> input, int inputCount, ItemLike result, int resultCount, BiFunction<CountIngredient, ItemStack, Recipe<?>> factory
   ) {
      return general(Ingredient.of(input), inputCount, new ItemStack(result, resultCount), factory);
   }

   public static StandartMachineBuilder general(Ingredient input, int inputCount, ItemStack result, BiFunction<CountIngredient, ItemStack, Recipe<?>> factory) {
      return new StandartMachineBuilder(new CountIngredient(input, inputCount), result, factory);
   }
}
