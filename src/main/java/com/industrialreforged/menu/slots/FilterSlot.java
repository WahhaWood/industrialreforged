package com.industrialreforged.menu.slots;

import java.util.Arrays;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;

public class FilterSlot extends BaseSlot {
   private final Ingredient filter;

   public FilterSlot(Container inv, int id, int x, int y, ItemLike... likes) {
      this(inv, id, x, y, -1, likes);
   }

   public FilterSlot(Container inv, int id, int x, int y, int limitStack, ItemLike... likes) {
      this(inv, id, x, y, limitStack, Ingredient.of(likes));
   }

   public FilterSlot(Container inv, int id, int x, int y, TagKey<Item> tagKey) {
      this(inv, id, x, y, -1, tagKey);
   }

   public FilterSlot(Container inv, int id, int x, int y, int limitStack, TagKey<Item> tagKey) {
      this(inv, id, x, y, limitStack, Ingredient.of(tagKey));
   }

   public FilterSlot(Container inv, int id, int x, int y, ItemStack... stacks) {
      this(inv, id, x, y, -1, stacks);
   }

   public FilterSlot(Container inv, int id, int x, int y, int limitStack, ItemStack... stacks) {
      this(inv, id, x, y, limitStack, Ingredient.of(stacks));
   }

   public FilterSlot(Container inv, int id, int x, int y, Ingredient ingredient) {
      this(inv, id, x, y, -1, ingredient);
   }

   public FilterSlot(Container inv, int id, int x, int y, int limitStack, Ingredient ingredient) {
      super(inv, id, x, y, limitStack);
      this.filter = ingredient;
   }

   @Deprecated(forRemoval = true)
   public FilterSlot(Container inv, int id, int x, int y, Item... filters) {
      this(inv, id, x, y, -1, filters);
   }

   @Deprecated(forRemoval = true)
   public FilterSlot(Container inv, int id, int x, int y, int limitStack, Item... filters) {
      this(inv, id, x, y, limitStack, Arrays.stream(filters).map(ItemStack::new).toArray(ItemStack[]::new));
   }

   @Deprecated(forRemoval = true)
   public FilterSlot(Container inv, int id, int x, int y, boolean ignoreNbt, Item... filters) {
      this(inv, id, x, y, ignoreNbt, -1, filters);
   }

   @Deprecated(forRemoval = true)
   public FilterSlot(Container inv, int id, int x, int y, boolean ignoreNbt, int limitStack, Item... filters) {
      this(inv, id, x, y, ignoreNbt, limitStack, Arrays.stream(filters).map(ItemStack::new).toArray(ItemStack[]::new));
   }

   @Deprecated(forRemoval = true)
   public FilterSlot(Container inv, int id, int x, int y, boolean ignoreNbt, ItemStack... filters) {
      this(inv, id, x, y, ignoreNbt, -1, filters);
   }

   @Deprecated(forRemoval = true)
   public FilterSlot(Container inv, int id, int x, int y, boolean ignoreNbt, int limitStack, ItemStack... filters) {
      super(inv, id, x, y, limitStack);
      this.filter = ignoreNbt
         ? Ingredient.of(filters)
         : (
            filters.length == 1
               ? DataComponentIngredient.of(false, filters[0])
               : CompoundIngredient.of(Arrays.stream(filters).map(stack -> DataComponentIngredient.of(false, stack)).toArray(Ingredient[]::new))
         );
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return this.filter.test(stack);
   }
}
