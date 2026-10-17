package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.core.recipe.BlockCutterRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

public class BlockCutterCategory extends StandardMachineCategory<BlockCutterRecipe> {
   private final IDrawable[] icons;

   public BlockCutterCategory(RecipeType<BlockCutterRecipe> recipeType, IGuiHelper helper) {
      super(recipeType, "block.industrialreforged.block_cutter", IRBlocks.BLOCK_CUTTER, Gauge.ProgressBlockCutter, 300, helper, 106);
      this.icons = new IDrawable[]{
         helper.createDrawableItemLike(IRItems.IRON_CUTTING_BLADE),
         helper.createDrawableItemLike(IRItems.STEEL_CUTTING_BLADE),
         helper.createDrawableItemLike(IRItems.DIAMOND_CUTTING_BLADE)
      };
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, BlockCutterRecipe recipe, IFocusGroup focuses) {
      super.createRecipeExtras(builder, recipe, focuses);
      builder.addDrawable(this.icons[recipe.hardness <= 1 ? 0 : (recipe.hardness <= 5 ? 1 : 2)], 41, 19);
   }
}
