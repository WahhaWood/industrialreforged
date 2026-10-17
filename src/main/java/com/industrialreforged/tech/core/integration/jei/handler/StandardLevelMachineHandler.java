package com.industrialreforged.tech.core.integration.jei.handler;

import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler1LevelBlockEntity;
import com.industrialreforged.tech.core.integration.jei.wrapper.RecyclerWrapper;
import com.industrialreforged.tech.client.screen.machine.StandardLevelMachineScreen;
import com.industrialreforged.tech.core.recipe.CompressorRecipe;
import com.industrialreforged.tech.core.recipe.ExtractorRecipe;
import com.industrialreforged.tech.core.recipe.MaceratorRecipe;
import com.industrialreforged.tech.menu.machine.StandardLevelMachineMenu;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;

public class StandardLevelMachineHandler implements IGuiContainerHandler<StandardLevelMachineScreen> {
   private final RecipeType<CompressorRecipe> compressor;
   private final RecipeType<ExtractorRecipe> extractor;
   private final RecipeType<MaceratorRecipe> macerator;
   private final RecipeType<RecyclerWrapper> recycler;

   public StandardLevelMachineHandler(
      RecipeType<CompressorRecipe> compressor, RecipeType<ExtractorRecipe> extractor, RecipeType<MaceratorRecipe> macerator, RecipeType<RecyclerWrapper> recycler
   ) {
      this.compressor = compressor;
      this.extractor = extractor;
      this.macerator = macerator;
      this.recycler = recycler;
   }

   public Collection<IGuiClickableArea> getGuiClickableAreas(StandardLevelMachineScreen containerScreen, double guiMouseX, double guiMouseY) {
      AbstractStandardMachineBlockEntity<?> entity = (AbstractStandardMachineBlockEntity<?>)((StandardLevelMachineMenu)containerScreen.getMenu()).entity;

      return List.of(switch (entity.inputSlot.getContainerSize()) {
         case 2 -> this.area(entity, 80, 24, 22, 43);
         case 3 -> this.area(entity, 75, 22, 32, 43);
         case 4 -> this.area(entity, 75, 24, 29, 43);
         default -> this.area(entity, 80, 35, 22, 15);
      });
   }

   private IGuiClickableArea area(AbstractStandardMachineBlockEntity<?> entity, int x, int y, int w, int h) {
      return IGuiClickableArea.createBasic(x, y, w, h, new RecipeType[]{switch (entity) {
         case Compressor1LevelBlockEntity ignored -> this.compressor;
         case Extractor1LevelBlockEntity ignored -> this.extractor;
         case Macerator1LevelBlockEntity ignored -> this.macerator;
         case Recycler1LevelBlockEntity ignored -> this.recycler;
         default -> RecipeTypes.SMELTING;
      }});
   }
}
