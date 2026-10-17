package com.industrialreforged.transfer.core;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.network.BasePayload;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WireLimitPayload extends BasePayload {
   public static final Type<WireLimitPayload> TYPE = IndustrialReforged.payloadType("transfer_edit_limit");
   private final long limit;

   public WireLimitPayload(long limit) {
      this.limit = limit;
   }

   public WireLimitPayload(RegistryFriendlyByteBuf buf) {
      this.limit = buf.readVarLong();
   }

   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeVarLong(this.limit);
   }

   protected boolean isEnqueueWork() {
      return true;
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   @Override
   public void executeServer(net.minecraft.server.level.ServerPlayer player) {
      if (player.containerMenu instanceof WireMenu menu) {
         menu.applyResistor(this.limit);
      }
   }
}
