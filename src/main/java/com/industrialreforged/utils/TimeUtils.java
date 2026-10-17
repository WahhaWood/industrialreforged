package com.industrialreforged.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeUtils {
   private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");

   @NotNull
   public static ZoneId moscowZone() {
      return MOSCOW_ZONE;
   }

   @NotNull
   public static ZonedDateTime currentMoscowTime() {
      return toMoscow(Instant.now());
   }

   @NotNull
   public static LocalDate currentMoscowDate() {
      return LocalDate.now(MOSCOW_ZONE);
   }

   @NotNull
   public static ZonedDateTime toMoscow(@NotNull Instant instant) {
      return instant.atZone(MOSCOW_ZONE);
   }

   @NotNull
   public static ZonedDateTime toMoscow(@NotNull Timestamp timestamp) {
      return timestamp.toInstant().atZone(MOSCOW_ZONE);
   }

   @Nullable
   public static Duration elapsedTime(@Nullable Temporal instant) {
      return instant != null ? Duration.between(instant, Instant.now()) : null;
   }

   @Nullable
   public static Duration leftTime(@Nullable Temporal instant) {
      return instant != null ? Duration.between(Instant.now(), instant) : null;
   }

   public static int getDayOffFromWipe() {
      return ServerUtils.getInfo().getWipe().getDayPassed();
   }

   public static Duration getLeftTimeNextDay() {
      ZonedDateTime now = ZonedDateTime.now(MOSCOW_ZONE);
      return Duration.between(now, now.toLocalDate().plusDays(1L).atStartOfDay(MOSCOW_ZONE));
   }

   public static int getDayOfYear() {
      return currentMoscowDate().getDayOfYear();
   }

   public static Duration parseDuration(String value) {
      try {
         return value.isBlank() ? Duration.ZERO : Duration.ofSeconds(Long.parseLong(value));
      } catch (NumberFormatException e) {
         return NumberUtils.toLong(value.substring(0, value.length() - 1)).map(val -> {
            return switch (value.charAt(value.length() - 1)) {
               case 'M' -> Duration.ofDays(val * 30L);
               case 'd' -> Duration.ofDays(val);
               case 'h' -> Duration.ofHours(val);
               case 'm' -> Duration.ofMinutes(val);
               case 'y' -> Duration.ofDays(val * 365L);
               default -> Duration.ofSeconds(val);
            };
         }).orElse(Duration.ZERO);
      }
   }

   public static long convertToSeconds(String value) {
      try {
         return Long.parseLong(value);
      } catch (Throwable e) {
         try {
            long number = Long.parseLong(value.substring(0, value.length() - 1));
            char unit = value.charAt(value.length() - 1);

            return number * switch (unit) {
               case 'M' -> 2592000L;
               case 'd' -> 86400L;
               case 'h' -> 3600L;
               case 'm' -> 60L;
               case 'y' -> 946080000L;
               default -> 1L;
            };
         } catch (Throwable e1) {
            return -1L;
         }
      }
   }
}
