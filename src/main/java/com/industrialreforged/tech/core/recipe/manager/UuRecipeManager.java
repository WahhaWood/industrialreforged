package com.industrialreforged.tech.core.recipe.manager;

import com.industrialreforged.tech.core.recipe.ScannerRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class UuRecipeManager {
   public static final UuRecipeManager instance = new UuRecipeManager();

   public int getCost(@Nullable Level level, ItemStack request) {
      RecipeManager manager = this.getRecipeManager(level);
      return manager == null
         ? -1
         : manager.byType(IRRecipeTypes.SCANNER.get())
            .stream()
            .filter(holder -> ((ScannerRecipe)holder.value()).matches(request))
            .findFirst()
            .<ScannerRecipe>map(RecipeHolder::value)
            .map(ScannerRecipe::getCost)
            .orElse(-1);
   }

   public ItemStack find(@Nullable Level level, ItemStack request) {
      RecipeManager manager = this.getRecipeManager(level);
      return manager == null
         ? ItemStack.EMPTY
         : manager.byType(IRRecipeTypes.SCANNER.get())
            .stream()
            .filter(holder -> ((ScannerRecipe)holder.value()).matches(request))
            .findFirst()
            .<ScannerRecipe>map(RecipeHolder::value)
            .map(ScannerRecipe::getItem)
            .orElse(ItemStack.EMPTY);
   }

   @Nullable
   private RecipeManager getRecipeManager(@Nullable Level level) {
      return level == null ? (RecipeManager)null : level.getRecipeManager();
   }
}
