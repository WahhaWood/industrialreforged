package com.industrialreforged.core;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.ServerInfo;
import com.industrialreforged.network.BasePayload;
import com.industrialreforged.utils.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.jetbrains.annotations.NotNull;

/** Server -> client packet carrying the server information blob. */
public class ServerInfoPayload extends BasePayload {
   public static final Type<ServerInfoPayload> TYPE = IndustrialReforged.payloadType("server_info");
   private final ServerInfo info;

   public ServerInfoPayload(ServerInfo serverInfo) {
      this.info = serverInfo;
   }

   public ServerInfoPayload(RegistryFriendlyByteBuf buf) {
      this.info = new ServerInfo(buf);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
   }

   @Override
   public void executeClient(Minecraft mc) {
      ServerUtils.serverInfo = this.info;
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
