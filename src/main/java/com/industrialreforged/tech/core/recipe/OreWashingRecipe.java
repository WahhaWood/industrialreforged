package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class OreWashingRecipe extends SimpleInputAndMultiOutputRecipe {
   private final int amount;

   public OreWashingRecipe(CountIngredient ingredient, NonNullList<ItemStack> result, int amount) {
      super(ingredient, result);
      this.amount = amount;
   }

   public int getAmount() {
      return this.amount;
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.ORE_WASHER.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.ORE_WASHER.get();
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      OreWashingPlantBlockEntity machine = (OreWashingPlantBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.fluidTank.drainForce(this.amount, FluidAction.EXECUTE);
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.outputSlot.add(this.result.stream().map(ItemStack::copy).collect(Collectors.toList()));
      return true;
   }

   public static class Serializer implements RecipeSerializer<OreWashingRecipe> {
      public static final MapCodec<OreWashingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient),
               NonNullList.codecOf(ItemStack.STRICT_CODEC).fieldOf("result").forGetter(o -> o.result),
               Codec.INT.optionalFieldOf("amount", 1000).forGetter(o -> o.amount)
            )
            .apply(inst, OreWashingRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, OreWashingRecipe> STREAM_CODEC = StreamCodec.of(
         OreWashingRecipe.Serializer::toNetwork, OreWashingRecipe.Serializer::fromNetwork
      );

      public MapCodec<OreWashingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, OreWashingRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static OreWashingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         CountIngredient input = (CountIngredient)CountIngredient.STREAM_CODEC.decode(buf);
         NonNullList<ItemStack> outputs = NonNullList.withSize(buf.readByte(), ItemStack.EMPTY);
         outputs.replaceAll(ignored -> (ItemStack)ItemStack.STREAM_CODEC.decode(buf));
         return new OreWashingRecipe(input, outputs, buf.readVarInt());
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, OreWashingRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         buf.writeByte(recipe.result.size());

         for (ItemStack output : recipe.result) {
            ItemStack.STREAM_CODEC.encode(buf, output);
         }

         buf.writeVarInt(recipe.amount);
      }
   }
}
