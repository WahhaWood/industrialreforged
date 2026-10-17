package com.industrialreforged.tech.core.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ToolUseRecipe implements CraftingRecipe {

   @Override
   public boolean isSpecial() {
      return true;
   }

   private final Ingredient tool;
   private final Ingredient input;
   private final ItemStack output;
   private final NonNullList<Ingredient> ingredients;

   public ToolUseRecipe(Ingredient tool, Ingredient input, ItemStack output) {
      this.tool = tool;
      this.input = input;
      this.output = output;
      this.ingredients = NonNullList.create();
      this.ingredients.add(tool);
      this.ingredients.add(input);
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.TOOL_USE.get();
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }

   public boolean matches(CraftingInput input, Level level) {
      int tool = -1;

      for (int i = 0; i < input.size(); i++) {
         ItemStack itemStack = input.getItem(i);
         if (!itemStack.isEmpty() && this.tool.test(itemStack)) {
            tool = i;
            break;
         }
      }

      if (tool == -1) {
         return false;
      }

      boolean check = false;
      boolean result = false;

      for (int i = 0; i < input.size(); i++) {
         ItemStack itemStack = input.getItem(i);
         if (!itemStack.isEmpty() && i != tool) {
            if (check) {
               return false;
            }

            check = true;
            result = this.input.test(itemStack);
            if (!result) {
               return false;
            }
         }
      }

      return result;
   }

   public ItemStack assemble(CraftingInput input, Provider registries) {
      return this.output.copy();
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
      int n = input.size();
      NonNullList<ItemStack> nonNullList = NonNullList.withSize(n, ItemStack.EMPTY);
      boolean bl = false;

      for (int i = 0; i < n; i++) {
         ItemStack itemStack = input.getItem(i);
         if (!itemStack.isEmpty()) {
            if (!bl && this.tool.test(itemStack)) {
               bl = true;
               if (itemStack.isDamageableItem()) {
                  ItemStack damagedTool = itemStack.copy();
                  damagedTool.setDamageValue(damagedTool.getDamageValue() + 1);
                  if (damagedTool.getDamageValue() < damagedTool.getMaxDamage()) {
                     nonNullList.set(i, damagedTool);
                  }
                  continue;
               }
            }

            if (itemStack.hasCraftingRemainingItem()) {
               nonNullList.set(i, itemStack.getCraftingRemainingItem());
            }
         }
      }

      return nonNullList;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.output;
   }

   public CraftingBookCategory category() {
      return CraftingBookCategory.MISC;
   }

   public static final class Serializer implements RecipeSerializer<ToolUseRecipe> {
      public static final MapCodec<ToolUseRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               Ingredient.CODEC_NONEMPTY.fieldOf("tool").forGetter(o -> o.tool),
               Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(o -> o.input),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(o -> o.output)
            )
            .apply(inst, ToolUseRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ToolUseRecipe> STREAM_CODEC = StreamCodec.of(
         ToolUseRecipe.Serializer::toNetwork, ToolUseRecipe.Serializer::fromNetwork
      );

      public MapCodec<ToolUseRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ToolUseRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static ToolUseRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         Ingredient tool = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
         Ingredient input = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
         return new ToolUseRecipe(tool, input, (ItemStack)ItemStack.STREAM_CODEC.decode(buf));
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, ToolUseRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.tool);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
         ItemStack.STREAM_CODEC.encode(buf, recipe.output);
      }
   }
}
