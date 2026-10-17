package com.industrialreforged.datagen.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ItemExistsCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class GEShapedRecipeBuilder extends ShapedRecipeBuilder {
   private final List<ICondition> conditions = new ArrayList<>();

   public GEShapedRecipeBuilder(RecipeCategory category, ItemLike result, int count) {
      super(category, result, count);
   }

   public GEShapedRecipeBuilder(RecipeCategory category, ItemStack result) {
      super(category, result);
   }

   public GEShapedRecipeBuilder modId(String... modIds) {
      for (String modId : modIds) {
         this.condition(new ModLoadedCondition(modId));
      }

      return this;
   }

   public GEShapedRecipeBuilder itemExists(ItemLike... items) {
      for (ItemLike like : items) {
         ResourceLocation location = BuiltInRegistries.ITEM.getKey(like.asItem());
         if (location != null && !location.toString().equals("minecraft:air")) {
            this.condition(new ItemExistsCondition(location));
         }
      }

      return this;
   }

   public GEShapedRecipeBuilder condition(ICondition condition) {
      this.conditions.add(condition);
      return this;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation id) {
      recipeOutput.accept(
         id,
         new ShapedRecipe(
            Objects.requireNonNullElse(this.group, ""),
            RecipeBuilder.determineBookCategory(this.category),
            ShapedRecipePattern.of(this.key, this.rows),
            this.resultStack,
            this.showNotification
         ),
         null,
         this.conditions.toArray(ICondition[]::new)
      );
   }

   public static GEShapedRecipeBuilder shaped(ItemLike result) {
      return shaped(RecipeCategory.MISC, result, 1);
   }

   public static GEShapedRecipeBuilder shaped(ItemLike result, int count) {
      return shaped(RecipeCategory.MISC, result, count);
   }

   public static GEShapedRecipeBuilder shaped(ItemStack result) {
      return shaped(RecipeCategory.MISC, result);
   }

   public static GEShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
      return shaped(category, result, 1);
   }

   public static GEShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) {
      return new GEShapedRecipeBuilder(category, result, count);
   }

   public static GEShapedRecipeBuilder shaped(RecipeCategory category, ItemStack result) {
      return new GEShapedRecipeBuilder(category, result);
   }
}
