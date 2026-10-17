package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.CentrifugeBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputAndMultiOutputRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CentrifugeRecipe extends SimpleInputAndMultiOutputRecipe {
   private final short minHeat;

   public CentrifugeRecipe(CountIngredient ingredient, NonNullList<ItemStack> result, short minHeat) {
      super(ingredient, result);
      this.minHeat = minHeat;
   }

   public short getMinHeat() {
      return this.minHeat;
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.CENTRIFUGE.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.CENTRIFUGE.get();
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      CentrifugeBlockEntity machine = (CentrifugeBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.outputSlot.add(this.result.stream().map(ItemStack::copy).collect(Collectors.toList()));
      return true;
   }

   public static class Serializer implements RecipeSerializer<CentrifugeRecipe> {
      public static final MapCodec<CentrifugeRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient),
               NonNullList.codecOf(ItemStack.STRICT_CODEC).fieldOf("result").forGetter(o -> o.result),
               Codec.SHORT.optionalFieldOf("minHeat", (short)250).forGetter(o -> o.minHeat)
            )
            .apply(inst, CentrifugeRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> STREAM_CODEC = StreamCodec.of(
         CentrifugeRecipe.Serializer::toNetwork, CentrifugeRecipe.Serializer::fromNetwork
      );

      public MapCodec<CentrifugeRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static CentrifugeRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         CountIngredient input = (CountIngredient)CountIngredient.STREAM_CODEC.decode(buf);
         NonNullList<ItemStack> outputs = NonNullList.withSize(buf.readByte(), ItemStack.EMPTY);
         outputs.replaceAll(ignored -> (ItemStack)ItemStack.STREAM_CODEC.decode(buf));
         return new CentrifugeRecipe(input, outputs, buf.readShort());
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, CentrifugeRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         buf.writeByte(recipe.result.size());

         for (ItemStack output : recipe.result) {
            ItemStack.STREAM_CODEC.encode(buf, output);
         }

         buf.writeShort(recipe.minHeat);
      }
   }
}
