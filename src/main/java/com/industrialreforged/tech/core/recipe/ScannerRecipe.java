package com.industrialreforged.tech.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ScannerRecipe implements Recipe<RecipeInput> {

   @Override
   public boolean isSpecial() {
      return true;
   }

   private final ItemStack item;
   private final int cost;

   public ScannerRecipe(ItemStack item, int cost) {
      this.item = item;
      this.cost = cost;
   }

   public ItemStack getItem() {
      return this.item;
   }

   public int getCost() {
      return this.cost;
   }

   public boolean matches(ItemStack stack) {
      return ItemStack.isSameItem(this.item, stack);
   }

   public boolean matches(RecipeInput input, Level level) {
      return this.matches(input.getItem(0));
   }

   public ItemStack assemble(RecipeInput input, Provider registries) {
      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int w, int h) {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.item.copy();
   }

   public RecipeSerializer<?> getSerializer() {
      return IRRecipeSerializers.SCANNER.get();
   }

   public RecipeType<?> getType() {
      return IRRecipeTypes.SCANNER.get();
   }

   public static class Serializer implements RecipeSerializer<ScannerRecipe> {
      public final MapCodec<ScannerRecipe> codec = RecordCodecBuilder.mapCodec(
         inst -> inst.group(ItemStack.CODEC.fieldOf("item").forGetter(ScannerRecipe::getItem), Codec.INT.fieldOf("cost").forGetter(ScannerRecipe::getCost))
            .apply(inst, ScannerRecipe::new)
      );
      public final StreamCodec<RegistryFriendlyByteBuf, ScannerRecipe> streamCodec = StreamCodec.composite(
         ItemStack.STREAM_CODEC, ScannerRecipe::getItem, ByteBufCodecs.VAR_INT, ScannerRecipe::getCost, ScannerRecipe::new
      );

      public MapCodec<ScannerRecipe> codec() {
         return this.codec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ScannerRecipe> streamCodec() {
         return this.streamCodec;
      }
   }
}
