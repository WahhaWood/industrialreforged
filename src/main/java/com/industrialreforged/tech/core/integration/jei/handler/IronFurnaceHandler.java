package com.industrialreforged.tech.core.integration.jei.handler;

import com.industrialreforged.tech.client.screen.machine.IronFurnaceScreen;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;

public class IronFurnaceHandler implements IGuiContainerHandler<IronFurnaceScreen> {
   public Collection<IGuiClickableArea> getGuiClickableAreas(IronFurnaceScreen containerScreen, double guiMouseX, double guiMouseY) {
      return List.of(
         IGuiClickableArea.createBasic(56, 36, 13, 13, new RecipeType[]{RecipeTypes.FUELING}),
         IGuiClickableArea.createBasic(80, 35, 22, 15, new RecipeType[]{RecipeTypes.SMELTING})
      );
   }
}
