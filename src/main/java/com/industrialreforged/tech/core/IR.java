package com.industrialreforged.tech.core;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.item.api.IEnergyContainerItem;
import com.mojang.logging.LogUtils;
import com.industrialreforged.tech.blockentity.generator.electric.SemifluidGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.generator.heat.FluidHeatGeneratorBlockEntity;
import com.industrialreforged.tech.blockentity.machine.LiquidHeatExchangerBlockEntity;
import com.industrialreforged.tech.core.forge.ClientEventHandlerForge;
import com.industrialreforged.tech.core.forge.EventHandlerForge;
import com.industrialreforged.tech.core.integration.botania.BotaniaIntegration;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRCreativeTabs;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IREntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRMobEffects;
import com.industrialreforged.tech.core.ref.IRRecipeSerializers;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRMenuRegistration;
import net.minecraft.resources.ResourceLocation;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.Keyboard;
import com.industrialreforged.tech.core.util.KeyboardClient;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.crop.IRCrops;
import com.industrialreforged.tech.item.ItemBooze;
import com.industrialreforged.tech.item.tool.ItemGraviTool;
import com.industrialreforged.tech.item.upgrade.ItemUpgradeModule;
import com.industrialreforged.tech.network.KeyUpdatePacket;
import com.industrialreforged.tech.network.ObscurationPacket;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class IR {
   public static final String MODID = "industrialreforged";
   public static final SideProxy sideProxy = createSideProxy();
   public static final Logger log = LogUtils.getLogger();
   public static final Keyboard keyboard = sideProxy.getKeyboard();
   public static final RandomSource random = RandomSource.create();
   /** True once the AE2 integration (ME machines) registered; read instead of touching its classes. */
   public static boolean ae2Integration;

   public IR(IEventBus bus, ModContainer container) {
      bus.register(this);
      if (FMLEnvironment.dist.isClient()) {
         bus.register(KeyboardClient.INSTANCE);
      }

      IRDataComponents.REGISTRY.register(bus);
      IRBlocks.REGISTRY.register(bus);
      IRFluids.REGISTRY.register(bus);
      IRItems.REGISTRY.register(bus);
      IREntities.REGISTRY.register(bus);
      IRMobEffects.REGISTRY.register(bus);
      IRBlockEntities.REGISTRY.register(bus);
      IRRecipeTypes.REGISTRY.register(bus);
      IRRecipeSerializers.REGISTRY.register(bus);
      bus.addListener(IRMenuRegistration::registerMenuTypes);
      IRCreativeTabs.REGISTRY.register(bus);
      IRArmorMaterials.REGISTRY.register(bus);
      IRSounds.REGISTRY.register(bus);
      if (ModList.get().isLoaded("cable_facades")) {
         com.industrialreforged.tech.core.integration.cablefacades.CableFacadesIntegration.init();
      }
      if (ModList.get().isLoaded("ae2")) {
         // the AE2 package is touched only behind this guard (1.0.8 crash); LinkageError = incompatible AE2
         try {
            com.industrialreforged.tech.core.integration.ae2.AE2Integration.init(bus);
            ae2Integration = true;
         } catch (LinkageError e) {
            log.error("AE2 integration disabled: incompatible AE2 version", e);
         }
      }
      if (ModList.get().isLoaded("botania")) {
         BotaniaIntegration.init(bus, ae2Integration);
      }

      IndustrialReforged.registerMessage(KeyUpdatePacket.TYPE, KeyUpdatePacket::write, KeyUpdatePacket::new, PacketFlow.SERVERBOUND);
      IndustrialReforged.registerMessage(
         com.industrialreforged.tech.core.network.OpenJeiPagePayload.TYPE,
         com.industrialreforged.tech.core.network.OpenJeiPagePayload::write,
         com.industrialreforged.tech.core.network.OpenJeiPagePayload::new,
         PacketFlow.CLIENTBOUND
      );
      IndustrialReforged.registerMessage(ObscurationPacket.TYPE, ObscurationPacket::write, ObscurationPacket::new, PacketFlow.SERVERBOUND);
      container.registerConfig(Type.SERVER, IRConfig.CONFIG_SPEC);
   }

   @SubscribeEvent
   public void load(FMLCommonSetupEvent event) {
      long l = System.nanoTime();
      log.debug("Starting common-setup.");
      SolidCannerRecipe.init();
      SemifluidGeneratorBlockEntity.init();
      FluidHeatGeneratorBlockEntity.init();
      LiquidHeatExchangerBlockEntity.init();
      IRCrops.init();
      log.debug("Finished common-setup after {} ms.", (System.nanoTime() - l) / 1000000L);
      NeoForge.EVENT_BUS.register(new EventHandlerForge());
      if (FMLEnvironment.dist.isClient()) {
         NeoForge.EVENT_BUS.register(new ClientEventHandlerForge());
      }
   }

   private static SideProxy createSideProxy() {
      String string = FMLEnvironment.dist.isClient() ? "com.industrialreforged.tech.core.SideProxyClient" : "com.industrialreforged.tech.core.SideProxyServer";

      try {
         return (SideProxy)Class.forName(string).getConstructor().newInstance();
      } catch (ReflectiveOperationException reflectiveOperationException) {
         throw new RuntimeException(reflectiveOperationException);
      }
   }

   @Nullable
   public static <C extends RecipeInput, T extends Recipe<C>> RecipeHolder<T> findRecipeFromId(RecipeType<T> recipeType, ResourceLocation id) {
      return sideProxy.getRecipeManager().getAllRecipesFor(recipeType).stream().filter(tRecipeHolder -> tRecipeHolder.id().equals(id)).findFirst().orElse(null);
   }

   public static ResourceLocation location(String path) {
      return ResourceLocation.fromNamespaceAndPath("industrialreforged", path);
   }

   public static ResourceLocation texture(String path) {
      return ResourceLocation.fromNamespaceAndPath("industrialreforged", String.format("textures/%s.png", path));
   }
}
