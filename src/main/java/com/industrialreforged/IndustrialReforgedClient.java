package com.industrialreforged;

import com.industrialreforged.font.FontType;
import com.industrialreforged.core.AsyncTexture;
import com.industrialreforged.utils.LogUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import com.industrialreforged.transfer.init.GTMenuTypes;
import com.industrialreforged.transfer.render.WireGeometry;
import com.industrialreforged.transfer.screen.WireScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-only half of the mod: custom font/texture bootstrap and the sound
 * filter.  Kept in a separate class so that dedicated servers never touch
 * client-only Minecraft classes.
 */
@Mod(value = IndustrialReforged.MODID, dist = Dist.CLIENT)
public class IndustrialReforgedClient {
   public IndustrialReforgedClient(IEventBus bus, ModContainer container) {
      bus.addListener(this::clientSetup);
      bus.addListener(this::registerMenuScreens);
      bus.addListener(this::registerGeometryLoaders);
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::playSound);
   }

   private void clientSetup(FMLClientSetupEvent event) {
      ResourceLocation location = IndustrialReforged.location("textures/loading.png");
      RenderSystem.recordRenderCall(() -> {
         LogUtils.info("Loading custom font: " + FontType.values().length);
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getTextureManager().register(location, new SimpleTexture(location));
         AsyncTexture.loadingTextureId = minecraft.getTextureManager().getTexture(location).getId();
      });
   }

   private void registerMenuScreens(RegisterMenuScreensEvent event) {
      event.register(GTMenuTypes.ENERGY_CABLE.get(), WireScreen::new);
   }

   private void registerGeometryLoaders(RegisterGeometryLoaders event) {
      event.register(ResourceLocation.fromNamespaceAndPath(IndustrialReforged.MODID, "wire"), new WireGeometry.Loader());
   }

   private void playSound(PlaySoundEvent event) {
      if (event.getName().equals("entity.wither.spawn") || event.getName().equals("entity.wither.death")
            || event.getName().equals("entity.ender_dragon.death")) {
         event.setSound(null);
      }
   }
}
