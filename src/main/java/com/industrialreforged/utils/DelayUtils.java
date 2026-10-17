package com.industrialreforged.utils;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMaps;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.concurrent.TimeUnit;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.common.NeoForge;

public class DelayUtils {
   private static final DelayUtils INSTANCE = new DelayUtils();
   private final Int2LongMap deadlines = Int2LongMaps.synchronize(new Int2LongOpenHashMap());

   private DelayUtils() {
      NeoForge.EVENT_BUS.register(this);
   }

   private boolean isActive(int key) {
      return this.deadlines.containsKey(key);
   }

   private void schedule(int key, long time) {
      this.deadlines.put(key, time);
   }

   private long deadline(int key) {
      return this.deadlines.get(key);
   }

   private void clear(int key) {
      this.deadlines.remove(key);
   }

   @SubscribeEvent
   public void onClientTick(Pre event) {
      long time = System.currentTimeMillis();
      ObjectIterator<Entry> iterator = Int2LongMaps.fastIterator(this.deadlines);

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         if (entry.getLongValue() < time) {
            iterator.remove();
         }
      }
   }

   public static boolean has(String key) {
      return INSTANCE.isActive(key.hashCode());
   }

   public static boolean has(int key) {
      return INSTANCE.isActive(key);
   }

   public static void push(String key, TimeUnit unit, int time) {
      push(key.hashCode(), unit, time);
   }

   public static void push(int key, TimeUnit unit, int time) {
      INSTANCE.schedule(key, System.currentTimeMillis() + unit.toMillis(time));
   }

   public static long get(String key) {
      return INSTANCE.deadline(key.hashCode());
   }

   public static long get(int key) {
      return INSTANCE.deadline(key);
   }

   public static void remove(String key) {
      INSTANCE.clear(key.hashCode());
   }

   public static void remove(int key) {
      INSTANCE.clear(key);
   }
}
