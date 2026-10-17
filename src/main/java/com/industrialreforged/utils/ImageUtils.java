package com.industrialreforged.utils;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;

public class ImageUtils {
   public static NativeImage convert(BufferedImage image) {
      NativeImage r = new NativeImage(image.getWidth(), image.getHeight(), false);

      for (int i1 = 0; i1 < image.getWidth(); i1++) {
         for (int i2 = 0; i2 < image.getHeight(); i2++) {
            int argb = image.getRGB(i1, i2);
            int rgba = argb >> 16 & 0xFF | argb << 16 & 0xFF0000 | argb & -16711936;
 r.setPixelRGBA(i1, i2, rgba);
         }
      }

      return r;
   }
}
