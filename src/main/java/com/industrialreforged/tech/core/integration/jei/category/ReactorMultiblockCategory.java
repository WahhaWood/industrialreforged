package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.integration.jei.ReactorMultiblockRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Страница JEI «Мультиблок: ядерный реактор» в том же стиле ER2, что и страница
 * резервуара: панель, список блоков/компонентов и правила сборки.
 */
public class ReactorMultiblockCategory extends AbstractRecipeCategory<ReactorMultiblockRecipe> {
   private static final int WIDTH = 224;
   private static final int HEIGHT = 166;
   private static final int SLOT_X = 118;
   private static final int LABEL_X = 140;
   private static final int TEXT_COLOR = 0xF2F8FF;

   private final IDrawable background;

   public ReactorMultiblockCategory(RecipeType<ReactorMultiblockRecipe> recipeType, IGuiHelper helper) {
      super(
         recipeType,
         Component.translatable("text.industrialreforged.jei.reactor_multiblock.title"),
         helper.createDrawableItemLike(IRBlocks.NUCLEAR_REACTOR),
         WIDTH,
         HEIGHT
      );
      this.background = helper.createDrawable(IR.texture("screen/tank_controller"), 0, 0, WIDTH, HEIGHT);
   }

   @Override
   public IDrawable getBackground() {
      return this.background;
   }

   @Override
   public boolean needsRecipeBorder() {
      return false;
   }

   @Override
   public void setRecipe(IRecipeLayoutBuilder builder, ReactorMultiblockRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 28).addItemStack(new ItemStack(IRBlocks.NUCLEAR_REACTOR.get()));
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 54).addItemStack(new ItemStack(IRBlocks.REACTOR_CHAMBER.get()));
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 80).addItemStack(new ItemStack(IRItems.URANIUM_FUEL_ROD.get()));
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 106).addItemStack(new ItemStack(IRItems.HEAT_VENT.get()));
   }

   @Override
   public void createRecipeExtras(IRecipeExtrasBuilder builder, ReactorMultiblockRecipe recipe, IFocusGroup focuses) {
      this.label(builder, "text.industrialreforged.jei.reactor_multiblock.core", 32, null);
      this.label(builder, "text.industrialreforged.jei.reactor_multiblock.chamber", 58, "text.industrialreforged.jei.reactor_multiblock.chamber.tip");
      this.label(builder, "text.industrialreforged.jei.reactor_multiblock.fuel", 84, "text.industrialreforged.jei.reactor_multiblock.fuel.tip");
      this.label(builder, "text.industrialreforged.jei.reactor_multiblock.vent", 110, "text.industrialreforged.jei.reactor_multiblock.vent.tip");

      this.line(builder, "text.industrialreforged.jei.reactor_multiblock.size", 136, "text.industrialreforged.jei.reactor_multiblock.size.tip");
      this.line(builder, "text.industrialreforged.jei.reactor_multiblock.grid", 146, "text.industrialreforged.jei.reactor_multiblock.grid.tip");
      this.line(builder, "text.industrialreforged.jei.reactor_multiblock.heat", 156, "text.industrialreforged.jei.reactor_multiblock.heat.tip");
   }

   private void label(IRecipeExtrasBuilder builder, String key, int y, String tooltipKey) {
      ITextWidget widget = builder.addText(Component.translatable(key), WIDTH - LABEL_X - 4, 10)
         .setColor(TEXT_COLOR)
         .setShadow(true)
         .setPosition(LABEL_X, y, WIDTH - LABEL_X - 4, 10, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
      if (tooltipKey != null) {
         widget.setTooltip(Component.translatable(tooltipKey));
      }
   }

   private void line(IRecipeExtrasBuilder builder, String key, int y, String tooltipKey) {
      ITextWidget widget = builder.addText(Component.translatable(key), WIDTH - 16, 10)
         .setColor(TEXT_COLOR)
         .setShadow(true)
         .setPosition(8, y, WIDTH - 16, 10, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
      if (tooltipKey != null) {
         widget.setTooltip(Component.translatable(tooltipKey));
      }
   }
}
