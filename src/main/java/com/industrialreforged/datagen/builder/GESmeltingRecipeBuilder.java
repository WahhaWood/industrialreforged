package com.industrialreforged.datagen.builder;

import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.AbstractCookingRecipe.Factory;
import net.minecraft.world.level.ItemLike;

public class GESmeltingRecipeBuilder extends AbstractRecipeBuilder<GESmeltingRecipeBuilder> {
   private final CookingBookCategory bookCategory;
   private final ItemStack stackResult;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Factory<?> factory;

   private GESmeltingRecipeBuilder(
      CookingBookCategory bookCategory, ItemLike result, Ingredient ingredient, float experience, int cookingTime, Factory<?> factory
   ) {
      this(bookCategory, new ItemStack(result), ingredient, experience, cookingTime, factory);
   }

   private GESmeltingRecipeBuilder(
      CookingBookCategory bookCategory, ItemStack result, Ingredient ingredient, float experience, int cookingTime, Factory<?> factory
   ) {
      super(result.getItem());
      this.bookCategory = bookCategory;
      this.stackResult = result;
      this.ingredient = ingredient;
      this.experience = experience;
      this.cookingTime = cookingTime;
      this.factory = factory;
   }

   public static <T extends AbstractCookingRecipe> GESmeltingRecipeBuilder generic(
      ItemLike ingredient, ItemLike result, float experience, int cookingTime, RecipeSerializer<T> cookingSerializer, Factory<T> factory
   ) {
      return new GESmeltingRecipeBuilder(
         determineRecipeCategory(cookingSerializer, result), result, Ingredient.of(new ItemLike[]{ingredient}), experience, cookingTime, factory
      );
   }

   public static GESmeltingRecipeBuilder campfireCooking(ItemLike ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(
         CookingBookCategory.FOOD, result, Ingredient.of(new ItemLike[]{ingredient}), experience, cookingTime, CampfireCookingRecipe::new
      );
   }

   public static GESmeltingRecipeBuilder blasting(ItemLike ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(
         determineBlastingRecipeCategory(result), result, Ingredient.of(new ItemLike[]{ingredient}), experience, cookingTime, BlastingRecipe::new
      );
   }

   public static GESmeltingRecipeBuilder blasting(TagKey<Item> tag, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(determineBlastingRecipeCategory(result), result, Ingredient.of(tag), experience, cookingTime, BlastingRecipe::new);
   }

   public static GESmeltingRecipeBuilder smelting(ItemLike ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(
         determineSmeltingRecipeCategory(result), result, Ingredient.of(new ItemLike[]{ingredient}), experience, cookingTime, SmeltingRecipe::new
      );
   }

   public static GESmeltingRecipeBuilder smelting(TagKey<Item> tag, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(determineSmeltingRecipeCategory(result), result, Ingredient.of(tag), experience, cookingTime, SmeltingRecipe::new);
   }

   public static GESmeltingRecipeBuilder smoking(ItemLike ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(
         CookingBookCategory.FOOD, result, Ingredient.of(new ItemLike[]{ingredient}), experience, cookingTime, SmokingRecipe::new
      );
   }

   public static <T extends AbstractCookingRecipe> GESmeltingRecipeBuilder generic(
      Ingredient ingredient, ItemLike result, float experience, int cookingTime, RecipeSerializer<T> cookingSerializer, Factory<T> factory
   ) {
      return new GESmeltingRecipeBuilder(determineRecipeCategory(cookingSerializer, result), result, ingredient, experience, cookingTime, factory);
   }

   public static GESmeltingRecipeBuilder campfireCooking(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(CookingBookCategory.FOOD, result, ingredient, experience, cookingTime, CampfireCookingRecipe::new);
   }

   public static GESmeltingRecipeBuilder blasting(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(determineBlastingRecipeCategory(result), result, ingredient, experience, cookingTime, BlastingRecipe::new);
   }

   public static GESmeltingRecipeBuilder smelting(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(determineSmeltingRecipeCategory(result), result, ingredient, experience, cookingTime, SmeltingRecipe::new);
   }

   public static GESmeltingRecipeBuilder smoking(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(CookingBookCategory.FOOD, result, ingredient, experience, cookingTime, SmokingRecipe::new);
   }

   public static <T extends AbstractCookingRecipe> GESmeltingRecipeBuilder generic(
      Ingredient ingredient, ItemStack result, float experience, int cookingTime, Factory<T> factory
   ) {
      return new GESmeltingRecipeBuilder(determineSmeltingRecipeCategory(result.getItem()), result, ingredient, experience, cookingTime, factory);
   }

   public static GESmeltingRecipeBuilder campfireCooking(Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(CookingBookCategory.FOOD, result, ingredient, experience, cookingTime, CampfireCookingRecipe::new);
   }

   public static GESmeltingRecipeBuilder blasting(Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(determineBlastingRecipeCategory(result.getItem()), result, ingredient, experience, cookingTime, BlastingRecipe::new);
   }

   public static GESmeltingRecipeBuilder smelting(Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(determineSmeltingRecipeCategory(result.getItem()), result, ingredient, experience, cookingTime, SmeltingRecipe::new);
   }

   public static GESmeltingRecipeBuilder smoking(Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
      return new GESmeltingRecipeBuilder(CookingBookCategory.FOOD, result, ingredient, experience, cookingTime, SmokingRecipe::new);
   }

   @Override
   protected Recipe<?> build() {
      return this.factory
         .create(Objects.requireNonNullElse(this.group, ""), this.bookCategory, this.ingredient, this.stackResult, this.experience, this.cookingTime);
   }

   private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike result) {
      if (result.asItem().components().has(DataComponents.FOOD)) {
         return CookingBookCategory.FOOD;
      } else {
         return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
      }
   }

   private static CookingBookCategory determineBlastingRecipeCategory(ItemLike result) {
      return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
   }

   private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ItemLike result) {
      if (serializer == RecipeSerializer.SMELTING_RECIPE) {
         return determineSmeltingRecipeCategory(result);
      } else if (serializer == RecipeSerializer.BLASTING_RECIPE) {
         return determineBlastingRecipeCategory(result);
      } else if (serializer != RecipeSerializer.SMOKING_RECIPE && serializer != RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
         throw new IllegalStateException("Unknown cooking recipe type");
      } else {
         return CookingBookCategory.FOOD;
      }
   }
}
