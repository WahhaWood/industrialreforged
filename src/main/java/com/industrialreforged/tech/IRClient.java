package com.industrialreforged.tech;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.tech.client.IRClientFluids;
import com.industrialreforged.tech.client.IRClientScreens;
import com.industrialreforged.tech.client.SoundUtilClient;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.client.render.KineticGeneratorRenderer;
import com.industrialreforged.tech.client.render.TankFluidRenderer;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IREntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ItemBooze;
import com.industrialreforged.tech.item.tool.ItemGraviTool;
import com.industrialreforged.tech.item.upgrade.ItemUpgradeModule;
import com.industrialreforged.item.api.IEnergyContainerItem;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component; // keep imports ordered below
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import com.industrialreforged.tech.core.util.Util;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

/** Client-only mod entry: item properties, block/item colours, renderers. */
@Mod(value = IndustrialReforged.MODID, dist = Dist.CLIENT)
public class IRClient {
   public IRClient(IEventBus bus, ModContainer container) {
      bus.addListener(IRClientScreens::registerMenuScreens);
      bus.addListener(IRClientFluids::registerExtensions);
      SoundUtil.setHandler(new SoundUtilClient());
      bus.addListener(this::clientLoad);
      bus.addListener(this::onRegisterBlockColorProviders);
      bus.addListener(this::onRegisterItemColorProviders);
      bus.addListener(this::onRegisterRenderers);
      net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(IRClient::registerClientCommands);
   }

   /** Dev-команда /irjei tank|reactor — открыть страницу JEI с мультиблоком. */
   @SubscribeEvent
   public static void registerClientCommands(net.neoforged.neoforge.client.event.RegisterClientCommandsEvent event) {
      event.getDispatcher().register(
         com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("irjei")
            .then(
               com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("tank")
                  .executes(ctx -> {
                     boolean ok = com.industrialreforged.tech.core.integration.jei.IRJeiPlugin.openTankMultiblock();
                     ctx.getSource().sendSuccess(() -> Component.literal(ok ? "JEI: страница резервуара открыта" : "JEI недоступен"), false);
                     return ok ? 1 : 0;
                  })
            )
            .then(
               com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("reactor")
                  .executes(ctx -> {
                     boolean ok = com.industrialreforged.tech.core.integration.jei.IRJeiPlugin.openReactorMultiblock();
                     ctx.getSource().sendSuccess(() -> Component.literal(ok ? "JEI: страница реактора открыта" : "JEI недоступен"), false);
                     return ok ? 1 : 0;
                  })
            )
      );
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void clientLoad(FMLClientSetupEvent event) {
      ItemProperties.registerGeneric(IR.location("brew"), (itemStack, clientLevel, livingEntity, n) -> {
         if (itemStack.getItem() instanceof ItemBooze booze) {
            return switch (booze.getType(itemStack)) {
               case 1 -> booze.getTime(itemStack) / 10.0F;
               case 2 -> 0.9F;
               default -> 0.0F;
            };
         } else {
            return 0.0F;
         }
      });
      ItemProperties.registerGeneric(
         IR.location("charge"),
         (itemStack, clientLevel, livingEntity, n) -> itemStack.getItem() instanceof IEnergyContainerItem energyItem
            ? Math.min(1.0F, energyItem.getRatio(itemStack))
            : 0.0F
      );
      ItemProperties.registerGeneric(
         IR.location("mode"), (itemStack, clientLevel, livingEntity, n) -> ((ItemGraviTool)IRItems.GRAVI_TOOL.get()).getToolMode(itemStack, 4) * 0.1F
      );

      for (Direction direction : Util.ALL_DIRS) {
         ItemProperties.registerGeneric(
            IR.location(direction.getName()),
            (itemStack, clientLevel, livingEntity, n) -> itemStack.getItem() instanceof ItemUpgradeModule
                  && ItemUpgradeModule.getDirection(itemStack) == direction
               ? 1.0F
               : 0.0F
         );
      }

      ItemProperties.registerGeneric(
         IR.location("active"), (itemStack, clientLevel, livingEntity, n) -> itemStack.getOrDefault(IRDataComponents.ACTIVE, false) ? 1.0F : 0.0F
      );
      ItemProperties.registerGeneric(
         IR.location("color"),
         (itemStack, clientLevel, livingEntity, n) -> itemStack.has(DataComponents.DYED_COLOR)
            ? (((DyedItemColor)itemStack.get(DataComponents.DYED_COLOR)).rgb() + 1) / 16.0F
            : 0.0F
      );
      EntityRenderers.register(IREntities.ENERGY_ARROW.get(), TippableArrowRenderer::new);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRegisterBlockColorProviders(RegisterColorHandlersEvent.Block event) {
      event.register(
         (blockState, blockAndTintGetter, blockPos, n) -> 6723908,
         new net.minecraft.world.level.block.Block[]{(net.minecraft.world.level.block.Block)IRBlocks.RUBBER_LEAVES.get()}
      );
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRegisterItemColorProviders(RegisterColorHandlersEvent.Item event) {
      event.register((itemStack, n) -> -10053308, new ItemLike[]{(ItemLike)IRItems.RUBBER_LEAVES.get()});
      event.register((itemStack, n) -> {
         if (n != 1) {
            return -1;
         }

         FluidStack fluidStack = FluidUtil.getFluidContained(itemStack).orElse(FluidStack.EMPTY);
         if (fluidStack.isEmpty()) {
            return -11184811;
         }

         Fluid fluid = fluidStack.getFluid();
         return fluid == Fluids.LAVA ? -2742503 : IClientFluidTypeExtensions.of(fluid).getTintColor();
      }, new ItemLike[]{(ItemLike)IRItems.CELL.get()});
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRegisterRenderers(RegisterRenderers registerRenderers) {
      registerRenderers.registerBlockEntityRenderer(IRBlockEntities.WIND_KINETIC_GENERATOR.get(), KineticGeneratorRenderer::new);
      registerRenderers.registerBlockEntityRenderer(IRBlockEntities.WATER_KINETIC_GENERATOR.get(), KineticGeneratorRenderer::new);
      registerRenderers.registerBlockEntityRenderer(IRBlockEntities.TANK_VALVE.get(), TankFluidRenderer::new);
   }

}
