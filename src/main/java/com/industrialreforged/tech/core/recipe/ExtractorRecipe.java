package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.Extractor1LevelBlockEntity;
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

public class ExtractorRecipe extends SimpleInputAndOutputRecipe {
   public ExtractorRecipe(CountIngredient ingredient, ItemStack result) {
      super(ingredient, result);
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.EXTRACTOR.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.EXTRACTOR.get();
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      Extractor1LevelBlockEntity machine = (Extractor1LevelBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.outputSlot.add(this.result.copy());
      return true;
   }

   public static class Serializer implements RecipeSerializer<ExtractorRecipe> {
      public static final MapCodec<ExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient), ItemStack.STRICT_CODEC.fieldOf("result").forGetter(o -> o.result)
            )
            .apply(inst, ExtractorRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> STREAM_CODEC = StreamCodec.of(
         ExtractorRecipe.Serializer::toNetwork, ExtractorRecipe.Serializer::fromNetwork
      );

      public MapCodec<ExtractorRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static ExtractorRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         return new ExtractorRecipe((CountIngredient)CountIngredient.STREAM_CODEC.decode(buf), (ItemStack)ItemStack.STREAM_CODEC.decode(buf));
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, ExtractorRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         ItemStack.STREAM_CODEC.encode(buf, recipe.result);
      }
   }
}
