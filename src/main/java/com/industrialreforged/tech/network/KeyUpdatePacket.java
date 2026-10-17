package com.industrialreforged.tech.network;

import com.industrialreforged.network.BasePayload;
import com.industrialreforged.tech.core.IR;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class KeyUpdatePacket extends BasePayload {
   public static final Type<KeyUpdatePacket> TYPE = new Type(IR.location("key_update"));
   private final int key;

   public KeyUpdatePacket(int key) {
      this.key = key;
   }

   public KeyUpdatePacket(RegistryFriendlyByteBuf buf) {
      this.key = buf.readVarInt();
   }

   protected boolean isEnqueueWork() {
      return true;
   }

   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeVarInt(this.key);
   }

   @Override
   public void executeServer(ServerPlayer player) {
      // Без этого серверная карта клавиш навсегда пустая: пакет прилетал,
      // а срабатывал пустой дефолт из BasePayload. Мертвы были все клавишные
      // механики брони (ранец F / ховер Пробел+M, режимы инструментов).
      IR.keyboard.processKeyUpdate(player, this.key);
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
