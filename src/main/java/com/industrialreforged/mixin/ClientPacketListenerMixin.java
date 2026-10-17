package com.industrialreforged.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
   @Redirect(
      method = "handleUpdateAttributes",
      at = @At(remap = false, value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V")
   )
   public void removeLog(Logger instance, String s, Object o, Object o2) {
   }
}
