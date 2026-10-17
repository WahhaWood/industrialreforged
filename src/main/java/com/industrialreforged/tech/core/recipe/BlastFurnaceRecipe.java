package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.BlastFurnaceBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputAndMultiOutputRecipe;
import com.industrialreforged.tech.core.ref.IRItems;
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
import net.minecraft.world.level.ItemLike;

public class BlastFurnaceRecipe extends SimpleInputAndMultiOutputRecipe {
   private final int cells;
   private final int duration;

   public BlastFurnaceRecipe(CountIngredient ingredient, NonNullList<ItemStack> result, int cells, int duration) {
      super(ingredient, result);
      this.cells = cells;
      this.duration = duration;
   }

   public int getCells() {
      return this.cells;
   }

   public int getDuration() {
      return this.duration;
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.BLAST_FURNACE.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.BLAST_FURNACE.get();
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      BlastFurnaceBlockEntity machine = (BlastFurnaceBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.tankInputSlot.shrink(indexSlot, this.cells);
      machine.tankOutputSlot.add(new ItemStack((ItemLike)IRItems.CELL.get(), this.cells));
      machine.outputSlot.add(this.result.stream().map(ItemStack::copy).collect(Collectors.toList()));
      return true;
   }

   public static class Serializer implements RecipeSerializer<BlastFurnaceRecipe> {
      public static final MapCodec<BlastFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient),
               NonNullList.codecOf(ItemStack.STRICT_CODEC).fieldOf("result").forGetter(o -> o.result),
               Codec.INT.optionalFieldOf("cells", 1).forGetter(o -> o.cells),
               Codec.INT.optionalFieldOf("duration", 100).forGetter(o -> o.duration)
            )
            .apply(inst, BlastFurnaceRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, BlastFurnaceRecipe> STREAM_CODEC = StreamCodec.of(
         BlastFurnaceRecipe.Serializer::toNetwork, BlastFurnaceRecipe.Serializer::fromNetwork
      );

      public MapCodec<BlastFurnaceRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, BlastFurnaceRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static BlastFurnaceRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         CountIngredient input = (CountIngredient)CountIngredient.STREAM_CODEC.decode(buf);
         NonNullList<ItemStack> outputs = NonNullList.withSize(buf.readByte(), ItemStack.EMPTY);
         outputs.replaceAll(ignored -> (ItemStack)ItemStack.STREAM_CODEC.decode(buf));
         return new BlastFurnaceRecipe(input, outputs, buf.readVarInt(), buf.readVarInt());
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, BlastFurnaceRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         buf.writeByte(recipe.result.size());

         for (ItemStack output : recipe.result) {
            ItemStack.STREAM_CODEC.encode(buf, output);
         }

         buf.writeVarInt(recipe.cells);
         buf.writeVarInt(recipe.duration);
      }
   }
}
