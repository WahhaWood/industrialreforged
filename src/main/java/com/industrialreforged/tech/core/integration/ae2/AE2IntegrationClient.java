package com.industrialreforged.tech.core.integration.ae2;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Client half of {@link AE2Integration}: screens, and recipe-list refresh when the server syncs recipes. */
final class AE2IntegrationClient {
   private AE2IntegrationClient() {
   }

   static void init(IEventBus modBus) {
      modBus.addListener(AE2IntegrationClient::registerScreens);
      NeoForge.EVENT_BUS.addListener((RecipesUpdatedEvent event) -> MEMachineRecipe.invalidate());
   }

   private static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(AE2Integration.ME_MACHINE_MENU.get(), MEMachineScreen::new);
      event.register(AE2Integration.ME_RECYCLER_MENU.get(), MERecyclerScreen::new);
      event.register(AE2Integration.ME_REPLICATOR_MENU.get(), MEReplicatorScreen::new);
      event.register(AE2Integration.ME_MATTER_MENU.get(), MEMatterGeneratorScreen::new);
   }
}
