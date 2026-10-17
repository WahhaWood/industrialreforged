package com.industrialreforged.tech.core.network;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.network.BasePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

/** Сервер → клиент: открыть страницу JEI (например, мультиблок резервуара). */
public class OpenJeiPagePayload extends BasePayload {
   public static final CustomPacketPayload.Type<OpenJeiPagePayload> TYPE = IndustrialReforged.payloadType("open_jei_page");
   private final String page;

   public OpenJeiPagePayload(String page) {
      this.page = page;
   }

   public OpenJeiPagePayload(RegistryFriendlyByteBuf buf) {
      this.page = buf.readUtf();
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeUtf(this.page);
   }

   @Override
   protected boolean isEnqueueWork() {
      // JEI открывается только из основного потока клиента
      return true;
   }

   @Override
   public void executeClient(Minecraft mc) {
      if (!ModList.get().isLoaded("jei")) {
         return;
      }
      if ("tank".equals(this.page)) {
         com.industrialreforged.tech.core.integration.jei.IRJeiPlugin.openTankMultiblock();
      } else if ("reactor".equals(this.page)) {
         com.industrialreforged.tech.core.integration.jei.IRJeiPlugin.openReactorMultiblock();
      }
   }

   @NotNull
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
