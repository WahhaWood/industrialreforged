package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.recipe.CentrifugeRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.List;
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
import net.minecraft.world.item.ItemStack;

public class CentrifugeCategory extends AbstractRecipeCategory<CentrifugeRecipe> {
   public CentrifugeCategory(RecipeType<CentrifugeRecipe> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.centrifuge"), helper.createDrawableItemLike(IRBlocks.CENTRIFUGE), 135, 36);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses) {
      builder.addInputSlot(1, 6).setStandardSlotBackground().addItemStacks(recipe.getItemsJei());
      List<ItemStack> resultItems = recipe.getResultItems();

      for (int i = 0; i < resultItems.size(); i++) {
         builder.addOutputSlot(61 + i * 25, 6).setOutputSlotBackground().addItemStack(resultItems.get(i));
      }
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses) {
      builder.addAnimatedRecipeArrow(200).setPosition(26, 6);
      Component timeString = Component.translatable("text.industrialreforged.heat", new Object[]{recipe.getMinHeat()});
      ((ITextWidget)builder.addText(timeString, this.getWidth() - 20, 10)
            .setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM))
         .setTextAlignment(HorizontalAlignment.RIGHT)
         .setTextAlignment(VerticalAlignment.BOTTOM)
         .setColor(-8355712);
   }
}
