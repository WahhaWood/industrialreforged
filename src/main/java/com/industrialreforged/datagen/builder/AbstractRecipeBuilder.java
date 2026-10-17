package com.industrialreforged.datagen.builder;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ItemExistsCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public abstract class AbstractRecipeBuilder<T extends AbstractRecipeBuilder<T>> implements RecipeBuilder {
   protected ICondition[] conditions = new ICondition[0];
   protected final Item result;
   @Nullable
   protected String group;

   public AbstractRecipeBuilder() {
      this(Items.AIR);
   }

   public AbstractRecipeBuilder(ItemLike result) {
      this.result = result.asItem();
   }

   protected abstract Recipe<?> build();

   public T modId(String... modIds) {
      return this.conditions(Arrays.stream(modIds).map(ModLoadedCondition::new).toArray(ICondition[]::new));
   }

   public T itemExists(ItemLike... items) {
      return this.conditions(
         Arrays.stream(items)
            .map(ItemLike::asItem)
            .<ResourceLocation>map(BuiltInRegistries.ITEM::getKey)
            .filter(location -> location != null && !location.toString().equals("minecraft:air"))
            .map(ItemExistsCondition::new)
            .toArray(ICondition[]::new)
      );
   }

   public T conditions(ICondition... values) {
      this.conditions = values;
      return (T)this;
   }

   public final void save(RecipeOutput recipeOutput, ResourceLocation id) {
      recipeOutput.accept(id, this.build(), null, this.conditions);
   }

   public final RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
      return this;
   }

   public final RecipeBuilder group(String groupName) {
      this.group = groupName;
      return this;
   }

   public final Item getResult() {
      return this.result;
   }
}
