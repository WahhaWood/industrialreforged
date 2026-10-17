package com.industrialreforged.tech.core.integration.jei.category;

import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.builder.GaugeProperties;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputAndOutputRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.common.gui.elements.DrawableCombined;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

public class StandardMachineCategory<T extends SimpleInputAndOutputRecipe> extends AbstractRecipeCategory<T> {
   private final IDrawable energyGauge;
   private final IDrawable customGauge;

   public StandardMachineCategory(
      RecipeType<T> recipeType, String translationKey, ItemLike icon, GaugeProperties custom, int operationLength, IGuiHelper helper
   ) {
      this(recipeType, translationKey, icon, custom, operationLength, helper, 82);
   }

   public StandardMachineCategory(
      RecipeType<T> recipeType, String translationKey, ItemLike icon, GaugeProperties custom, int operationLength, IGuiHelper helper, int width
   ) {
      super(recipeType, Component.translatable(translationKey), helper.createDrawableItemLike(icon), width, 54);
      GaugeProperties bolt = EnergyGauge.Bolt;
      this.energyGauge = new DrawableCombined(
         new IDrawable[]{
            helper.createDrawable(bolt.texture, bolt.uBgInactive, bolt.vBgInactive, bolt.bgWidth, bolt.bgHeight),
            helper.createAnimatedDrawable(
               helper.createDrawable(bolt.texture, bolt.uInner, bolt.vInner, bolt.innerWidth, bolt.innerHeight),
               300,
               bolt.reverse ? (bolt.vertical ? StartDirection.TOP : StartDirection.RIGHT) : (bolt.vertical ? StartDirection.BOTTOM : StartDirection.LEFT),
               true
            )
         }
      );
      this.customGauge = new DrawableCombined(
         new IDrawable[]{
            helper.createDrawable(custom.texture, custom.uBgActive, custom.vBgActive, custom.bgWidth, custom.bgHeight),
            helper.createAnimatedDrawable(
               helper.createDrawable(custom.texture, custom.uInner, custom.vInner, custom.innerWidth, custom.innerHeight),
               operationLength,
               custom.reverse ? (custom.vertical ? StartDirection.BOTTOM : StartDirection.RIGHT) : (custom.vertical ? StartDirection.TOP : StartDirection.LEFT),
               false
            )
         }
      );
   }

   public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
      builder.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(recipe.getItemsJei());
      builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 1, 37).setStandardSlotBackground();
      builder.addOutputSlot(this.getWidth() - 21, 19).setOutputSlotBackground().addItemStacks(recipe.getResultItems());
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
      builder.addDrawable(this.energyGauge, 1, 20);
      builder.addDrawable(this.customGauge, 26, 17);
   }
}
