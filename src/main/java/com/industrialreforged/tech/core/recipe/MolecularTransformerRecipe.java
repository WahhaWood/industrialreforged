package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputAndOutputRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class MolecularTransformerRecipe extends SimpleInputAndOutputRecipe {
   public long energy;

   public MolecularTransformerRecipe(CountIngredient input, ItemStack output, long energy) {
      super(input, output);
      this.energy = energy;
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.MOLECULAR_TRANSFORMER.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.MOLECULAR_TRANSFORMER.get();
   }

   @Override
   public boolean assemble(RecipeInput machine, int indexSlot) {
      return false;
   }

   public static final class Serializer implements RecipeSerializer<MolecularTransformerRecipe> {
      public static final MapCodec<MolecularTransformerRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("input").forGetter(o -> o.ingredient),
               ItemStack.STRICT_CODEC.fieldOf("output").forGetter(o -> o.result),
               Codec.LONG.fieldOf("energy").forGetter(o -> o.energy)
            )
            .apply(inst, MolecularTransformerRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, MolecularTransformerRecipe> STREAM_CODEC = StreamCodec.of(
         MolecularTransformerRecipe.Serializer::toNetwork, MolecularTransformerRecipe.Serializer::fromNetwork
      );

      public MapCodec<MolecularTransformerRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, MolecularTransformerRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static MolecularTransformerRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         return new MolecularTransformerRecipe(
            (CountIngredient)CountIngredient.STREAM_CODEC.decode(buf), (ItemStack)ItemStack.STREAM_CODEC.decode(buf), buf.readVarLong()
         );
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, MolecularTransformerRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         ItemStack.STREAM_CODEC.encode(buf, recipe.result);
         buf.writeVarLong(recipe.energy);
      }
   }
}
