package com.industrialreforged.screen;

import net.minecraft.client.Minecraft;

public class ScreenScale {
   private static final Minecraft mc = Minecraft.getInstance();
   public static float FULL_HD = 1.7777778F;
   public static float screenCenterX;
   public static float screenCenterY;
   public static int screenWidth;
   public static int screenHeight;
   public static float scaleValue;
   public static float DEFAULT_WIDTH = 1920.0F;
   public static float HALF_WIDTH = 960.0F;
   public static float DEFAULT_HEIGHT = 1080.0F;
   public static float HALF_HEIGHT = 540.0F;

   public static void update() {
      refresh(FULL_HD);
   }

   public static void update(float minAspect) {
      refresh(minAspect);
   }

   private static void refresh(float minAspect) {
      screenWidth = mc.getWindow().getGuiScaledWidth();
      screenHeight = mc.getWindow().getGuiScaledHeight();
      screenCenterX = screenWidth / 2.0F;
      screenCenterY = screenHeight / 2.0F;
      float ratio = (float)screenWidth / screenHeight;
      scaleValue = ratio < minAspect ? screenHeight / (1.0F + (minAspect - ratio)) : screenHeight;
   }

   public static float get(float value) {
      return scaleValue / (DEFAULT_HEIGHT / value);
   }

   public static float get(float value, float size) {
      return scaleValue / (DEFAULT_HEIGHT / value) - get(size) / 2.0F;
   }

   public static float getf(float value) {
      return scaleValue / (DEFAULT_HEIGHT / value);
   }

   public static float getf(float value, float size) {
      return scaleValue / (DEFAULT_HEIGHT / value) - get(size) / 2.0F;
   }

   public static float getX(float value) {
      return scaleValue / (DEFAULT_WIDTH / value);
   }

   public static float getfX(float value) {
      return scaleValue / (DEFAULT_WIDTH / value);
   }

   public static float getCenterX(float value, float size) {
      return screenCenterX + scaleValue / (DEFAULT_HEIGHT / (value - HALF_WIDTH)) - get(size) / 2.0F;
   }

   public static float getCenterX(float value) {
      return screenCenterX + scaleValue / (DEFAULT_HEIGHT / (value - HALF_WIDTH));
   }

   public static float getCenterY(float value) {
      return screenCenterY + scaleValue / (DEFAULT_HEIGHT / (value - HALF_HEIGHT));
   }

   public static float getCenterY(float value, float size) {
      return screenCenterY + scaleValue / (DEFAULT_HEIGHT / (value - HALF_HEIGHT)) - get(size) / 2.0F;
   }

   public static float getRight(float value) {
      return screenWidth + scaleValue / (DEFAULT_HEIGHT / (value - DEFAULT_WIDTH));
   }

   public static float getRight(float value, float size) {
      return screenWidth + scaleValue / (DEFAULT_HEIGHT / (value - DEFAULT_WIDTH)) - get(size) / 2.0F;
   }

   public static float getBot(float value) {
      return screenHeight - scaleValue / (DEFAULT_HEIGHT / (DEFAULT_HEIGHT - value));
   }

   public static float getBot(float y, float size) {
      return screenHeight - scaleValue / (DEFAULT_HEIGHT / (DEFAULT_HEIGHT - y)) - get(size) / 2.0F;
   }
}
