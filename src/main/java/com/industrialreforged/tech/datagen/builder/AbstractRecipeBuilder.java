package com.industrialreforged.tech.datagen.builder;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.ICondition;

abstract class AbstractRecipeBuilder implements RecipeBuilder {
   protected ICondition[] conditions = new ICondition[0];

   public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
      return this;
   }

   public RecipeBuilder group(String groupName) {
      return this;
   }

   public Item getResult() {
      return Items.AIR;
   }
}
