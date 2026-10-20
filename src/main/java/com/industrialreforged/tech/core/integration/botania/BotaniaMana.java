package com.industrialreforged.tech.core.integration.botania;

import appeng.api.stacks.AEKey;
import org.jetbrains.annotations.Nullable;

/**
 * What IR needs from Botania Reforged beyond Botania's public API: the mana AE key of its AE2 module and the
 * Alfheim market price. IR compiles against upstream Botania, so both are looked up by name.
 */
final class BotaniaMana {
   private static final String MANA_KEY = "vazkii.botania.ae2.ae2.ManaKey";
   private static final String MARKET_SETTINGS = "net.lmor.botanicalextramachinery.config.LibXServerConfig$AlfheimMarketSettings";
   /** Mana per Alfheim trade when the market setting cannot be read (its default). */
   private static final int DEFAULT_MARKET_COST = 500;
   @Nullable
   private static AEKey key;

   private BotaniaMana() {
   }

   static boolean available() {
      try {
         Class.forName(MANA_KEY, false, BotaniaMana.class.getClassLoader());
         return true;
      } catch (ClassNotFoundException e) {
         return false;
      }
   }

   /** Botania's mana as an AE key (one amount unit = one mana). */
   static AEKey key() {
      if (key == null) {
         try {
            key = (AEKey)Class.forName(MANA_KEY).getField("KEY").get(null);
         } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Botania mana key not found", e);
         }
      }

      return key;
   }

   /** Mana per trade of the Alfheim market (Botanical Machinery setting, read on every recipe scan). */
   static int marketCost() {
      try {
         return Class.forName(MARKET_SETTINGS).getField("recipeCost").getInt(null);
      } catch (ReflectiveOperationException | RuntimeException e) {
         return DEFAULT_MARKET_COST;
      }
   }
}
