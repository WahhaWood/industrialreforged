package com.industrialreforged.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.common.NeoForge;

public class SchedulerUtils {
   private static final ConcurrentLinkedQueue<SchedulerUtils.TimerTask> TASKS = new ConcurrentLinkedQueue<>();
   private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

   private SchedulerUtils() {
   }

   public static SchedulerUtils.TimerTask runTaskTimer(Runnable task, long requiredExecute, long period) {
      SchedulerUtils.TimerTask timer = new SchedulerUtils.TimerTask(task, requiredExecute, period);
      TASKS.add(timer);
      return timer;
   }

   public static SchedulerUtils.TimerTask runTaskTimer(Runnable task, long requiredExecute, long period, TimeUnit timeUnit) {
      return runTaskTimer(task, requiredExecute, timeUnit.toSeconds(period) * 20L);
   }

   public static SchedulerUtils.TimerTask runTaskTimer(Runnable task, long period) {
      return runTaskTimer(task, -1L, period);
   }

   public static SchedulerUtils.TimerTask runTaskTimer(Runnable task, long period, TimeUnit timeUnit) {
      return runTaskTimer(task, -1L, timeUnit.toSeconds(period) * 20L);
   }

   public static SchedulerUtils.TimerTask runTaskLater(Runnable task, long delay) {
      return runTaskTimer(task, 1L, delay);
   }

   public static SchedulerUtils.TimerTask runTaskLater(Runnable task, long delay, TimeUnit timeUnit) {
      return runTaskTimer(task, 1L, timeUnit.toSeconds(delay) * 20L);
   }

   public static List<SchedulerUtils.TimerTask> getActiveTasks() {
      return new ArrayList<>(TASKS);
   }

   @SubscribeEvent
   public void onClientTick(Pre event) {
      Iterator<SchedulerUtils.TimerTask> iterator = TASKS.iterator();

      while (iterator.hasNext()) {
         SchedulerUtils.TimerTask task = iterator.next();
         if (!task.alive || task.requiredExecute != -1L && task.executed >= task.requiredExecute) {
            iterator.remove();
         } else {
            task.tick();
         }
      }
   }

   public static ScheduledFuture<?> runAsyncTaskTimer(Runnable task, long initialDelay, long period, TimeUnit timeUnit) {
      return SCHEDULER.scheduleAtFixedRate(task, initialDelay, period, timeUnit);
   }

   public static ScheduledFuture<?> runAsyncTaskLater(Runnable task, long delay, TimeUnit timeUnit) {
      return SCHEDULER.schedule(task, delay, timeUnit);
   }

   static {
      NeoForge.EVENT_BUS.register(new SchedulerUtils());
   }

   public static class TimerTask {
      private final long period;
      private final long requiredExecute;
      private final Runnable task;
      private long ticks;
      private long executed;
      private boolean alive = true;

      private TimerTask(Runnable task, long requiredExecute, long period) {
         this.requiredExecute = requiredExecute;
         this.period = period;
         this.task = task;
      }

      private void tick() {
         this.ticks++;
         if (this.ticks >= this.period) {
            this.ticks = 0L;

            try {
               this.task.run();
            } catch (Exception exception) {
               exception.printStackTrace();
            }

            this.executed++;
         }
      }

      public long getExecutedCount() {
         return this.executed;
      }

      public long getPeriod() {
         return this.period;
      }

      public long getRequiredExecute() {
         return this.requiredExecute;
      }

      public void cancel() {
         this.alive = false;
      }
   }
}
