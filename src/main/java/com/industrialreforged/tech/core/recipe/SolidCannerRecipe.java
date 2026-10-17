package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.recipe.basic.BasicRecipe;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ItemCell;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

public class SolidCannerRecipe extends BasicRecipe {
   private static final List<SolidCannerRecipe> RECIPE_LIST = new ArrayList<>();
   public Ingredient input;
   public int inputCount;
   public Ingredient center;
   public int centerCount;
   public ItemStack result;

   public SolidCannerRecipe() {
   }

   public SolidCannerRecipe(Ingredient input, int inputCount, Ingredient center, int centerCount, ItemStack result) {
      this.input = input;
      this.inputCount = inputCount;
      this.center = center;
      this.centerCount = centerCount;
      this.result = result;
   }

   public boolean matches(ItemStack input, ItemStack center) {
      return input.getCount() >= this.inputCount && center.getCount() >= this.centerCount && this.input.test(input) && this.center.test(center);
   }

   public void assemble(BasicSimpleContainer input, BasicSimpleContainer center, InvSlotOutput output) {
      input.shrink(0, this.inputCount);
      center.shrink(0, this.centerCount);
      output.add(this.result.copy());
   }

   @Override
   public boolean matches(RecipeInput machine, Level level) {
      return this.input.test(machine.getItem(0)) && this.center.test(machine.getItem(1));
   }

   @Override
   public boolean assemble(RecipeInput machine, int indexSlot) {
      return false;
   }

   public NonNullList<Ingredient> getIngredients() {
      return NonNullList.of(Ingredient.EMPTY, new Ingredient[]{this.input, this.center});
   }

   @Override
   public NonNullList<ItemStack> getResultItems() {
      // Настоящий результат (а не пусто): иначе гейт места в выходе, кламп пакета
      // укрепления и JEI видят пустой список — продукт сгорает и не отображается.
      if (this.result == null || this.result.isEmpty()) {
         return NonNullList.create();
      }
      NonNullList<ItemStack> list = NonNullList.create();
      list.add(this.result.copy());
      return list;
   }

   public RecipeSerializer<?> getSerializer() {
      return null;
   }

   public RecipeType<?> getType() {
      return null;
   }

   public static void init() {
      add((ItemLike)IRItems.FUEL_ROD.get(), (ItemLike)IRItems.URANIUM.get(), (ItemLike)IRItems.URANIUM_FUEL_ROD.get());
      add((ItemLike)IRItems.FUEL_ROD.get(), (ItemLike)IRItems.MOX.get(), (ItemLike)IRItems.MOX_FUEL_ROD.get());
      add((ItemLike)IRItems.TIN_CAN.get(), Items.POTATO, (ItemLike)IRItems.FILLED_TIN_CAN.get());
      add((ItemLike)IRItems.TIN_CAN.get(), 1, Items.POISONOUS_POTATO, 2, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 1);
      add((ItemLike)IRItems.TIN_CAN.get(), 1, Items.ROTTEN_FLESH, 2, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 1);
      add((ItemLike)IRItems.TIN_CAN.get(), 2, Items.COOKIE, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 2);
      add((ItemLike)IRItems.TIN_CAN.get(), 2, Items.MELON, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 2);
      add((ItemLike)IRItems.TIN_CAN.get(), 2, Items.COD, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 2);
      add((ItemLike)IRItems.TIN_CAN.get(), 2, Items.SALMON, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 2);
      add((ItemLike)IRItems.TIN_CAN.get(), 2, Items.TROPICAL_FISH, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 2);
      add((ItemLike)IRItems.TIN_CAN.get(), 2, Items.CHICKEN, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 2);
      add((ItemLike)IRItems.TIN_CAN.get(), 2, Items.RABBIT, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 2);
      add((ItemLike)IRItems.TIN_CAN.get(), 3, Items.PORKCHOP, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 3);
      add((ItemLike)IRItems.TIN_CAN.get(), 3, Items.BEEF, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 3);
      add((ItemLike)IRItems.TIN_CAN.get(), 4, Items.APPLE, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 4);
      add((ItemLike)IRItems.TIN_CAN.get(), 4, Items.CARROT, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 4);
      add((ItemLike)IRItems.TIN_CAN.get(), 5, Items.BREAD, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 5);
      add((ItemLike)IRItems.TIN_CAN.get(), 5, Items.COOKED_COD, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 5);
      add((ItemLike)IRItems.TIN_CAN.get(), 5, Items.COOKED_SALMON, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 5);
      add((ItemLike)IRItems.TIN_CAN.get(), 6, Items.COOKED_CHICKEN, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 6);
      add((ItemLike)IRItems.TIN_CAN.get(), 6, Items.COOKED_RABBIT, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 6);
      add((ItemLike)IRItems.TIN_CAN.get(), 6, Items.BAKED_POTATO, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 6);
      add((ItemLike)IRItems.TIN_CAN.get(), 6, Items.MUSHROOM_STEW, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 6);
      add((ItemLike)IRItems.TIN_CAN.get(), 6, Items.PUMPKIN_PIE, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 6);
      add((ItemLike)IRItems.TIN_CAN.get(), 8, Items.COOKED_PORKCHOP, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 8);
      add((ItemLike)IRItems.TIN_CAN.get(), 8, Items.COOKED_BEEF, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 8);
      add((ItemLike)IRItems.TIN_CAN.get(), 12, Items.CAKE, 1, (ItemLike)IRItems.FILLED_TIN_CAN.get(), 12);
      add(ItemCell.getCell(Fluids.WATER), Ingredient.of(new ItemLike[]{(ItemLike)IRItems.CF_POWDER.get()}), 1, IRFluids.CONSTRUCTION_FOAM.getCell());
      add(ItemCell.getCell(Fluids.WATER), Ingredient.of(IRItemTags.DUSTS_LAPIS), 8, IRFluids.COOLANT.getCell());
      add(IRFluids.DISTILLED_WATER.getCell(), Ingredient.of(IRItemTags.DUSTS_LAPIS), 1, IRFluids.COOLANT.getCell());
   }

   private static void add(ItemLike input, ItemLike center, ItemLike result) {
      add(input, 1, center, 1, result, 1);
   }

   private static void add(ItemLike input, int inputCount, ItemLike center, int centerCount, ItemLike result, int resultCount) {
      RECIPE_LIST.add(
         new SolidCannerRecipe(
            Ingredient.of(new ItemLike[]{input}), inputCount, Ingredient.of(new ItemLike[]{center}), centerCount, new ItemStack(result, resultCount)
         )
      );
   }

   private static void add(ItemStack input, Ingredient center, int centerCount, ItemStack result) {
      RECIPE_LIST.add(new SolidCannerRecipe(DataComponentIngredient.of(true, input), 1, center, centerCount, result));
   }

   public static List<SolidCannerRecipe> gets() {
      return RECIPE_LIST;
   }
}
