package com.industrialreforged.tech.core.util;

import net.minecraft.world.level.Level;

public class WindUtil {
   public static double getStrength(Level level, double height) {
      if (level == null) {
         return 0.0;
      }

      double ret = 50.0;
      if (height > 160.0) {
         ret -= (height - 160.0) / 4.0;
      } else if (height < 150.0) {
         ret -= (150.0 - height) / 2.0;
      }

      if (level.isThundering()) {
         ret *= 1.5;
      } else if (level.isRaining()) {
         ret *= 1.25;
      }

      return Math.max(0.0, ret);
   }
}
