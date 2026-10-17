package com.industrialreforged.tech.core.util;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.util.FakePlayer;

public final class Util {
   public static final Direction[] ALL_DIRS = Direction.values();
   public static final Direction[] HORIZONTAL_DIRS = Arrays.copyOfRange(ALL_DIRS, 2, 6);

   public static int limit(int n, int n2, int n3) {
      return Math.clamp(n, n2, n3);
   }

   public static float limit(float f, float f2, float f3) {
      return !Float.isNaN(f) && !(f <= f2) ? Math.min(f, f3) : f2;
   }

   public static double limit(double d, double d2, double d3) {
      return !Double.isNaN(d) && !(d <= d2) ? Math.min(d, d3) : d2;
   }

   public static double map(double d, double d2, double d3) {
      if (d < 0.0 || Double.isNaN(d)) {
 d = 0.0;
      }

      if (d > d2) {
 d = d2;
      }

      return d / d2 * d3;
   }

   public static int square(int n) {
      return n * n;
   }

   public static boolean isSimilar(double f, double f2) {
      return Math.abs(f - f2) < 1.0E-5F;
   }

   public static ResourceLocation getDimId(Level level) {
      return level.dimension().location();
   }

   public static String toSiStringUU(int value) {
      return toSiString(value / 100000.0, 4) + "B";
   }

   public static String toSiString(double d, int n) {
      if (d == 0.0) {
         return "0 ";
      }

      if (Double.isNaN(d)) {
         return "NaN ";
      }

      Object object2 = "";
      if (d < 0.0) {
         object2 = "-";
 d = -d;
      }

      if (Double.isInfinite(d)) {
         return object2 + "∞ ";
      }

      double d3 = Math.log10(d);
      Object object;
      double d2;
      if (d3 >= 0.0) {
         int n3 = (int)Math.floor(d3 / 3.0);
         d2 = 1.0 / Math.pow(10.0, n3 * 3);

         object = switch (n3) {
            case 0 -> "";
            case 1 -> "k";
            case 2 -> "M";
            case 3 -> "G";
            case 4 -> "T";
            case 5 -> "P";
            case 6 -> "E";
            case 7 -> "Z";
            case 8 -> "Y";
            default -> "E" + n3 * 3;
         };
      } else {
         int n3 = (int)Math.ceil(-d3 / 3.0);
         d2 = Math.pow(10.0, n3 * 3);

         object = switch (n3) {
            case 0 -> "";
            case 1 -> "m";
            case 2 -> "µ";
            case 3 -> "n";
            case 4 -> "p";
            case 5 -> "f";
            case 6 -> "a";
            case 7 -> "z";
            case 8 -> "y";
            default -> "E-" + n3 * 3;
         };
      }

      double var12;
      int var16 = (int)Math.floor(var12 = d * d2);
 d = var12 - var16;
      int n4 = 1;
      if (var16 > 0) {
         n4 = (int)(n4 + Math.floor(Math.log10(var16)));
      }

      int n2;
      if ((n2 = (int)Math.round(d * (d2 = Math.pow(10.0, n - n4)))) >= d2) {
         n2 = (int)(n2 - d2);
         n4 = 1;
         if (++var16 > 0) {
            n4 = (int)(n4 + Math.floor(Math.log10(var16)));
         }
      }

      object2 = (String)object2 + var16;
      if (n > n4 && n2 != 0) {
         object2 = object2 + String.format(".%0" + (n - n4) + "d", n2);
      }

      object2 = ((String)object2).replaceFirst("(\\.\\d*?)0+$", "$1");
      return object2 + " " + object;
   }

   public static boolean isFakePlayer(Player player, boolean bl) {
      if (player == null) {
         return false;
      } else if (!(player instanceof ServerPlayer)) {
         return true;
      } else {
         return bl ? player instanceof FakePlayer : player.getClass() != ServerPlayer.class;
      }
   }

   public static boolean isAreaLoaded(LevelReader levelReader, BlockPos blockPos, int n) {
      return levelReader.hasChunksAt(blockPos.getX() - n, blockPos.getZ() - n, blockPos.getX() + n, blockPos.getZ() + n);
   }
}
