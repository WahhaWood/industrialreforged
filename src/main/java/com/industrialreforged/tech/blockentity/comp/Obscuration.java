package com.industrialreforged.tech.blockentity.comp;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.item.tool.ItemObscurator;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Obscuration {
   private final Runnable changeHandler;
   private Obscuration.ObscurationData[] dataMap;

   public Obscuration(Runnable changeHandler) {
      this.changeHandler = changeHandler;
   }

   public void load(CompoundTag compoundTag) {
      if (!compoundTag.isEmpty()) {
         for (Direction direction : Util.ALL_DIRS) {
            if (compoundTag.contains(direction.getSerializedName(), 10)) {
               CompoundTag compound = compoundTag.getCompound(direction.getSerializedName());
               DataResult<Pair<Obscuration.ObscurationData, Tag>> dataResult = Obscuration.ObscurationData.CODEC.decode(NbtOps.INSTANCE, compound);
               if (dataResult.isSuccess()) {
                  if (this.dataMap == null) {
                     this.dataMap = new Obscuration.ObscurationData[Util.ALL_DIRS.length];
                  }

                  this.dataMap[direction.ordinal()] = (Obscuration.ObscurationData)dataResult.map(Pair::getFirst).getOrThrow();
               }
            }
         }
      }
   }

   public CompoundTag save() {
      if (this.dataMap == null) {
         return null;
      }

      CompoundTag compoundTag = new CompoundTag();

      for (Direction direction : Util.ALL_DIRS) {
         Obscuration.ObscurationData obscurationData = this.dataMap[direction.ordinal()];
         if (obscurationData != null) {
            compoundTag.put(direction.getSerializedName(), (Tag)Obscuration.ObscurationData.CODEC.encodeStart(NbtOps.INSTANCE, obscurationData).getOrThrow());
         }
      }

      return compoundTag;
   }

   public boolean applyObscuration(Direction direction, Obscuration.ObscurationData obscurationData) {
      if (this.dataMap != null && obscurationData.equals(this.dataMap[direction.ordinal()])) {
         return false;
      }

      if (this.dataMap == null) {
         this.dataMap = new Obscuration.ObscurationData[Util.ALL_DIRS.length];
      }

      this.dataMap[direction.ordinal()] = obscurationData;
      this.changeHandler.run();
      return true;
   }

   public void clear() {
      this.dataMap = null;
      this.changeHandler.run();
   }

   public boolean hasObscuration() {
      return this.dataMap != null;
   }

   public Obscuration.ObscurationData[] getRenderState() {
      return this.dataMap == null ? null : Arrays.copyOf(this.dataMap, this.dataMap.length);
   }

   public record ObscurationData(BlockState state, Direction side, int[] colorMultipliers) {
      public static final Codec<Obscuration.ObscurationData> CODEC = RecordCodecBuilder.create(
         inst -> inst.group(
               BlockState.CODEC.fieldOf("block_state").forGetter(Obscuration.ObscurationData::state),
               Direction.CODEC.fieldOf("side").forGetter(Obscuration.ObscurationData::side),
               Codec.INT_STREAM
                  .xmap(intStream -> ItemObscurator.internColorMultipliers(intStream.toArray()), IntStream::of)
                  .fieldOf("colors")
                  .forGetter(Obscuration.ObscurationData::colorMultipliers)
            )
            .apply(inst, Obscuration.ObscurationData::new)
      );
      public static final Obscuration.ObscurationData EMPTY = new Obscuration.ObscurationData(
         Blocks.AIR.defaultBlockState(), Direction.NORTH, new int[]{16777215}
      );

      public boolean isEmpty() {
         return this == EMPTY || this.state.isAir();
      }

      @Override
      public boolean equals(Object o) {
         if (o != null && this.getClass() == o.getClass()) {
            Obscuration.ObscurationData that = (Obscuration.ObscurationData)o;
            return this.side == that.side && Objects.equals(this.state, that.state) && Objects.deepEquals(this.colorMultipliers, that.colorMultipliers);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return (this.state.hashCode() * 7 + this.side.ordinal()) * 23;
      }
   }
}
