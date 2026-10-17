package com.industrialreforged.transfer.core;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.utils.CapabilitiesUtils;
import com.industrialreforged.transfer.init.GTBlockEntities;
import com.industrialreforged.transfer.init.GTBlocks;
import com.industrialreforged.transfer.init.GTItems;
import com.industrialreforged.transfer.init.GTMenuTypes;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class TransferModule {
   public static final String MODID = "industrialreforged";

   public TransferModule(IEventBus bus, ModContainer container) {
      bus.register(this);
      GTBlocks.REGISTRY.register(bus);
      GTItems.REGISTRY.register(bus);
      GTBlockEntities.REGISTRY.register(bus);
      GTMenuTypes.REGISTRY.register(bus);
      IndustrialReforged.registerMessage(WireLimitPayload.TYPE, WireLimitPayload::write, WireLimitPayload::new, PacketFlow.SERVERBOUND);
   }

   @SubscribeEvent
   public void registerCapabilities(RegisterCapabilitiesEvent event) {
      CapabilitiesUtils.registerEnergyStorageBlock(event, GTBlockEntities.WIRE);
   }

}
