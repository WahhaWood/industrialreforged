package com.industrialreforged;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.init.GEItems;
import com.industrialreforged.network.BasePayload;
import com.industrialreforged.network.PayloadHandler;
import com.industrialreforged.core.DataSlotSyncPayload;
import com.industrialreforged.core.ServerInfoPayload;
import com.industrialreforged.utils.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

@Mod("industrialreforged")
public class IndustrialReforged {
   private static final List<IndustrialReforged.NetworkRegistryData<?>> PACKETS = new ArrayList<>();
   public static String PROJECT_NAME;
   public static String TAB_SERVER_NAME;
   public static final String MODID = "industrialreforged";
   public static IndustrialReforged instance;
   public static boolean isPermission;

   public IndustrialReforged(IEventBus bus, ModContainer modContainer) {
 instance = this;
      bus.addListener(this::register);
      GEItems.REGISTRY.register(bus);
      GEDataComponents.REGISTRY.register(bus);
      isPermission = ModList.get().isLoaded("gravitypermission");
      // Companion content that used to live in separate jars, now part of
      // this single mod: energy wires and the whole IR machine set.
      new com.industrialreforged.transfer.core.TransferModule(bus, modContainer);
      new com.industrialreforged.tech.core.IR(bus, modContainer);
      // development-only control bridge (localhost HTTP); no-op in release builds
      com.industrialreforged.debug.bridge.DevBridge.start();
   }

   public void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
 PayloadHandler handler = new PayloadHandler();
      registrar.playToClient(DataSlotSyncPayload.TYPE, StreamCodec.ofMember(DataSlotSyncPayload::write, DataSlotSyncPayload::new), handler);
      registrar.playToClient(ServerInfoPayload.TYPE, StreamCodec.ofMember(ServerInfoPayload::write, ServerInfoPayload::new), handler);

      for (IndustrialReforged.NetworkRegistryData packet : PACKETS) {
         LogUtils.debug("Register packet: {}", packet.type);
         if (packet.flow == null) {
            registrar.playBidirectional(packet.type, packet.codec, handler);
         } else if (packet.flow.isServerbound()) {
            registrar.playToServer(packet.type, packet.codec, handler);
         } else {
            registrar.playToClient(packet.type, packet.codec, handler);
         }
      }

      PACKETS.clear();
   }

   public static ResourceLocation location(String path) {
      return ResourceLocation.fromNamespaceAndPath("industrialreforged", path);
   }

   public static <T extends CustomPacketPayload> Type<T> payloadType(String name) {
      return new Type(location(name));
   }

   public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToServer(payload, payloads);
   }

   public static void sendTo(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToPlayer(player, payload, payloads);
   }

   public static void sendToAll(CustomPacketPayload payload, CustomPacketPayload... payloads) {
      if (ServerLifecycleHooks.getCurrentServer() != null) {
         PacketDistributor.sendToAllPlayers(payload, payloads);
      }
   }

   public static void sendToAround(ServerLevel level, BlockPos blockPos, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      sendToAround(level, blockPos.getCenter(), radius, payload, payloads);
   }

   public static void sendToAround(ServerLevel level, Vec3 vec3, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      sendToAround(level, vec3.x, vec3.y, vec3.z, radius, payload, payloads);
   }

   public static void sendToAround(ServerLevel level, double x, double y, double z, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      if (ServerLifecycleHooks.getCurrentServer() != null) {
         PacketDistributor.sendToPlayersNear(level, null, x, y, z, radius, payload, payloads);
      }
   }

   public static void sendToAround(ServerPlayer player, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      sendToAround(player, player.position(), radius, payload, payloads);
   }

   public static void sendToAround(ServerPlayer player, BlockPos blockPos, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      sendToAround(player, blockPos.getCenter(), radius, payload, payloads);
   }

   public static void sendToAround(ServerPlayer player, Vec3 vec3, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      sendToAround(player, vec3.x, vec3.y, vec3.z, radius, payload, payloads);
   }

   public static void sendToAround(
      ServerPlayer player, double x, double y, double z, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads
   ) {
      if (ServerLifecycleHooks.getCurrentServer() != null) {
         PacketDistributor.sendToPlayersNear(player.serverLevel(), player, x, y, z, radius, payload, payloads);
      }
   }

   public static synchronized <MSG extends BasePayload> void registerMessage(
      Type<MSG> type, StreamMemberEncoder<RegistryFriendlyByteBuf, MSG> encoder, StreamDecoder<RegistryFriendlyByteBuf, MSG> decoder
   ) {
      registerMessage(type, encoder, decoder, null);
   }

   public static synchronized <MSG extends BasePayload> void registerMessage(
      Type<MSG> type,
      StreamMemberEncoder<RegistryFriendlyByteBuf, MSG> encoder,
      StreamDecoder<RegistryFriendlyByteBuf, MSG> decoder,
      @Nullable PacketFlow direction
   ) {
      PACKETS.add(new IndustrialReforged.NetworkRegistryData(type, encoder, decoder, direction));
   }

   static {
      String __ = System.getenv("PROJECT_NAME");
      PROJECT_NAME = __;
      String envServerName = System.getenv("SERVER_NAME");
      String serverName = envServerName != null && !envServerName.isEmpty() ? envServerName : "LOCALHOST";
      TAB_SERVER_NAME = serverName;
   }

   private static class NetworkRegistryData<MSG extends BasePayload> {
      Type<MSG> type;
      StreamCodec<RegistryFriendlyByteBuf, MSG> codec;
      @Nullable
      PacketFlow flow;

      public NetworkRegistryData(
         Type<MSG> type,
         StreamMemberEncoder<RegistryFriendlyByteBuf, MSG> encoder,
         StreamDecoder<RegistryFriendlyByteBuf, MSG> decoder,
         @Nullable PacketFlow flow
      ) {
         this.type = type;
         this.codec = StreamCodec.ofMember(encoder, decoder);
         this.flow = flow;
      }
   }
}
