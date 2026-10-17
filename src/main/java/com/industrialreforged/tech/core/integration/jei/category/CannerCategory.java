package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.integration.jei.wrapper.CannerWrapper;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class CannerCategory extends AbstractRecipeCategory<CannerWrapper> {
   public CannerCategory(RecipeType<CannerWrapper> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.canner"), helper.createDrawableItemLike(IRBlocks.CANNER), 97, 20);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, CannerWrapper recipe, IFocusGroup focuses) {
      this.addInput(builder, 1, recipe.input(), recipe.inputCount(), recipe.inputFluid(), recipe.inputAmount());
      this.addInput(builder, 34, recipe.center(), recipe.centerCount(), Fluids.EMPTY, 0);
      IRecipeSlotBuilder slot = builder.addOutputSlot(80, 1).setStandardSlotBackground();
      if (!recipe.result().isEmpty()) {
         slot.addItemStack(recipe.result());
      }

      if (recipe.resultFluid() != Fluids.EMPTY) {
         slot.addFluidStack(recipe.resultFluid(), recipe.resultAmount());
      }
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, CannerWrapper recipe, IFocusGroup focuses) {
      builder.addAnimatedRecipeArrow(200).setPosition(54, 1);
      builder.addRecipePlusSign().setPosition(19, 2);
   }

   private void addInput(IRecipeLayoutBuilder builder, int x, Ingredient ingredient, int count, Fluid inputFluid, int inputAmount) {
      IRecipeSlotBuilder slot = builder.addInputSlot(x, 1).setStandardSlotBackground();
      if (!ingredient.isEmpty()) {
         if (count > 1) {
            slot.addItemStacks(Arrays.stream(ingredient.getItems()).map(stack -> stack.copyWithCount(count)).toList());
         } else {
            slot.addIngredients(ingredient);
         }
      }

      if (inputFluid != Fluids.EMPTY) {
         slot.addFluidStack(inputFluid, inputAmount);
      }
   }
}
