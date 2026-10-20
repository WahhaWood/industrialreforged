package com.industrialreforged.tech.core.integration.botania;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Client half of {@link BotaniaMachines}: the screen, and recipe-list refresh when the server syncs recipes. */
final class BotaniaMachinesClient {
   private BotaniaMachinesClient() {
   }

   static void init(IEventBus modBus) {
      modBus.addListener(BotaniaMachinesClient::registerScreens);
      NeoForge.EVENT_BUS.addListener((RecipesUpdatedEvent event) -> BotaniaMachineRecipe.invalidate());
   }

   private static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(BotaniaMachines.MACHINE_MENU.get(), BotaniaMachineScreen::new);
   }
}
