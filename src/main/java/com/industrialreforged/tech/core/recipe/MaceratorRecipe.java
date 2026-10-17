package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.Macerator1LevelBlockEntity;
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

public class MaceratorRecipe extends SimpleInputAndOutputRecipe {
   public MaceratorRecipe(CountIngredient ingredient, ItemStack result) {
      super(ingredient, result);
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.MACERATOR.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.MACERATOR.get();
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      Macerator1LevelBlockEntity machine = (Macerator1LevelBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.outputSlot.add(this.result.copy());
      return true;
   }

   public static class Serializer implements RecipeSerializer<MaceratorRecipe> {
      public static final MapCodec<MaceratorRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient), ItemStack.STRICT_CODEC.fieldOf("result").forGetter(o -> o.result)
            )
            .apply(inst, MaceratorRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, MaceratorRecipe> STREAM_CODEC = StreamCodec.of(
         MaceratorRecipe.Serializer::toNetwork, MaceratorRecipe.Serializer::fromNetwork
      );

      public MapCodec<MaceratorRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, MaceratorRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static MaceratorRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         return new MaceratorRecipe((CountIngredient)CountIngredient.STREAM_CODEC.decode(buf), (ItemStack)ItemStack.STREAM_CODEC.decode(buf));
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, MaceratorRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         ItemStack.STREAM_CODEC.encode(buf, recipe.result);
      }
   }
}
