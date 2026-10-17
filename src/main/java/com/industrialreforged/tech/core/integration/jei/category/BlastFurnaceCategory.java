package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.recipe.BlastFurnaceRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
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

public class BlastFurnaceCategory extends AbstractRecipeCategory<BlastFurnaceRecipe> {
   public BlastFurnaceCategory(RecipeType<BlastFurnaceRecipe> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.blast_furnace"), helper.createDrawableItemLike(IRBlocks.BLAST_FURNACE), 110, 40);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, BlastFurnaceRecipe recipe, IFocusGroup focuses) {
      builder.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(recipe.getItemsJei());
      List<ItemStack> resultItems = recipe.getResultItems();

      for (int i = 0; i < resultItems.size(); i++) {
         builder.addOutputSlot(61 + i * 25, 10).setOutputSlotBackground().addItemStack(resultItems.get(i));
      }

      builder.addInputSlot(1, 21).setStandardSlotBackground().addItemStack(IRItems.COMPRESSED_AIR_CELL.toStack(recipe.getCells()));
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, BlastFurnaceRecipe recipe, IFocusGroup focuses) {
      int duration = recipe.getDuration();
      builder.addAnimatedRecipeArrow(Math.clamp(duration, 20, 200)).setPosition(26, 11);
      if (duration > 0) {
         int cookTimeSeconds = duration / 20;
         Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", new Object[]{cookTimeSeconds});
         ((ITextWidget)builder.addText(timeString, this.getWidth() - 20, 10)
               .setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM))
            .setTextAlignment(HorizontalAlignment.RIGHT)
            .setTextAlignment(VerticalAlignment.BOTTOM)
            .setColor(-8355712);
      }
   }
}
