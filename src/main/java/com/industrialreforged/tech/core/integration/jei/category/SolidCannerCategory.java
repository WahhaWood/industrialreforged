package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.Arrays;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

public class SolidCannerCategory extends AbstractRecipeCategory<SolidCannerRecipe> {
   public SolidCannerCategory(RecipeType<SolidCannerRecipe> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.solid_canner"), helper.createDrawableItemLike(IRBlocks.SOLID_CANNER), 97, 20);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, SolidCannerRecipe recipe, IFocusGroup focuses) {
      this.addInput(builder, 1, recipe.input, recipe.inputCount);
      this.addInput(builder, 34, recipe.center, recipe.centerCount);
      builder.addOutputSlot(80, 1).setStandardSlotBackground().addItemStack(recipe.result);
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, SolidCannerRecipe recipe, IFocusGroup focuses) {
      builder.addAnimatedRecipeArrow(200).setPosition(54, 1);
      builder.addRecipePlusSign().setPosition(19, 2);
   }

   private void addInput(IRecipeLayoutBuilder builder, int x, Ingredient ingredient, int count) {
      IRecipeSlotBuilder slot = builder.addInputSlot(x, 1).setStandardSlotBackground();
      if (count > 1) {
         slot.addItemStacks(Arrays.stream(ingredient.getItems()).map(stack -> stack.copyWithCount(count)).toList());
      } else {
         slot.addIngredients(ingredient);
      }
   }
}
