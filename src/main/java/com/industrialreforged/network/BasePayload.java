package com.industrialreforged.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public abstract class BasePayload implements CustomPacketPayload {
   public abstract void write(RegistryFriendlyByteBuf var1);

   protected boolean isEnqueueWork() {
      return false;
   }

   public void executeClient(Minecraft mc) {
   }

   /** Server side handler, called on the main thread. */
   public void executeServer(ServerPlayer player) {
   }
}
