package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.recipe.OreWashingRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

public class OreWashingPlantCategory extends AbstractRecipeCategory<OreWashingRecipe> {
   public OreWashingPlantCategory(RecipeType<OreWashingRecipe> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.ore_washing_plant"), helper.createDrawableItemLike(IRBlocks.ORE_WASHING_PLANT), 135, 40);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, OreWashingRecipe recipe, IFocusGroup focuses) {
      builder.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(recipe.getItemsJei());
      List<ItemStack> resultItems = recipe.getResultItems();

      for (int i = 0; i < resultItems.size(); i++) {
         builder.addOutputSlot(61 + i * 25, 10).setOutputSlotBackground().addItemStack(resultItems.get(i));
      }

      builder.addInputSlot(1, 21).setStandardSlotBackground().addFluidStack(Fluids.WATER, recipe.getAmount());
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, OreWashingRecipe recipe, IFocusGroup focuses) {
      builder.addAnimatedRecipeArrow(200).setPosition(26, 11);
   }
}
