package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.utils.NumberUtils;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;

public class MolecularTransformerCategory extends AbstractRecipeCategory<MolecularTransformerRecipe> {
   public MolecularTransformerCategory(RecipeType<MolecularTransformerRecipe> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.molecular_transformer"), helper.createDrawableItemLike(IRBlocks.MOLECULAR_TRANSFORMER), 67, 32);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, MolecularTransformerRecipe recipe, IFocusGroup focuses) {
      builder.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(recipe.getItemsJei());
      builder.addOutputSlot(50, 1).setStandardSlotBackground().addItemStack(recipe.getResultItem());
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, MolecularTransformerRecipe recipe, IFocusGroup focuses) {
      builder.addAnimatedRecipeArrow(200).setPosition(23, 1);
      ((ITextWidget)builder.addText(Component.translatable("text.industrialreforged.fe", new Object[]{NumberUtils.formatToSi(recipe.energy)}), this.getWidth() - 16, 10)
            .setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM))
         .setTextAlignment(HorizontalAlignment.CENTER)
         .setTextAlignment(VerticalAlignment.BOTTOM)
         .setColor(-8355712);
   }

   public void getTooltip(ITooltipBuilder tooltip, MolecularTransformerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      int width = this.getWidth() - 16;
      int height = this.getHeight();
      if (mouseX >= 8.0 && mouseX <= 8 + width && mouseY >= height - 10 && mouseY <= height) {
         tooltip.add(Component.translatable("text.industrialreforged.fe", new Object[]{NumberUtils.formatLong(recipe.energy)}));
      }
   }
}
