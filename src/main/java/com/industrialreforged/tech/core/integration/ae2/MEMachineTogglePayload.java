package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.network.BasePayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Client → server: a click in the ME machine GUI (one recipe on/off, or all of them). */
public class MEMachineTogglePayload extends BasePayload {
   public static final Type<MEMachineTogglePayload> TYPE = new Type<>(IR.location("me_machine_toggle"));

   public enum Action {
      TOGGLE,
      ENABLE_ALL,
      DISABLE_ALL
   }

   private final int containerId;
   private final Action action;
   @Nullable
   private final ResourceLocation recipe;

   public MEMachineTogglePayload(int containerId, Action action, @Nullable ResourceLocation recipe) {
      this.containerId = containerId;
      this.action = action;
      this.recipe = recipe;
   }

   public MEMachineTogglePayload(RegistryFriendlyByteBuf buf) {
      this.containerId = buf.readVarInt();
      this.action = buf.readEnum(Action.class);
      this.recipe = buf.readBoolean() ? buf.readResourceLocation() : null;
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeVarInt(this.containerId);
      buf.writeEnum(this.action);
      buf.writeBoolean(this.recipe != null);
      if (this.recipe != null) {
         buf.writeResourceLocation(this.recipe);
      }
   }

   @Override
   public void executeServer(ServerPlayer player) {
      // only the menu the player really has open, and only while it is still valid
      if (player.containerMenu instanceof METoggleMenu menu && player.containerMenu.containerId == this.containerId && player.containerMenu.stillValid(player)) {
         menu.applyToggle(this.action, this.recipe);
      }
   }

   @NotNull
   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
