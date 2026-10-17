package com.industrialreforged.tech.core.integration.jei.handler;

import com.industrialreforged.tech.client.screen.machine.BlockCutterScreen;
import com.industrialreforged.tech.core.recipe.BlockCutterRecipe;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;

public class BlockCutterHandler implements IGuiContainerHandler<BlockCutterScreen> {
   private final RecipeType<BlockCutterRecipe> recipeType;

   public BlockCutterHandler(RecipeType<BlockCutterRecipe> recipeType) {
      this.recipeType = recipeType;
   }

   public Collection<IGuiClickableArea> getGuiClickableAreas(BlockCutterScreen containerScreen, double guiMouseX, double guiMouseY) {
      return List.of(
         IGuiClickableArea.createBasic(55, 33, 15, 19, new RecipeType[]{this.recipeType}),
         IGuiClickableArea.createBasic(86, 33, 15, 19, new RecipeType[]{this.recipeType})
      );
   }
}
