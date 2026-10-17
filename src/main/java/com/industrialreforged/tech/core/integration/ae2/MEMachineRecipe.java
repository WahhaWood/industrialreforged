package com.industrialreforged.tech.core.integration.ae2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * One recipe as an ME machine sees it: a single (possibly tagged) input times a count, fixed outputs.
 * All machine recipes the ME machines take are single-input, so this covers them without chance outputs.
 * {@code aeCost} is the AE one craft draws from the network.
 */
public record MEMachineRecipe(ResourceLocation id, Ingredient input, int inputCount, List<ItemStack> outputs, double aeCost) {
   /**
    * Bumped whenever recipes may have changed (datapack reload on the server, recipe sync on the
    * client). Machines compare it with the value they built their list for.
    */
   private static final AtomicInteger GENERATION = new AtomicInteger();

   @Nullable
   static MEMachineRecipe of(ResourceLocation id, Ingredient input, int inputCount, List<ItemStack> results, double aeCost) {
      if (inputCount <= 0 || input.isEmpty() || input.getItems().length == 0) {
         return null;
      }

      List<ItemStack> outputs = new ArrayList<>(results.size());
      for (ItemStack result : results) {
         if (!result.isEmpty()) {
            outputs.add(result.copy());
         }
      }

      return outputs.isEmpty() ? null : new MEMachineRecipe(id, input, inputCount, List.copyOf(outputs), aeCost);
   }

   /** The item the GUI shows for this recipe. */
   public ItemStack icon() {
      return this.outputs.get(0);
   }

   public static int generation() {
      return GENERATION.get();
   }

   public static void invalidate() {
      GENERATION.incrementAndGet();
   }

   /**
    * All usable recipes of a kind, ordered by id. Server and client build the same list from the same
    * synced recipes, so the GUI only needs the set of switched-off ids, never the recipes themselves.
    */
   @SuppressWarnings({"unchecked", "rawtypes"})
   public static Map<ResourceLocation, MEMachineRecipe> collect(MEMachineKind kind, Level level) {
      List<MEMachineRecipe> list = new ArrayList<>();
      for (RecipeHolder<?> holder : level.getRecipeManager().getAllRecipesFor((RecipeType<Recipe<RecipeInput>>)(RecipeType)kind.recipeType())) {
         MEMachineRecipe recipe = kind.convert(holder, level.registryAccess());
         if (recipe != null) {
            list.add(recipe);
         }
      }

      list.sort(Comparator.comparing(recipe -> recipe.id().toString()));
      Map<ResourceLocation, MEMachineRecipe> byId = new LinkedHashMap<>();
      for (MEMachineRecipe recipe : list) {
         byId.put(recipe.id(), recipe);
      }

      return byId;
   }
}
