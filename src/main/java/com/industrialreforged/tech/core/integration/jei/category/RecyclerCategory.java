package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.integration.jei.wrapper.RecyclerWrapper;
import com.industrialreforged.tech.core.recipe.RecyclerRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
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

public class RecyclerCategory extends AbstractRecipeCategory<RecyclerWrapper> {
   public RecyclerCategory(RecipeType<RecyclerWrapper> recipeType, IGuiHelper helper) {
      super(recipeType, Component.translatable("text.industrialreforged.recycler"), helper.createDrawableItemLike(IRBlocks.RECYCLER_1_LEVEL), 82, 40);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, RecyclerWrapper wrapper, IFocusGroup focuses) {
      builder.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(wrapper.examples());
      builder.addOutputSlot(61, 1).setOutputSlotBackground().addItemStack(new ItemStack(IRItems.SCRAP.get()));
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, RecyclerWrapper wrapper, IFocusGroup focuses) {
      builder.addAnimatedRecipeArrow(45).setPosition(26, 1);
      Component chance = Component.translatable("text.industrialreforged.recycler.chance", String.format(java.util.Locale.ROOT, "%.1f", 100.0 / RecyclerRecipe.SCRAP_CHANCE));
      ((ITextWidget)builder.addText(chance, this.getWidth(), 10)
            .setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM))
         .setTextAlignment(HorizontalAlignment.CENTER)
         .setTextAlignment(VerticalAlignment.BOTTOM)
         .setColor(-8355712);
   }
}
