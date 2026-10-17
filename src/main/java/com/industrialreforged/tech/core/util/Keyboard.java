package com.industrialreforged.tech.core.util;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class Keyboard {
   private final Map<Player, Set<Keyboard.Key>> playerKeys = new WeakHashMap<>();

   public boolean isAltKeyDown(Player player) {
      return this.get(player, Keyboard.Key.alt);
   }

   public boolean isBoostKeyDown(Player player) {
      return this.get(player, Keyboard.Key.boost);
   }

   public boolean isForwardKeyDown(Player player) {
      return this.get(player, Keyboard.Key.forward);
   }

   public boolean isJumpKeyDown(Player player) {
      return this.get(player, Keyboard.Key.jump);
   }

   public boolean isModeSwitchKeyDown(Player player) {
      return this.get(player, Keyboard.Key.modeSwitch);
   }

   public boolean isFlyKeyDown(Player player) {
      return this.get(player, Keyboard.Key.fly);
   }

   public Component getAltKeyName() {
      return Component.translatable("key.industrialreforged.alt");
   }

   public Component getBoostKeyName() {
      return Component.translatable("key.industrialreforged.boost");
   }

   public Component getForwardKeyName() {
      return Component.literal("Up");
   }

   public Component getJumpKeyName() {
      return Component.literal("Space");
   }

   public Component getModeSwitchKeyName() {
      return Component.translatable("key.industrialreforged.switch");
   }

   public Component getFlyKeyName() {
      return Component.translatable("key.industrialreforged.fly");
   }

   public void sendKeyUpdate() {
   }

   public void processKeyUpdate(Player player, int n) {
      this.playerKeys.put(player, Keyboard.Key.fromInt(n));
   }

   public void removePlayerReferences(Player player) {
      this.playerKeys.remove(player);
   }

   private boolean get(Player player, Keyboard.Key key) {
      Set<Keyboard.Key> set = this.playerKeys.get(player);
      return set == null ? false : set.contains(key);
   }

   protected enum Key {
      alt,
      boost,
      forward,
      modeSwitch,
      fly,
      jump;

      public static int toInt(Iterable<Keyboard.Key> iterable) {
         int n = 0;

         for (Keyboard.Key key : iterable) {
 n |= 1 << key.ordinal();
         }

         return n;
      }

      public static Set<Keyboard.Key> fromInt(int n) {
         EnumSet<Keyboard.Key> enumSet = EnumSet.noneOf(Keyboard.Key.class);
         int i = 0;

         while (n != 0 && i < values().length) {
            if ((n & 1) != 0) {
               enumSet.add(values()[i]);
            }

            i++;
 n >>= 1;
         }

         return enumSet;
      }
   }
}
