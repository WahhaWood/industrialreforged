package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.client.screen.widgets.builder.GaugePropertyBuilder;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;

public class MetalFormerCategory extends StandardMachineCategory<MetalFormerRecipe> {
   private final IDrawable[] icons;

   public MetalFormerCategory(RecipeType<MetalFormerRecipe> recipeType, IGuiHelper helper) {
      super(
         recipeType,
         "text.industrialreforged.metal_former",
         IRBlocks.METAL_FORMER_1_LEVEL,
         GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right).uv(200, 16).size(46, 15).withBackground(200, 0).build(),
         300,
         helper,
         106
      );
      this.icons = new IDrawable[]{
         helper.createDrawableItemLike(com.industrialreforged.transfer.init.GTItems.WIRE_2_LEVEL),
         helper.createDrawableItemLike(IRItems.CUTTER),
         helper.createDrawableItemLike(IRItems.FORGE_HAMMER)
      };
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, MetalFormerRecipe recipe, IFocusGroup focuses) {
      super.createRecipeExtras(builder, recipe, focuses);
      builder.addDrawable(this.icons[recipe.getMode().ordinal()], 41, 35);
   }

   public void getTooltip(ITooltipBuilder tooltip, MetalFormerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      if (mouseX >= 41.0 && mouseX <= 57.0 && mouseY >= 35.0 && mouseY <= 51.0) {
         tooltip.add(switch (recipe.getMode()) {
            case EXTRUDING -> Component.translatable("text.industrialreforged.tooltip.metal_former.switch.extruding");
            case CUTTING -> Component.translatable("text.industrialreforged.tooltip.metal_former.switch.cutting");
            case ROLLING -> Component.translatable("text.industrialreforged.tooltip.metal_former.switch.rolling");
         });
      }
   }
}
