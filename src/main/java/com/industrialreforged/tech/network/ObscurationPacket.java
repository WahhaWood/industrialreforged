package com.industrialreforged.tech.network;

import com.industrialreforged.network.BasePayload;
import com.mojang.datafixers.util.Pair;
import com.industrialreforged.tech.blockentity.comp.Obscuration;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.item.tool.ItemObscurator;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ObscurationPacket extends BasePayload {
   public static final Type<ObscurationPacket> TYPE = new Type(IR.location("obscuration"));
   private final int slotId;
   private final Obscuration.ObscurationData obscuration;

   public ObscurationPacket(int slotId, Obscuration.ObscurationData obscurationData) {
      this.slotId = slotId;
      this.obscuration = obscurationData;
   }

   public ObscurationPacket(RegistryFriendlyByteBuf buf) {
      this.slotId = buf.readByte();
      this.obscuration = (Obscuration.ObscurationData)Obscuration.ObscurationData.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).map(Pair::getFirst).getOrThrow();
   }

   protected boolean isEnqueueWork() {
      return true;
   }

   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeByte(this.slotId);
      buf.writeNbt((Tag)Obscuration.ObscurationData.CODEC.encodeStart(NbtOps.INSTANCE, this.obscuration).getOrThrow());
   }

   @Override
   public void executeServer(ServerPlayer player) {
      // Без этого скан обскуратора (Shift+ПКМ, клиент) уходил на сервер и терялся:
      // текстура в предмет не записывалась. Та же болезнь, что была у KeyUpdatePacket.
      ItemStack stack = player.getInventory().getItem(this.slotId);
      if (stack.getItem() instanceof ItemObscurator obscurator) {
         obscurator.writeObscuration(stack, this.obscuration);
      }
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
