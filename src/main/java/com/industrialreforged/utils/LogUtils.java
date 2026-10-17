package com.industrialreforged.utils;

import javax.annotation.Nullable;
import org.slf4j.Logger;

public class LogUtils {
   private static final Logger n_s = com.mojang.logging.LogUtils.getLogger();

   public static void info(String msg, Object... args) {
      n_s.info(msg, args);
   }

   public static void info(String msg, Throwable throwable) {
      n_s.info(msg, throwable);
   }

   public static void warn(String msg, Object... args) {
      n_s.warn(msg, args);
   }

   public static void warn(String msg, Throwable throwable) {
      n_s.warn(msg, throwable);
   }

   public static void debug(String msg, Object... args) {
      n_s.debug(msg, args);
   }

   public static void debug(String msg, Throwable args) {
      n_s.debug(msg, args);
   }

   public static void severe(String msg, Object... args) {
      severe(null, msg, args);
   }

   public static void severe(@Nullable Throwable throwable, String msg, Object... args) {
      if (throwable == null) {
         n_s.error(msg, args);
      } else {
         n_s.error(msg, args, throwable);
      }
   }
}
