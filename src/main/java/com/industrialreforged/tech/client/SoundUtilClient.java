package com.industrialreforged.tech.client;

import com.industrialreforged.tech.core.util.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/** Client-side implementation of the {@link SoundUtil} handler. */
public final class SoundUtilClient implements SoundUtil.Handler {
   private static SoundManager manager() {
      return Minecraft.getInstance().getSoundManager();
   }

   @Override
   public Object playSound(Object sound) {
      if (sound != null) {
         manager().play((SoundInstance)sound);
      }

      return sound;
   }

   @Override
   public Object play(SoundEvent sound, SoundSource source, float volume, float pitch, boolean looping) {
      return playSound(
         new SimpleSoundInstance(
            sound.getLocation(), source, volume, pitch, SoundInstance.createUnseededRandom(), looping, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true
         )
      );
   }

   @Override
   public Object play(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old) {
      SoundManager manager = manager();
      if (old != null) {
         manager.stop((SoundInstance)old);
      }

      if (sound == null) {
         return null;
      }

      return playSound(new BlockSoundInstance(sound, entity));
   }

   @Override
   public Object playLoop(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old) {
      SoundManager manager = manager();
      if (old != null) {
         manager.stop((SoundInstance)old);
      }

      if (sound == null) {
         return null;
      }

      return playSound(new LoopBlockSoundInstance(sound, entity));
   }

   @Override
   public Object loop(BlockEntity entity, @Nullable SoundEvent sound) {
      return sound == null ? null : playSound(new LoopBlockSoundInstance(sound, entity));
   }

   @Override
   public Object stop(Object sound) {
      if (sound != null) {
         manager().stop((SoundInstance)sound);
      }

      return sound;
   }

   @Override
   public boolean isStopped(Object sound) {
      return sound == null || !manager().isActive((SoundInstance)sound);
   }

   @Override
   public boolean isActive(Object sound) {
      return sound != null && manager().isActive((SoundInstance)sound);
   }
}
