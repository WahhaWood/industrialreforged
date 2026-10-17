package com.industrialreforged.tech.core.integration.jei.handler;

import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.client.screen.machine.MetalFormerScreen;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.menu.machine.MetalFormerMenu;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;

public class MetalFormerHandler implements IGuiContainerHandler<MetalFormerScreen> {
   private final RecipeType<MetalFormerRecipe> recipeType;

   public MetalFormerHandler(RecipeType<MetalFormerRecipe> recipeType) {
      this.recipeType = recipeType;
   }

   public Collection<IGuiClickableArea> getGuiClickableAreas(MetalFormerScreen containerScreen, double guiMouseX, double guiMouseY) {
      MetalFormer1LevelBlockEntity entity = (MetalFormer1LevelBlockEntity)((MetalFormerMenu)containerScreen.getMenu()).entity;

      return List.of(switch (entity.inputSlot.getContainerSize()) {
         case 2 -> this.area(63, 21, 50, 43);
         case 3 -> this.area(75, 21, 40, 43);
         case 4 -> this.area(71, 18, 50, 55);
         default -> this.area(50, 36, 50, 15);
      });
   }

   private IGuiClickableArea area(int x, int y, int w, int h) {
      return IGuiClickableArea.createBasic(x, y, w, h, new RecipeType[]{this.recipeType});
   }
}
