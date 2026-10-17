package com.industrialreforged.tech.block;

import com.industrialreforged.block.RotateAndActiveEntityBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.api.block.BreakableBlock;
import com.industrialreforged.tech.blockentity.BrewBarrelBlockEntity;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BrewBarrelBlock extends RotateAndActiveEntityBlock<BrewBarrelBlockEntity> implements BreakableBlock {
   public BrewBarrelBlock() {
      super(Properties.of().strength(2.0F, 6.0F).sound(SoundType.WOOD));
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (player.isShiftKeyDown()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      BrewBarrelBlockEntity te = (BrewBarrelBlockEntity)this.getBlockEntity(level, pos);
      return te == null ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : te.onActivated(player, hand, hitResult.getDirection());
   }

   @Override
   public InteractionResult startBreak(Player player, Level level, InteractionHand hand, BlockPos pos, BlockState state, Direction direction) {
      BrewBarrelBlockEntity te = (BrewBarrelBlockEntity)this.getBlockEntity(level, pos);
      if (te != null) {
         return false ? InteractionResult.PASS : te.onClicked(player, direction);
      } else {
         return InteractionResult.PASS;
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BrewBarrelBlockEntity(pos, state);
   }

   public static class BrewBoozeData {
      public static final Codec<BrewBarrelBlock.BrewBoozeData> CODEC = RecordCodecBuilder.create(
         inst -> inst.group(
               Codec.INT.fieldOf("type").forGetter(o -> o.type),
               Codec.INT.fieldOf("booze").forGetter(o -> o.booze),
               Codec.INT.fieldOf("age").forGetter(o -> o.age),
               Codec.BOOL.fieldOf("opened").forGetter(o -> o.opened),
               Codec.BYTE.fieldOf("hopsCount").forGetter(o -> o.hopsCount),
               Codec.BYTE.fieldOf("wheatCount").forGetter(o -> o.wheatCount),
               Codec.BYTE.fieldOf("solid").forGetter(o -> o.solid),
               Codec.BYTE.fieldOf("hops").forGetter(o -> o.hops),
               Codec.BYTE.fieldOf("time").forGetter(o -> o.time)
            )
            .apply(inst, BrewBarrelBlock.BrewBoozeData::new)
      );
      public int type;
      public int booze;
      public int age;
      public boolean opened;
      public byte hopsCount;
      public byte wheatCount;
      public byte solid;
      public byte hops;
      public byte time;

      public BrewBoozeData(int type, int booze, int age, boolean opened, byte hopsCount, byte wheatCount, byte solid, byte hops, byte time) {
         this.type = type;
         this.booze = booze;
         this.age = age;
         this.opened = opened;
         this.hopsCount = hopsCount;
         this.wheatCount = wheatCount;
         this.solid = solid;
         this.hops = hops;
         this.time = time;
      }

      public void save(CompoundTag tag) {
         tag.putByte("type", (byte)this.type);
         tag.putByte("waterCount", (byte)this.booze);
         tag.putInt("age", this.age);
         tag.putBoolean("opened", this.opened);
         if (this.type == 1) {
            if (!this.opened) {
               tag.putByte("hopsCount", this.hopsCount);
               tag.putByte("wheatCount", this.wheatCount);
            }

            tag.putByte("solidRatio", this.solid);
            tag.putByte("hopsRatio", this.hops);
            tag.putByte("timeRatio", this.time);
         }
      }

      public void load(CompoundTag tag) {
         this.type = tag.getByte("type");
         this.booze = tag.getByte("waterCount");
         this.age = tag.getInt("age");
         this.opened = tag.getBoolean("opened");
         if (this.type == 1) {
            if (!this.opened) {
               this.hopsCount = tag.getByte("hopsCount");
               this.wheatCount = tag.getByte("wheatCount");
            }

            this.solid = tag.getByte("solidRatio");
            this.hops = tag.getByte("hopsRatio");
            this.time = tag.getByte("timeRatio");
         }
      }

      public void copy(BrewBarrelBlock.BrewBoozeData data) {
         this.type = data.type;
         this.booze = data.booze;
         this.age = data.age;
         this.opened = data.opened;
         this.hopsCount = data.hopsCount;
         this.wheatCount = data.wheatCount;
         this.solid = data.solid;
         this.hops = data.hops;
         this.time = data.time;
      }

      @Override
      public boolean equals(Object o) {
         if (o != null && this.getClass() == o.getClass()) {
            BrewBarrelBlock.BrewBoozeData data = (BrewBarrelBlock.BrewBoozeData)o;
            return this.type == data.type
               && this.booze == data.booze
               && this.age == data.age
               && this.opened == data.opened
               && this.hopsCount == data.hopsCount
               && this.wheatCount == data.wheatCount
               && this.solid == data.solid
               && this.hops == data.hops
               && this.time == data.time;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.type, this.booze, this.age, this.opened, this.hopsCount, this.wheatCount, this.solid, this.hops, this.time);
      }
   }
}
