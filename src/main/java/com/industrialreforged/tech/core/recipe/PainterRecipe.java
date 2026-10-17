package com.industrialreforged.tech.core.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import com.industrialreforged.tech.item.tool.ItemToolPainter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PainterRecipe extends CustomRecipe {
   public PainterRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput input, Level world) {
      boolean hasPainter = false;
      boolean hasDye = false;

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() == IRItems.PAINTER.get()) {
               hasPainter = true;
            } else {
               if (!(stack.getItem() instanceof DyeItem)) {
                  return false;
               }

               hasDye = true;
            }
         }
      }

      return hasPainter && hasDye;
   }

   public ItemStack assemble(CraftingInput input, Provider registries) {
      DyeColor color = DyeColor.WHITE;

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (stack.getItem() instanceof DyeItem dyeItem) {
            color = dyeItem.getDyeColor();
         }
      }

      ItemStack result = IRItems.PAINTER.toStack();
      ItemToolPainter.setColor(result, color);
      return result;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.PAINTER_RECIPE.get();
   }

   public static final class Serializer implements RecipeSerializer<PainterRecipe> {
      public static final MapCodec<PainterRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(CraftingBookCategory.CODEC.fieldOf("category").forGetter(CustomRecipe::category)).apply(inst, PainterRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, PainterRecipe> STREAM_CODEC = StreamCodec.composite(
         CraftingBookCategory.STREAM_CODEC, CustomRecipe::category, PainterRecipe::new
      );

      public MapCodec<PainterRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, PainterRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
