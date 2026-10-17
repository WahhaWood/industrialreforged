package com.industrialreforged.core;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.menu.MenuBase;
import com.industrialreforged.menu.sync.AbstractCustomDataSlot;
import com.industrialreforged.network.BasePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.jetbrains.annotations.NotNull;

/** Server -> client packet that pushes one custom menu data slot. */
public class DataSlotSyncPayload extends BasePayload {
   public static final Type<DataSlotSyncPayload> TYPE = IndustrialReforged.payloadType("menu_sync");
   private final int index;
   private final AbstractCustomDataSlot slot;
   private RegistryFriendlyByteBuf buffer;

   public DataSlotSyncPayload(int index, AbstractCustomDataSlot slot) {
      this.index = index;
      this.slot = slot;
   }

   public DataSlotSyncPayload(RegistryFriendlyByteBuf buf) {
      this.index = buf.readUnsignedShort();
      this.buffer = new RegistryFriendlyByteBuf(buf.readBytes(buf.readableBytes()), buf.registryAccess(), buf.getConnectionType());
      this.slot = null;
   }

   @Override
   public boolean isEnqueueWork() {
      return true;
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeShort(this.index);
      this.slot.write(buf);
   }

   @Override
   public void executeClient(Minecraft mc) {
      if (mc.player.containerMenu instanceof MenuBase<?> base) {
         base.readSyncData(this.index, this.buffer);
      }
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
