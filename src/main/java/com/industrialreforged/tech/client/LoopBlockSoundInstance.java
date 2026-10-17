package com.industrialreforged.tech.client;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LoopBlockSoundInstance extends BlockSoundInstance {
   public LoopBlockSoundInstance(Supplier<SoundEvent> supplier, BlockEntity entity) {
      this(supplier.get(), entity);
   }

   public LoopBlockSoundInstance(Supplier<SoundEvent> supplier, BlockPos pos) {
      this(supplier.get(), pos);
   }

   public LoopBlockSoundInstance(SoundEvent event, BlockEntity entity) {
      super(event, entity);
      this.looping = true;
   }

   public LoopBlockSoundInstance(SoundEvent event, BlockPos pos) {
      super(event, pos);
      this.looping = true;
   }

   @Override
   protected void off() {
      super.off();
      this.looping = true;
   }
}
