package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.integration.jei.wrapper.TurningWrapper;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.item.turning.TurningProgram;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class TurningCategory extends AbstractRecipeCategory<TurningWrapper> {
   public TurningCategory(RecipeType<TurningWrapper> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("block.industrialreforged.turning"), helper.createDrawableItemLike(IRBlocks.TURNING), 129, 32);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, TurningWrapper wrapper, IFocusGroup focuses) {
      builder.addInputSlot(32, 1).setStandardSlotBackground().addItemStack(new ItemStack(wrapper.getInput()));
      builder.addOutputSlot(81, 1).setStandardSlotBackground().addItemStack(new ItemStack(wrapper.getOutput()));
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, TurningWrapper wrapper, IFocusGroup focuses) {
      builder.addAnimatedRecipeArrow(200).setPosition(54, 1);
      TurningProgram program = wrapper.getProgram();
      byte width = wrapper.getInput().getWidth();
      MutableComponent text = Component.literal(program.state(0) + "/" + width);

      for (int i = 1; i < 5; i++) {
         text.append(" " + program.state(i) + "/" + width);
      }

      ((ITextWidget)builder.addText(text, this.getWidth() - 10, 10)
            .setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM))
         .setTextAlignment(HorizontalAlignment.CENTER)
         .setTextAlignment(VerticalAlignment.BOTTOM)
         .setColor(-8355712);
   }
}
