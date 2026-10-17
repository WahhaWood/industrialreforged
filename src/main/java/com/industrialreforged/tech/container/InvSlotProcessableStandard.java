package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.core.recipe.basic.BasicRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class InvSlotProcessableStandard<T extends BaseBlockEntity, R extends BasicRecipe> extends InvSlotProcessable<T, R> {
   public InvSlotProcessableStandard(T entity, String string, int size, RecipeType<R> recipeType) {
      super(entity, string, size, recipeType);
   }

   @Override
   public boolean assemble(int slot) {
      R recipe = this.process(slot);
      return recipe != null && recipe.assemble(this, slot);
   }
}
