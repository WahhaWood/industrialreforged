package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.Compressor1LevelBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputAndOutputRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CompressorRecipe extends SimpleInputAndOutputRecipe {
   public CompressorRecipe(CountIngredient ingredient, ItemStack result) {
      super(ingredient, result);
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.COMPRESSOR.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.COMPRESSOR.get();
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      Compressor1LevelBlockEntity machine = (Compressor1LevelBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.outputSlot.add(this.result.copy());
      return true;
   }

   public static class Serializer implements RecipeSerializer<CompressorRecipe> {
      public static final MapCodec<CompressorRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient), ItemStack.STRICT_CODEC.fieldOf("result").forGetter(o -> o.result)
            )
            .apply(inst, CompressorRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CompressorRecipe> STREAM_CODEC = StreamCodec.of(
         CompressorRecipe.Serializer::toNetwork, CompressorRecipe.Serializer::fromNetwork
      );

      public MapCodec<CompressorRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CompressorRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static CompressorRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         return new CompressorRecipe((CountIngredient)CountIngredient.STREAM_CODEC.decode(buf), (ItemStack)ItemStack.STREAM_CODEC.decode(buf));
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, CompressorRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         ItemStack.STREAM_CODEC.encode(buf, recipe.result);
      }
   }
}
