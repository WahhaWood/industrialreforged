package com.industrialreforged.tech.client;

import java.util.function.Supplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class BlockSoundInstance extends AbstractTickableSoundInstance {
   @Nullable
   private final BlockEntity source;
   private boolean beginFadeOut;
   private int ticks = 0;
   private int fadeIn = 50;
   private int fadeOut = 50;
   private boolean condition = true;

   public BlockSoundInstance(Supplier<SoundEvent> supplier, BlockEntity entity) {
      this(supplier.get(), entity);
   }

   public BlockSoundInstance(Supplier<SoundEvent> supplier, BlockPos pos) {
      this(supplier.get(), pos);
   }

   public BlockSoundInstance(SoundEvent event, BlockEntity entity) {
      this(event, entity.getBlockPos(), entity);
   }

   public BlockSoundInstance(SoundEvent event, BlockPos pos) {
      this(event, pos, null);
   }

   private BlockSoundInstance(SoundEvent event, BlockPos pos, @Nullable BlockEntity source) {
      super(event, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
      this.source = source;
      this.x = pos.getX() + 0.5;
      this.y = pos.getY() + 0.5;
      this.z = pos.getZ() + 0.5;
   }

   public BlockSoundInstance setCondition(boolean value) {
      this.condition = value;
      return this;
   }

   public BlockSoundInstance setFadeIn(int fadeIn) {
      this.fadeIn = Math.min(0, fadeIn);
      return this;
   }

   public BlockSoundInstance setFadeOut(int fadeOut) {
      this.fadeOut = Math.min(0, fadeOut);
      return this;
   }

   public float getFadeInMultiplier() {
      return this.ticks >= this.fadeIn ? 1.0F : (float)this.ticks / this.fadeIn;
   }

   public float getFadeOutMultiplier() {
      return this.ticks >= this.fadeOut ? 0.0F : (float)(this.fadeOut - this.ticks) / this.fadeOut;
   }

   public void tick() {
      if (this.source == null
         || !this.source.isRemoved() && this.source.getLevel() != null && this.source.getLevel().getBlockEntity(this.source.getBlockPos()) == this.source) {
         if (!this.beginFadeOut) {
            if (this.ticks < this.fadeIn) {
               this.ticks++;
            }

            if (!this.condition) {
               this.beginFadeOut = true;
               this.ticks = 0;
            }
         } else {
            this.ticks++;
         }

         float multiplier = this.beginFadeOut ? this.getFadeOutMultiplier() : this.getFadeInMultiplier();
         this.volume = multiplier;
         if (multiplier <= 0.0F) {
            this.off();
         }
      } else {
         this.stop();
      }
   }

   protected void off() {
      this.stop();
   }
}
