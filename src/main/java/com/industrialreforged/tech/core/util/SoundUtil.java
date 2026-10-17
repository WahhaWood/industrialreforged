package com.industrialreforged.tech.core.util;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Common façade for the machine/item sounds.
 *
 * <p>The real playback lives in {@code tech.client.SoundUtilClient}; on a
 * dedicated server no handler is installed and every call becomes a no-op.
 * Handles are plain {@link Object}s so that no client-only class is ever
 * referenced from common code.
 */
public final class SoundUtil {
   public interface Handler {
      Object playSound(Object sound);

      Object play(SoundEvent sound, SoundSource source, float volume, float pitch, boolean looping);

      Object play(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old);

      Object playLoop(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old);

      Object loop(BlockEntity entity, @Nullable SoundEvent sound);

      Object stop(Object sound);

      boolean isStopped(Object sound);

      boolean isActive(Object sound);
   }

   private static final Handler NOOP = new Handler() {
      @Override
      public Object playSound(Object sound) {
         return sound;
      }

      @Override
      public Object play(SoundEvent sound, SoundSource source, float volume, float pitch, boolean looping) {
         return null;
      }

      @Override
      public Object play(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old) {
         return null;
      }

      @Override
      public Object playLoop(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old) {
         return null;
      }

      @Override
      public Object loop(BlockEntity entity, @Nullable SoundEvent sound) {
         return null;
      }

      @Override
      public Object stop(Object sound) {
         return sound;
      }

      @Override
      public boolean isStopped(Object sound) {
         return true;
      }

      @Override
      public boolean isActive(Object sound) {
         return false;
      }
   };

   private static Handler handler = NOOP;

   private SoundUtil() {
   }

   public static void setHandler(Handler newHandler) {
      handler = newHandler == null ? NOOP : newHandler;
   }

   public static Object playSound(Object sound) {
      return handler.playSound(sound);
   }

   public static Object play(SoundEvent sound, float volume, float pitch) {
      return play(sound, volume, pitch, false);
   }

   public static Object play(SoundEvent sound, SoundSource source, float volume, float pitch) {
      return play(sound, source, volume, pitch, false);
   }

   public static Object play(SoundEvent sound, float volume, float pitch, boolean looping) {
      return play(sound, SoundSource.MASTER, volume, pitch, looping);
   }

   public static Object play(SoundEvent sound, SoundSource source, float volume, float pitch, boolean looping) {
      return handler.play(sound, source, volume, pitch, looping);
   }

   public static Object play(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old) {
      return handler.play(entity, sound, old);
   }

   public static Object playLoop(BlockEntity entity, @Nullable SoundEvent sound, @Nullable Object old) {
      return handler.playLoop(entity, sound, old);
   }

   /** Creates and starts a looping sound bound to the given block entity. */
   public static Object loop(BlockEntity entity, @Nullable SoundEvent sound) {
      return handler.loop(entity, sound);
   }

   public static Object stop(Object sound) {
      return handler.stop(sound);
   }

   public static boolean isStopped(Object sound) {
      return handler.isStopped(sound);
   }

   public static boolean isActive(Object sound) {
      return handler.isActive(sound);
   }

   public static Object play(Supplier<SoundEvent> supplier, float volume, float pitch) {
      return play(supplier, volume, pitch, false);
   }

   public static Object play(Supplier<SoundEvent> supplier, SoundSource source, float volume, float pitch) {
      return play(supplier, source, volume, pitch, false);
   }

   public static Object play(Supplier<SoundEvent> supplier, float volume, float pitch, boolean looping) {
      return play(supplier, SoundSource.MASTER, volume, pitch, looping);
   }

   public static Object play(Supplier<SoundEvent> supplier, SoundSource source, float volume, float pitch, boolean looping) {
      return play(supplier.get(), source, volume, pitch, looping);
   }

   public static Object play(BlockEntity entity, Supplier<SoundEvent> supplier, @Nullable Object old) {
      return play(entity, supplier.get(), old);
   }

   public static Object playLoop(BlockEntity entity, Supplier<SoundEvent> supplier, @Nullable Object old) {
      return playLoop(entity, supplier.get(), old);
   }

   public static Object loop(BlockEntity entity, Supplier<SoundEvent> supplier) {
      return loop(entity, supplier.get());
   }

   public enum SoundState {
      IDLE,
      RUNNING,
      INTERRUPTED;
   }
}
