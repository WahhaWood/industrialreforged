package com.industrialreforged.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@Deprecated(forRemoval = true)
public record CountIngredient(Ingredient ingredient, int count) {
   public static final Codec<CountIngredient> CODEC = Codec.of(
      new Encoder<CountIngredient>() {
         public <E> DataResult<E> encode(CountIngredient input, DynamicOps<E> ops, E prefix) {
            return Ingredient.CODEC_NONEMPTY
               .encodeStart(ops, input.ingredient())
               .flatMap(
                  ingredientValue -> input.count() > 1
                     ? ops.mergeToMap(ingredientValue, ops.createString("count"), ops.createInt(input.count()))
                     : DataResult.success(ingredientValue)
               );
         }
      },
      new Decoder<CountIngredient>() {
         public <E> DataResult<Pair<CountIngredient, E>> decode(DynamicOps<E> ops, E input) {
            return ops.getMap(input).flatMap(map -> {
               Ingredient ingredient = (Ingredient)Ingredient.CODEC_NONEMPTY.parse(ops, input).getOrThrow();
               int count = 1;
               E countValue = (E)map.get("count");
               if (countValue != null) {
                  count = Codec.INT.parse(ops, countValue).result().orElse(1);
               }

               return DataResult.success(Pair.of(new CountIngredient(ingredient, count), input));
            });
         }
      }
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, CountIngredient> STREAM_CODEC = StreamCodec.composite(
      Ingredient.CONTENTS_STREAM_CODEC, CountIngredient::ingredient, ByteBufCodecs.VAR_INT, CountIngredient::count, CountIngredient::new
   );

   public boolean test(ItemStack stack) {
      return this.ingredient.test(stack) && this.count <= stack.getCount();
   }

   public List<ItemStack> toList() {
      return this.count == 1
         ? Arrays.asList(this.ingredient.getItems())
         : Arrays.stream(this.ingredient.getItems()).map(stack -> stack.copyWithCount(this.count)).toList();
   }

   public ItemStack toFirstStack() {
      ItemStack[] stacks = this.ingredient.getItems();
      return stacks.length == 0 ? ItemStack.EMPTY : stacks[0].copyWithCount(this.count);
   }
}
