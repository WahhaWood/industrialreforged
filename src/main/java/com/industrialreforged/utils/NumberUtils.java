package com.industrialreforged.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import com.industrialreforged.support.Inline;

public class NumberUtils {
   private static final DecimalFormat DECIMAL;
   private static final DecimalFormat y;

   public static boolean isNumber(String s) {
      return s.matches("-?[0-9]+\\.?[0-9]+");
   }

   public static Optional<Double> toDouble(Object o) {
      try {
         return Optional.of(o instanceof Double ? (Double)o : Double.parseDouble(o.toString()));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   public static Optional<Integer> toInt(Object o) {
      try {
         return Optional.of(o instanceof Integer ? (Integer)o : Integer.parseInt(o.toString()));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   public static Optional<Long> toLong(Object o) {
      try {
         return Optional.of(o instanceof Long ? (Long)o : Long.parseLong(o.toString()));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   public static Optional<Float> toFloat(Object o) {
      try {
         return Optional.of(o instanceof Float ? (Float)o : Float.parseFloat(o.toString()));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   public static boolean inRange(int checkNumber, int lowerBound, int upperBound) {
      return checkNumber >= lowerBound && checkNumber <= upperBound;
   }

   public static boolean inRange(float checkNumber, float lowerBound, float upperBound) {
      return checkNumber >= lowerBound && checkNumber <= upperBound;
   }

   public static boolean inRange(double checkNumber, double lowerBound, double upperBound) {
      return checkNumber >= lowerBound && checkNumber <= upperBound;
   }

   public static int calcScale(double maxValue, double currentValue, int scale) {
      return currentValue > 0.0 && maxValue > 0.0 ? Mth.clamp((int)(scale * (currentValue / maxValue)), 0, scale) : 0;
   }

   public static String formatTime(TimeUnit unit, long time, boolean from, NumberUtils.FormatTimeType type) {
      switch (type) {
         case FIRST:
            long hoursx = unit.toHours(time);
            if (hoursx > 24L) {
               return timeText(unit.toDays(time), "format.helper.time.day", from);
            } else {
               long minutes = unit.toMinutes(time);
               if (minutes > 60L) {
                  return timeText(hoursx, "format.helper.time.hour", from);
               } else {
                  long seconds = unit.toSeconds(time);
                  if (seconds > 60L) {
                     return timeText(minutes, "format.helper.time.minute", from);
                  }

                  return timeText(seconds, "format.helper.time.second", from);
               }
            }
         case FIRST_DOUBLE:
            long hours2 = unit.toHours(time);
            if (hours2 > 24L) {
               return String.format(
                  "%s %s",
                  timeText(unit.toDays(time), "format.helper.time.day", from),
                  timeText(hours2 % 24L, "format.helper.time.hour", from)
               );
            } else {
               long minutes2 = unit.toMinutes(time);
               if (minutes2 > 60L) {
                  return String.format(
                     "%s %s",
                     timeText(hours2, "format.helper.time.hour", from),
                     timeText(minutes2 % 60L, "format.helper.time.minute", from)
                  );
               } else {
                  long seconds2 = unit.toSeconds(time);
                  if (seconds2 > 60L) {
                     return String.format(
                        "%s %s",
                        timeText(minutes2, "format.helper.time.minute", from),
                        timeText(seconds2 % 60L, "format.helper.time.second", from)
                     );
                  }

                  return timeText(seconds2, "format.helper.time.second", from);
               }
            }
         case SECONDS:
            return timeText(unit.toSeconds(time), "format.helper.time.second", from);
         case MINUTES:
            return timeText(unit.toMinutes(time), "format.helper.time.minute", from);
         case HOURS:
            return timeText(unit.toHours(time), "format.helper.time.hour", from);
         case DAYS:
            return timeText(unit.toDays(time), "format.helper.time.day", from);
         case ALL:
            long hours7 = unit.toHours(time);
            long minutes7 = unit.toMinutes(time);
            long seconds7 = unit.toSeconds(time);
            if (hours7 > 24L) {
               return String.format(
                  "%s %s %s %s",
                  timeText(unit.toDays(time), "format.helper.time.hour", from),
                  timeText(hours7 % 24L, "format.helper.time.hour", from),
                  timeText(minutes7 % 60L, "format.helper.time.minute", from),
                  timeText(seconds7 % 60L, "format.helper.time.second", from)
               );
            }

            return String.format(
               "%s %s %s",
               timeText(hours7, "format.helper.time.hour", from),
               timeText(minutes7 % 60L, "format.helper.time.minute", from),
               timeText(seconds7 % 60L, "format.helper.time.second", from)
            );
         case DAYS_OR_EXCLUDE:
            long hours8 = unit.toHours(time);
            if (hours8 > 24L) {
               return timeText(unit.toDays(time), "format.helper.time.hour", from);
            }

            return String.format(
               "%s %s %s",
               timeText(hours8, "format.helper.time.hour", from),
               timeText(unit.toMinutes(time) % 60L, "format.helper.time.minute", from),
               timeText(unit.toSeconds(time) % 60L, "format.helper.time.second", from)
            );
         default:
            return "";
      }
   }

   public static String formatTimeHMS(TimeUnit unit, long time) {
      return String.format(
         "%s %s %s",
         timeText(unit.toHours(time) % 24L, "format.helper.time.hour", false),
         timeText(unit.toMinutes(time) % 60L, "format.helper.time.minute", false),
         timeText(unit.toSeconds(time) % 60L, "format.helper.time.second", false)
      );
   }

   public static String formatTimeDHMS(TimeUnit unit, long time) {
      return String.format("%s %s", timeText(unit.toDays(time), "format.helper.time.day", false), formatTimeHMS(unit, time));
   }

   public static String formatTimeDorHMS(TimeUnit unit, long time) {
      return unit.toHours(time) > 24L ? timeText(unit.toDays(time), "format.helper.time.day", false) : formatTimeHMS(unit, time);
   }

   public static Component formatToComponentTime(TimeUnit unit, long time, boolean from, NumberUtils.FormatTimeType type) {
      switch (type) {
         case FIRST:
            long hoursx = unit.toHours(time);
            if (hoursx > 24L) {
               return timeComponent(unit.toDays(time), "format.helper.time.day", from);
            } else {
               long minutes = unit.toMinutes(time);
               if (minutes > 60L) {
                  return timeComponent(hoursx, "format.helper.time.hour", from);
               } else {
                  long seconds = unit.toSeconds(time);
                  if (seconds > 60L) {
                     return timeComponent(minutes, "format.helper.time.minute", from);
                  }

                  return timeComponent(seconds, "format.helper.time.second", from);
               }
            }
         case FIRST_DOUBLE:
            long hours2 = unit.toHours(time);
            if (hours2 > 24L) {
               return timeComponent(unit.toDays(time), "format.helper.time.day", from)
                  .append(" ")
                  .append(timeComponent(hours2 % 24L, "format.helper.time.hour", from));
            } else {
               long minutes2 = unit.toMinutes(time);
               if (minutes2 > 60L) {
                  return timeComponent(hours2, "format.helper.time.hour", from)
                     .append(" ")
                     .append(timeComponent(minutes2 % 60L, "format.helper.time.minute", from));
               } else {
                  long seconds2 = unit.toSeconds(time);
                  if (seconds2 > 60L) {
                     return timeComponent(minutes2, "format.helper.time.minute", from)
                        .append(" ")
                        .append(timeComponent(seconds2 % 60L, "format.helper.time.second", from));
                  }

                  return timeComponent(seconds2, "format.helper.time.second", from);
               }
            }
         case SECONDS:
            return timeComponent(unit.toSeconds(time), "format.helper.time.second", from);
         case MINUTES:
            return timeComponent(unit.toMinutes(time), "format.helper.time.minute", from);
         case HOURS:
            return timeComponent(unit.toHours(time), "format.helper.time.hour", from);
         case DAYS:
            return timeComponent(unit.toDays(time), "format.helper.time.day", from);
         case ALL:
            long hours7 = unit.toHours(time);
            long minutes7 = unit.toMinutes(time);
            long seconds7 = unit.toSeconds(time);
            if (hours7 > 24L) {
               return timeComponent(unit.toDays(time), "format.helper.time.hour", from)
                  .append(" ")
                  .append(timeComponent(hours7 % 24L, "format.helper.time.hour", from))
                  .append(" ")
                  .append(timeComponent(minutes7 % 60L, "format.helper.time.minute", from))
                  .append(" ")
                  .append(timeComponent(seconds7 % 60L, "format.helper.time.second", from));
            }

            return timeComponent(hours7, "format.helper.time.hour", from)
               .append(" ")
               .append(timeComponent(minutes7 % 60L, "format.helper.time.minute", from))
               .append(" ")
               .append(timeComponent(seconds7 % 60L, "format.helper.time.second", from));
         case DAYS_OR_EXCLUDE:
            long hours8 = unit.toHours(time);
            if (hours8 > 24L) {
               return timeComponent(unit.toDays(time), "format.helper.time.hour", from);
            }

            return timeComponent(hours8, "format.helper.time.hour", from)
               .append(" ")
               .append(timeComponent(unit.toMinutes(time) % 60L, "format.helper.time.minute", from))
               .append(" ")
               .append(timeComponent(unit.toSeconds(time) % 60L, "format.helper.time.second", from));
         default:
            return Component.empty();
      }
   }

   public static Component formatToComponentTimeHMS(TimeUnit unit, long time) {
      return timeComponent(unit.toHours(time) % 24L, "format.helper.time.hour", false)
         .append(" ")
         .append(timeComponent(unit.toMinutes(time) % 60L, "format.helper.time.minute", false))
         .append(" ")
         .append(timeComponent(unit.toSeconds(time) % 60L, "format.helper.time.second", false));
   }

   public static Component formatToComponentTimeDHMStoComponent(TimeUnit unit, long time) {
      return timeComponent(unit.toDays(time), "format.helper.time.day", false).append(" ").append(formatToComponentTimeHMS(unit, time));
   }

   public static Component formatToComponentTimeDorHMStoComponent(TimeUnit unit, long time) {
      return (Component)(unit.toHours(time) > 24L
         ? timeComponent(unit.toDays(time), "format.helper.time.day", false)
         : formatToComponentTimeHMS(unit, time));
   }

   private static String timeText(long count, String unlocalization, boolean from) {
      int forms = (int)(count % 100L);
      int lcount = (int)(count % 10L);
      if (count == 0L) {
         unlocalization = unlocalization + ".2";
      } else if (lcount == 1 && forms != 11) {
         unlocalization = unlocalization + (from ? ".-1" : ".0");
      } else if (lcount < 2 || lcount > 4 || forms >= 10 && forms < 20) {
         unlocalization = unlocalization + ".2";
      } else {
         unlocalization = unlocalization + ".1";
      }

      return String.format("%d %s", count, MsgUtils.translate(unlocalization));
   }

   private static MutableComponent timeComponent(long count, String unlocalization, boolean from) {
      int forms = (int)(count % 100L);
      int lcount = (int)(count % 10L);
      if (count == 0L) {
         unlocalization = unlocalization + ".2";
      } else if (lcount == 1 && forms != 11) {
         unlocalization = unlocalization + (from ? ".-1" : ".0");
      } else if (lcount < 2 || lcount > 4 || forms >= 10 && forms < 20) {
         unlocalization = unlocalization + ".2";
      } else {
         unlocalization = unlocalization + ".1";
      }

      return Component.literal(String.valueOf(count)).append(" ").append(Component.translatable(unlocalization));
   }

   public static String formatInt(int value) {
      return y.format(value);
   }

   public static String formatLong(long value) {
      return y.format(value);
   }

   public static String formatDouble(double value) {
      return y.format(value);
   }

   public static String formatDoubleRound(double value) {
      return DECIMAL.format(value);
   }

   public static String formatToSiWhole(long value) {
      return formatToSiWhole(value, "%s%s");
   }

   public static String formatToSiWhole(long value, String format) {
      if (value < 1000L) {
         return String.format(format, value, "");
      } else if (value < 1000000L) {
         return String.format(format, value / 1000L, "k");
      } else if (value < 1000000000L) {
         return String.format(format, value / 1000000L, "M");
      } else {
         return value < 1000000000000L ? String.format(format, value / 1000000000L, "G") : String.format(format, value / 1000000000000L, "T");
      }
   }

   public static String formatToSi(long value) {
      return value < 1000L ? String.valueOf(value) : formatToSi((double)value);
   }

   public static String formatToSi(double value) {
      return formatToSi(value, "%s%s");
   }

   public static String formatToSi(double value, String format) {
      if (value < 1000.0) {
         return String.format(format, formatDoubleRound(value), "");
      } else if (value < 1000000.0) {
         return String.format(format, formatDoubleRound(value / 1000.0), "k");
      } else if (value < 1.0E9) {
         return String.format(format, formatDoubleRound(value / 1000000.0), "M");
      } else {
         return value < 1.0E12 ? String.format(format, formatDoubleRound(value / 1.0E9), "G") : String.format(format, formatDoubleRound(value / 1.0E12), "T");
      }
   }

   @Inline
   public static double max(double first, double second) {
      return first > second ? first : second;
   }

   @Inline
   public static double min(double first, double second) {
      return first < second ? first : second;
   }

   @Inline
   public static double clamp(double value, double min, double max) {
      return value < min ? min : (value > max ? max : value);
   }

   @Inline
   public static float max(float first, float second) {
      return first > second ? first : second;
   }

   @Inline
   public static float min(float first, float second) {
      return first < second ? first : second;
   }

   @Inline
   public static float clamp(float value, float min, float max) {
      return value < min ? min : (value > max ? max : value);
   }

   static {
      DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
      symbols.setGroupingSeparator(' ');
 DECIMAL = new DecimalFormat("#,##0.##", symbols);
 y = new DecimalFormat("#,##0", symbols);
   }

   public enum FormatTimeType {
      FIRST,
      FIRST_DOUBLE,
      SECONDS,
      MINUTES,
      HOURS,
      DAYS,
      ALL,
      DAYS_OR_EXCLUDE;
   }
}
