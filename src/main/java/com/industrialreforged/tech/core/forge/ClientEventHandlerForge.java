package com.industrialreforged.tech.core.forge;

import com.industrialreforged.tech.core.TickHandler;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ClientEventHandlerForge {
   @SubscribeEvent
   public void onClientTick(Pre clientTickEvent) {
      TickHandler.onClientTick();
   }

   @SubscribeEvent
   public void onRenderFogColor(ComputeFogColor fogColors) {
      // Зовётся каждый кадр: вне жидкости выходим до поиска в реестре.
      Fluid fluid = fogColors.getCamera().getBlockAtCamera().getFluidState().getType();
      if (fluid != null && fluid != Fluids.EMPTY && "industrialreforged".equals(NeoForgeRegistries.FLUID_TYPES.getKey(fluid.getFluidType()).getNamespace())) {
         IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
         int n = extensions.getTintColor();
         if (n >= 0) {
            fogColors.setRed((n >>> 16 & 0xFF) / 255.0F);
            fogColors.setGreen((n >>> 8 & 0xFF) / 255.0F);
            fogColors.setBlue((n & 0xFF) / 255.0F);
         }
      }
   }
}
