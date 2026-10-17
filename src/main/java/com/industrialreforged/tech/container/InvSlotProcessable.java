package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class InvSlotProcessable<T extends BaseBlockEntity, R extends Recipe<?>> extends BasicSimpleContainer implements IProcessable<R>, RecipeInput {
   protected final T entity;
   protected final RecipeType<R> recipeType;
   protected final Recipe<RecipeInput>[] lastRecipes = new Recipe[4];
   private int indexRecipe;

   public InvSlotProcessable(T entity, String string, int size, @Nullable RecipeType<R> recipeType) {
      super(entity, string, size);
      this.entity = entity;
      this.recipeType = recipeType;
      this.setCanOutput(false);
   }

   public T getEntity() {
      return this.entity;
   }

   public boolean canPlaceItem(int index, ItemStack itemStack) {
      Level level = this.entity.getLevel();
      return level != null && !itemStack.isEmpty()
         ? this.canPlaceLast(itemStack) || ((java.util.Collection<RecipeHolder<?>>)level.getRecipeManager().byType((RecipeType)this.recipeType)).stream().anyMatch(holder -> {
            for (Ingredient ingredient : holder.value().getIngredients()) {
               if (ingredient.test(itemStack)) {
                  return true;
               }
            }

            return false;
         })
         : false;
   }

   @Nullable
   @Override
   public R process(int slot) {
      ItemStack stack = this.getItem(slot);
      if (stack.isEmpty()) {
         return null;
      }

      Level level = this.entity.getLevel();
      if (level == null) {
         return null;
      }

      RecipeInput input = this.createInput(stack);

      for (Recipe<RecipeInput> lastRecipe : this.lastRecipes) {
         if (lastRecipe != null && lastRecipe.matches(input, level)) {
            return (R)lastRecipe;
         }
      }

      RecipeHolder<?> holder = (RecipeHolder<?>)level.getRecipeManager()
         .getRecipeFor((RecipeType)this.recipeType, input, level)
         .orElse(null);
      Recipe<RecipeInput> recipe = holder == null ? null : (Recipe<RecipeInput>)holder.value();
      if (recipe != null) {
         this.lastRecipes[this.indexRecipe] = recipe;
         this.indexRecipe = (this.indexRecipe + 1) % this.lastRecipes.length;
      }

      return (R)recipe;
   }

   public int size() {
      return this.getContainerSize();
   }

   protected boolean canPlaceLast(ItemStack stack) {
      for (Recipe<RecipeInput> lastRecipe : this.lastRecipes) {
         if (lastRecipe != null) {
            for (Ingredient ingredient : lastRecipe.getIngredients()) {
               if (ingredient.test(stack)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   protected RecipeInput createInput(ItemStack stack) {
      return new SingleRecipeInput(stack);
   }

   /**
    * Выполняет до {@code amount} операций подряд (пакет улучшения «Укрепление»).
    * Возвращает число реально выполненных операций.
    */
   public int assemble(int slot, int amount) {
      int done = 0;
      for (int i = 0; i < amount; i++) {
         if (!this.assemble(slot)) {
            break;
         }
         done++;
      }
      return done;
   }
}
