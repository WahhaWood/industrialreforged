package com.industrialreforged.tech.core.integration.botania;

import net.minecraft.world.item.CreativeModeTab.Output;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Botania integration, enabled only when Botania is present. With AE2 too it adds the ME machines for Botania
 * recipes ({@link BotaniaMachines}); they take mana from the network as Botania's own AE key, so they also need
 * Botania's AE2 module (Botania Reforged ships it).
 */
public final class BotaniaIntegration {
   private static final Logger LOG = LoggerFactory.getLogger("IndustrialReforged/Botania");
   public static boolean meMachines;

   private BotaniaIntegration() {
   }

   /** @param ae2 the AE2 integration of IR is active */
   public static void init(IEventBus bus, boolean ae2) {
      if (!ae2) {
         return;
      }

      if (!BotaniaMana.available()) {
         LOG.warn("Botania has no AE2 mana key (not Botania Reforged?): ME machines for Botania recipes are off");
         return;
      }

      try {
         BotaniaMachines.init(bus);
         meMachines = true;
      } catch (LinkageError e) {
         LOG.error("ME machines for Botania recipes disabled: incompatible Botania version", e);
      }
   }

   public static void tab(Output output) {
      if (meMachines) {
         BotaniaMachines.tab(output);
      }
   }
}
