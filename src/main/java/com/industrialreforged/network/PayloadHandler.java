package com.industrialreforged.network;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class PayloadHandler<T extends BasePayload> implements IPayloadHandler<T> {
   public void handle(@NotNull T payload, @NotNull IPayloadContext context) {
      if (context.flow().isClientbound()) {
         this.handleClient(payload, context);
      } else if (context.flow().isServerbound() && context.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
         context.enqueueWork(() -> payload.executeServer(serverPlayer));
      }
   }

   public void handleClient(T payload, IPayloadContext context) {
      if (payload.isEnqueueWork()) {
         context.enqueueWork(() -> payload.executeClient(Minecraft.getInstance()));
      } else {
         payload.executeClient(Minecraft.getInstance());
      }
   }
}
