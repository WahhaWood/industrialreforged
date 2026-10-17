package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public class InvSlotProcessableForming extends InvSlotProcessable<MetalFormer1LevelBlockEntity, MetalFormerRecipe> {
   public InvSlotProcessableForming(MetalFormer1LevelBlockEntity inv, String name, int size) {
      super(inv, name, size, IRRecipeTypes.METAL_FORMER.get());
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack itemStack) {
      Level level = this.entity.getLevel();
      return level != null && !itemStack.isEmpty()
         ? this.canPlaceLast(itemStack) || level.getRecipeManager().byType(this.recipeType).stream().anyMatch(holder -> {
            if (((MetalFormerRecipe)holder.value()).getMode() != this.entity.getMode()) {
               return false;
            }

            for (Ingredient ingredient : holder.value().getIngredients()) {
               if (ingredient.test(itemStack)) {
                  return true;
               }
            }

            return false;
         })
         : false;
   }

   @Override
   public boolean assemble(int slot) {
      MetalFormerRecipe recipe = this.process(slot);
      return recipe != null && recipe.assemble(this, slot);
   }

   @Override
   protected RecipeInput createInput(ItemStack stack) {
      return new MetalFormerRecipe.SingleRecipeInput(stack, this.entity.getMode());
   }
}
