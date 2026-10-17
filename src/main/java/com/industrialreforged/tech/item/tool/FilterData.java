package com.industrialreforged.tech.item.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * Содержимое фильтра: список предметов + режим (чёрный список или белый).
 * Хранится в датакомпоненте предмета.
 */
public record FilterData(List<ItemStack> items, boolean blacklist) {
   public static final FilterData EMPTY = new FilterData(List.of(), false);
   public static final Codec<FilterData> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(FilterData::items),
            Codec.BOOL.fieldOf("blacklist").forGetter(FilterData::blacklist)
         )
         .apply(inst, FilterData::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, FilterData> STREAM_CODEC = StreamCodec.composite(
      ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()),
      FilterData::items,
      ByteBufCodecs.BOOL,
      FilterData::blacklist,
      FilterData::new
   );

   public FilterData withItems(List<ItemStack> newItems) {
      return new FilterData(List.copyOf(newItems), this.blacklist);
   }

   public FilterData withBlacklist(boolean value) {
      return new FilterData(this.items, value);
   }

   public boolean contains(ItemStack stack) {
      for (ItemStack sample : this.items) {
         if (!sample.isEmpty() && ItemStack.isSameItem(sample, stack)) {
            return true;
         }
      }

      return false;
   }
}
