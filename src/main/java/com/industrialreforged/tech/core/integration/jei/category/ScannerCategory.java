package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.recipe.ScannerRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.util.Util;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;

public class ScannerCategory extends AbstractRecipeCategory<ScannerRecipe> {
   public ScannerCategory(RecipeType<ScannerRecipe> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.uu_scanner"), helper.createDrawableItemLike(IRBlocks.UU_SCANNER), 88, 32);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, ScannerRecipe wrapper, IFocusGroup focuses) {
      builder.addOutputSlot(36, 1).setStandardSlotBackground().addItemStack(wrapper.getItem());
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, ScannerRecipe wrapper, IFocusGroup focuses) {
      ((ITextWidget)builder.addText(Component.literal(Util.toSiStringUU(wrapper.getCost()) + " UU"), this.getWidth() - 20, 10)
            .setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM))
         .setTextAlignment(HorizontalAlignment.CENTER)
         .setTextAlignment(VerticalAlignment.BOTTOM)
         .setColor(-8355712);
   }
}
