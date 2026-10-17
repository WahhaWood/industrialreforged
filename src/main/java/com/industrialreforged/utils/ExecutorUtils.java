package com.industrialreforged.utils;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.ReportType;
import net.minecraft.ReportedException;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class ExecutorUtils {
   private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
   private static final ExecutorService EXECUTOR = makeExecutor();

   public static ExecutorService background() {
      return EXECUTOR;
   }

   public static void shutdown() {
      EXECUTOR.shutdown();

      boolean flag;
      try {
         flag = EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException interruptedexception) {
         flag = false;
      }

      if (!flag) {
         EXECUTOR.shutdownNow();
      }
   }

   private static ExecutorService makeExecutor() {
      int i = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 5);
      AtomicInteger atomicinteger = new AtomicInteger(1);
      return new ForkJoinPool(i, p_314383_ -> {
         ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(p_314383_) {
            @Override
            protected void onTermination(Throwable throwOnTermination) {
               if (throwOnTermination != null) {
                  ExecutorUtils.LOGGER.warn("{} died", this.getName(), throwOnTermination);
               } else {
                  ExecutorUtils.LOGGER.debug("{} shutdown", this.getName());
               }

               super.onTermination(throwOnTermination);
            }
         };
         forkjoinworkerthread.setName("Worker-GE-" + atomicinteger.getAndIncrement());
         return forkjoinworkerthread;
      }, ExecutorUtils::onThreadException, true);
   }

   private static void onThreadException(Thread thread, Throwable throwable) {
      if (throwable instanceof CompletionException) {
         throwable = throwable.getCause();
      }

      if (throwable instanceof ReportedException reportedexception) {
         Bootstrap.realStdoutPrintln(reportedexception.getReport().getFriendlyReport(ReportType.CRASH));
         System.exit(-1);
      }

      LOGGER.error("Caught exception in thread {}", thread, throwable);
   }
}
