package com.industrialreforged.debug.bridge;

import com.google.gson.JsonObject;
import java.util.Arrays;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Dev bridge {@code /perf}: server tick duration from the first {@code ServerTickEvent.Pre} listener to the last
 * {@code ServerTickEvent.Post} one, so AE2's grid work (level end tick and server end tick) is inside.
 * Collected since the last reset; the bridge thread reads it, the server thread writes it.
 */
final class TickTimer {
   private static final int MAX_SAMPLES = 72_000;

   private static long tickStart;
   private static long[] samples = new long[MAX_SAMPLES];
   private static int count;

   private TickTimer() {
   }

   static void register() {
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, (ServerTickEvent.Pre event) -> tickStart = System.nanoTime());
      NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ServerTickEvent.Post event) -> record(System.nanoTime() - tickStart));
   }

   private static synchronized void record(long nanos) {
      if (count < MAX_SAMPLES) {
         samples[count++] = nanos;
      }
   }

   static synchronized JsonObject report(boolean reset) {
      JsonObject json = new JsonObject();
      long[] sorted = Arrays.copyOf(samples, count);
      Arrays.sort(sorted);
      long sum = 0;
      for (long nanos : sorted) {
         sum += nanos;
      }

      json.addProperty("ticks", count);
      json.addProperty("avgMs", count == 0 ? 0 : sum / 1e6 / count);
      json.addProperty("p50Ms", count == 0 ? 0 : sorted[count / 2] / 1e6);
      json.addProperty("p95Ms", count == 0 ? 0 : sorted[Math.min(count - 1, count * 95 / 100)] / 1e6);
      json.addProperty("maxMs", count == 0 ? 0 : sorted[count - 1] / 1e6);
      json.addProperty("totalMs", sum / 1e6);
      if (reset) {
         count = 0;
      }

      return json;
   }
}
