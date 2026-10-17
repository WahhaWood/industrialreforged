package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.BlockCutterBlockEntity;
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

public class BlockCutterRecipe extends SimpleInputAndOutputRecipe {
   public final int hardness;

   public BlockCutterRecipe(CountIngredient ingredient, ItemStack result, int hardness) {
      super(ingredient, result);
      this.hardness = hardness;
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.BLOCK_CUTTER.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.BLOCK_CUTTER.get();
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      BlockCutterBlockEntity machine = (BlockCutterBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.outputSlot.add(this.result.copy());
      return true;
   }

   public static class Serializer implements RecipeSerializer<BlockCutterRecipe> {
      public static final MapCodec<BlockCutterRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(o -> o.result),
               Codec.INT.optionalFieldOf("hardness", 1).forGetter(o -> o.hardness)
            )
            .apply(inst, BlockCutterRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, BlockCutterRecipe> STREAM_CODEC = StreamCodec.of(
         BlockCutterRecipe.Serializer::toNetwork, BlockCutterRecipe.Serializer::fromNetwork
      );

      public MapCodec<BlockCutterRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, BlockCutterRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static BlockCutterRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         return new BlockCutterRecipe(
            (CountIngredient)CountIngredient.STREAM_CODEC.decode(buf), (ItemStack)ItemStack.STREAM_CODEC.decode(buf), buf.readVarInt()
         );
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, BlockCutterRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         ItemStack.STREAM_CODEC.encode(buf, recipe.result);
         buf.writeVarInt(recipe.hardness);
      }
   }
}
