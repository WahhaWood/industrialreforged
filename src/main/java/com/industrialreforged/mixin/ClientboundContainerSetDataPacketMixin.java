package com.industrialreforged.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundContainerSetDataPacket.class)
public class ClientboundContainerSetDataPacketMixin {
   @Mutable
   @Shadow
   @Final
   private int containerId;
   @Mutable
   @Shadow
   @Final
   private int id;
   @Mutable
   @Shadow
   @Final
   private int value;

   @Redirect(
      method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readShort()S", ordinal = 1)
   )
   public short init(FriendlyByteBuf instance) {
      return 0;
   }

   @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
   public void init(FriendlyByteBuf buf, CallbackInfo ci) {
      this.value = buf.readVarInt();
   }

   @Overwrite
   public void write(FriendlyByteBuf buf) {
      buf.writeByte(this.containerId);
      buf.writeShort(this.id);
      buf.writeVarInt(this.value);
   }
}
