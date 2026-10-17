package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.integration.jei.TankMultiblockRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Страница JEI «Мультиблок: резервуар-котёл» в стиле Extreme Reactors: панель ER2,
 * изометрическая схема 3×3×3 с клапаном и контроллером и список блоков оболочки.
 */
public class TankMultiblockCategory extends AbstractRecipeCategory<TankMultiblockRecipe> {
   private static final int WIDTH = 224;
   private static final int HEIGHT = 166;
   private static final int ISO_W = 104;
   private static final int ISO_H = 116;
   private static final int SLOT_X = 118;
   private static final int LABEL_X = 140;
   private static final int TEXT_COLOR = 0xF2F8FF;

   private final IDrawable background;
   private final IDrawable iso;

   public TankMultiblockCategory(RecipeType<TankMultiblockRecipe> recipeType, IGuiHelper helper) {
      super(
         recipeType,
         Component.translatable("text.industrialreforged.jei.tank_multiblock.title"),
         helper.createDrawableItemLike(IRBlocks.TANK_CONTROLLER),
         WIDTH,
         HEIGHT
      );
      this.background = helper.createDrawable(IR.texture("screen/tank_controller"), 0, 0, WIDTH, HEIGHT);
      this.iso = helper.createDrawable(IR.texture("screen/tank_iso"), 0, 0, ISO_W, ISO_H);
   }

   @Override
   public IDrawable getBackground() {
      return new IDrawable() {
         @Override
         public int getWidth() {
            return WIDTH;
         }

         @Override
         public int getHeight() {
            return HEIGHT;
         }

         @Override
         public void draw(GuiGraphics graphics, int x, int y) {
            TankMultiblockCategory.this.background.draw(graphics, x, y);
            TankMultiblockCategory.this.iso.draw(graphics, x + 8, y + 24);
         }
      };
   }

   @Override
   public boolean needsRecipeBorder() {
      return false;
   }

   @Override
   public void setRecipe(IRecipeLayoutBuilder builder, TankMultiblockRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 28).addItemStack(new ItemStack(IRBlocks.TANK_WALL.get()));
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 54).addItemStack(new ItemStack(IRBlocks.REINFORCED_GLASS.get()));
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 80).addItemStack(new ItemStack(IRBlocks.TANK_VALVE.get()));
      builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, 106).addItemStack(new ItemStack(IRBlocks.TANK_CONTROLLER.get()));
   }

   @Override
   public void createRecipeExtras(IRecipeExtrasBuilder builder, TankMultiblockRecipe recipe, IFocusGroup focuses) {
      this.label(builder, "text.industrialreforged.jei.tank_multiblock.wall", 32, null);
      this.label(builder, "text.industrialreforged.jei.tank_multiblock.glass", 58, "text.industrialreforged.jei.tank_multiblock.glass.tip");
      this.label(builder, "text.industrialreforged.jei.tank_multiblock.valve", 84, null);
      this.label(builder, "text.industrialreforged.jei.tank_multiblock.controller", 110, "text.industrialreforged.jei.tank_multiblock.controller.tip");

      this.line(builder, "text.industrialreforged.jei.tank_multiblock.size", 136, null);
      this.line(builder, "text.industrialreforged.jei.tank_multiblock.build", 146, null);
      this.line(builder, "text.industrialreforged.jei.tank_multiblock.heat", 156, "text.industrialreforged.jei.tank_multiblock.heat.tip");
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
