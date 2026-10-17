package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.tech.core.recipe.basic.BasicRecipe;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * The recycler's only "recipe": any item outside {@link IRItemTags#RECYCLER_BLACKLIST} turns into
 * scrap with a 1/{@value #SCRAP_CHANCE} chance. Lives in code, never in the recipe manager, so it has
 * no type or serializer; the machine asks {@link #accepts} instead of matching an ingredient.
 */
public final class RecyclerRecipe extends BasicRecipe {
   public static final RecyclerRecipe INSTANCE = new RecyclerRecipe();
   /** One scrap per this many recycled items on average (IC2: 1/8). */
   public static final int SCRAP_CHANCE = 8;

   private RecyclerRecipe() {
   }

   public static boolean accepts(ItemStack stack) {
      return !stack.isEmpty() && !stack.is(IRItemTags.RECYCLER_BLACKLIST);
   }

   /** Scrap is produced by the recycler slot itself, see {@code InvSlotProcessableRecycler#assemble}. */
   @Override
   public boolean assemble(RecipeInput input, int slot) {
      return false;
   }

   @Override
   public NonNullList<ItemStack> getResultItems() {
      // a fresh list: callers copy and mutate counts while checking free space
      return NonNullList.of(ItemStack.EMPTY, new ItemStack(IRItems.SCRAP.get()));
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      throw new UnsupportedOperationException("The recycler recipe is code-only and never registered");
   }

   @Override
   public RecipeType<?> getType() {
      throw new UnsupportedOperationException("The recycler recipe is code-only and never registered");
   }
}
