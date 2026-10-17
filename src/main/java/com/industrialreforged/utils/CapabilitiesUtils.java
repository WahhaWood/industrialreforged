package com.industrialreforged.utils;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.item.api.IFluidContainerItem;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CapabilitiesUtils {
   public static <T extends BlockEntity & IContainerWrapper> void registerContainerBlock(RegisterCapabilitiesEvent event, Supplier<BlockEntityType<T>> supplier) {
      event.registerBlockEntity(ItemHandler.BLOCK, supplier.get(), (rec$, x$0) -> ((IContainerWrapper)rec$).getContainerWrapper(x$0));
   }

   public static <T extends BlockEntity & IEnergyWrapper> void registerEnergyStorageBlock(
      RegisterCapabilitiesEvent event, Supplier<BlockEntityType<T>> supplier
   ) {
      event.registerBlockEntity(EnergyStorage.BLOCK, supplier.get(), (rec$, x$0) -> ((IEnergyWrapper)rec$).getEnergyWrapper(x$0));
   }

   public static <T extends BlockEntity & IFluidWrapper> void registerFluidHandlerBlock(RegisterCapabilitiesEvent event, Supplier<BlockEntityType<T>> supplier) {
      event.registerBlockEntity(FluidHandler.BLOCK, supplier.get(), (rec$, x$0) -> ((IFluidWrapper)rec$).getFluidWrapper(x$0));
   }

   @Nullable
   public static <T, C> T get(BlockEntity blockEntity, BlockCapability<T, C> capability, C context) {
      Level level = blockEntity.getLevel();
      return (T)(level != null ? level.getCapability(capability, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, context) : null);
   }

   public static void registerEnergyContainerItem(RegisterCapabilitiesEvent event, Items items) {
      IEnergyContainerItem[] result = items.getEntries()
         .stream()
         .<Item>map(DeferredHolder::get)
         .filter(item -> item instanceof IEnergyContainerItem)
         .toArray(IEnergyContainerItem[]::new);
      if (result.length > 0) {
         event.registerItem(
            EnergyStorage.ITEM,
            (object, context) -> {
               IEnergyContainerItem item = (IEnergyContainerItem)object.getItem();
               return new ComponentEnergyStorage(
                  object, GEDataComponents.ENERGY.get(), item.getEnergyCapacity(object), item.getEnergyReceiveTick(object), item.getEnergyExtractTick(object)
               );
            },
            result
         );
      }
   }

   public static void registerFluidContainerItem(RegisterCapabilitiesEvent event, Items items) {
      IFluidContainerItem[] result = items.getEntries()
         .stream()
         .<Item>map(DeferredHolder::get)
         .filter(item -> item instanceof IFluidContainerItem)
         .toArray(IFluidContainerItem[]::new);
      if (result.length > 0) {
         event.registerItem(FluidHandler.ITEM, (object, context) -> {
            final IFluidContainerItem item = (IFluidContainerItem)object.getItem();
            return new FluidHandlerItemStack(GEDataComponents.FLUID, object, item.getFluidCapacity(object)) {
               public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                  return item.isFluidValid(stack);
               }
            };
         }, result);
      }
   }
}
