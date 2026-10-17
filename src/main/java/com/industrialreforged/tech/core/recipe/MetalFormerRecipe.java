package com.industrialreforged.tech.core.recipe;

import com.industrialreforged.recipe.CountIngredient;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputAndOutputRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class MetalFormerRecipe extends SimpleInputAndOutputRecipe {
   private final MetalFormerRecipe.Mode mode;

   public MetalFormerRecipe(CountIngredient ingredient, ItemStack result, MetalFormerRecipe.Mode mode) {
      super(ingredient, result);
      this.mode = mode;
   }

   public MetalFormerRecipe.Mode getMode() {
      return this.mode;
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.METAL_FORMER.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.METAL_FORMER.get();
   }

   @Override
   public boolean matches(RecipeInput machine, Level level) {
      return machine instanceof MetalFormerRecipe.SingleRecipeInput input && input.mode == this.mode && super.matches(machine, level);
   }

   @Override
   public boolean assemble(RecipeInput input, int indexSlot) {
      MetalFormer1LevelBlockEntity machine = (MetalFormer1LevelBlockEntity)((InvSlotProcessable)input).getEntity();
      machine.inputSlot.shrink(indexSlot, this.ingredient.count());
      machine.outputSlot.add(this.result.copy());
      return true;
   }

   public enum Mode implements StringRepresentable {
      EXTRUDING,
      CUTTING,
      ROLLING;

      public String getSerializedName() {
         return this.name();
      }
   }

   public static class Serializer implements RecipeSerializer<MetalFormerRecipe> {
      public static final MapCodec<MetalFormerRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               CountIngredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient),
               ItemStack.STRICT_CODEC.fieldOf("result").forGetter(o -> o.result),
               StringRepresentable.fromEnum(MetalFormerRecipe.Mode::values).fieldOf("mode").forGetter(o -> o.mode)
            )
            .apply(inst, MetalFormerRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> STREAM_CODEC = StreamCodec.of(
         MetalFormerRecipe.Serializer::toNetwork, MetalFormerRecipe.Serializer::fromNetwork
      );

      public MapCodec<MetalFormerRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static MetalFormerRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         return new MetalFormerRecipe(
            (CountIngredient)CountIngredient.STREAM_CODEC.decode(buf),
            (ItemStack)ItemStack.STREAM_CODEC.decode(buf),
            (MetalFormerRecipe.Mode)buf.readEnum(MetalFormerRecipe.Mode.class)
         );
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, MetalFormerRecipe recipe) {
         CountIngredient.STREAM_CODEC.encode(buf, recipe.ingredient);
         ItemStack.STREAM_CODEC.encode(buf, recipe.result);
         buf.writeEnum(recipe.mode);
      }
   }

   public record SingleRecipeInput(ItemStack item, MetalFormerRecipe.Mode mode) implements RecipeInput {
      public ItemStack getItem(int index) {
         if (index != 0) {
            throw new IllegalArgumentException("No item for index " + index);
         } else {
            return this.item;
         }
      }

      public int size() {
         return 1;
      }
   }
}
