package com.industrialreforged.tech.datagen.builder;

import com.industrialreforged.tech.core.recipe.ScannerRecipe;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class ScannerRecipeBuilder extends AbstractRecipeBuilder {
   private final ItemStack item;
   private final int cost;

   private ScannerRecipeBuilder(ItemStack item, int cost) {
      this.item = item;
      this.cost = cost;
   }

   public ScannerRecipeBuilder modId(String... modIds) {
      this.conditions(Arrays.stream(modIds).map(ModLoadedCondition::new).toArray(ICondition[]::new));
      return this;
   }

   public ScannerRecipeBuilder conditions(@Nullable ICondition... conditions) {
      this.conditions = conditions;
      return this;
   }

   public void save(RecipeOutput output, ResourceLocation id) {
      output.accept(id, new ScannerRecipe(this.item, this.cost), null, this.conditions);
   }

   public static ScannerRecipeBuilder scan(ItemLike like, int cost) {
      return scan(new ItemStack(like), cost);
   }

   public static ScannerRecipeBuilder scan(ItemStack stack, int cost) {
      return new ScannerRecipeBuilder(stack, cost);
   }
}
