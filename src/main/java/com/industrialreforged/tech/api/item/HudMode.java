package com.industrialreforged.tech.api.item;

public enum HudMode {
   DISABLED("industrialreforged.hud.disabled"),
   BASIC("industrialreforged.hud.basic"),
   EXTENDED("industrialreforged.hud.extended"),
   ADVANCED("industrialreforged.hud.advanced");

   private final String translationKey;
   private static final HudMode[] VALUES = values();

   HudMode(String string2) {
      this.translationKey = string2;
   }

   public boolean shouldDisplay() {
      return this != DISABLED;
   }

   public boolean hasTooltip() {
      return this == EXTENDED || this == ADVANCED;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public int getID() {
      return this.ordinal();
   }

   public static HudMode getFromID(int n) {
      return VALUES[n % VALUES.length];
   }

   public static int getMaxMode() {
      return VALUES.length - 1;
   }
}
