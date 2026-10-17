package com.industrialreforged.datagen.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ItemExistsCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class GEShapelessRecipeBuilder extends ShapelessRecipeBuilder {
   private final List<ICondition> conditions = new ArrayList<>();

   public GEShapelessRecipeBuilder(RecipeCategory category, ItemLike result, int count) {
      super(category, result, count);
   }

   public GEShapelessRecipeBuilder(RecipeCategory category, ItemStack result) {
      super(category, result);
   }

   public GEShapelessRecipeBuilder modId(String... modIds) {
      for (String modId : modIds) {
         this.condition(new ModLoadedCondition(modId));
      }

      return this;
   }

   public GEShapelessRecipeBuilder itemExists(ItemLike... items) {
      for (ItemLike like : items) {
         ResourceLocation location = BuiltInRegistries.ITEM.getKey(like.asItem());
         if (location != null && !location.toString().equals("minecraft:air")) {
            this.condition(new ItemExistsCondition(location));
         }
      }

      return this;
   }

   public GEShapelessRecipeBuilder condition(ICondition condition) {
      this.conditions.add(condition);
      return this;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation id) {
      recipeOutput.accept(
         id,
         new ShapelessRecipe(Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), this.resultStack, this.ingredients),
         null,
         this.conditions.toArray(ICondition[]::new)
      );
   }

   public static GEShapelessRecipeBuilder shapeless(ItemLike result) {
      return shapeless(RecipeCategory.MISC, result, 1);
   }

   public static GEShapelessRecipeBuilder shapeless(ItemLike result, int count) {
      return shapeless(RecipeCategory.MISC, result, count);
   }

   public static GEShapelessRecipeBuilder shapeless(ItemStack result) {
      return shapeless(RecipeCategory.MISC, result);
   }

   public static GEShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result) {
      return shapeless(category, result, 1);
   }

   public static GEShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count) {
      return new GEShapelessRecipeBuilder(category, result, count);
   }

   public static GEShapelessRecipeBuilder shapeless(RecipeCategory category, ItemStack result) {
      return new GEShapelessRecipeBuilder(category, result);
   }
}
