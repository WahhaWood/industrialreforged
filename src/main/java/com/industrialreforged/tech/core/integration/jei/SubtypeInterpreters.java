package com.industrialreforged.tech.core.integration.jei;

import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.item.ItemCropSeed;
import com.industrialreforged.tech.item.tool.ItemToolPainter;
import java.util.function.BiFunction;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SubtypeInterpreters {
   public static final ISubtypeInterpreter<ItemStack> CROPS = create((stack, context) -> {
      CropData data = ItemCropSeed.getCropData(stack);
      return data.isEmpty() ? "" : data.getCard().getId();
   });
   public static final ISubtypeInterpreter<ItemStack> PAINTS = create((stack, context) -> {
      ItemToolPainter var10000 = (ItemToolPainter)IRItems.PAINTER.get();
      return ItemToolPainter.getColor(stack).getName();
   });
   public static final ISubtypeInterpreter<ItemStack> FLUIDS = create(
      (stack, context) -> FluidUtil.getFluidContained(stack)
         .map(fluidStack -> BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()))
         .<String>map(ResourceLocation::toString)
         .orElse("")
   );
   public static final ISubtypeInterpreter<ItemStack> ENERGY = create((stack, context) -> {
      IEnergyStorage storage = (IEnergyStorage)stack.getCapability(EnergyStorage.ITEM);
      return storage == null ? "" : "energy:" + storage.getEnergyStored();
   });

   private static ISubtypeInterpreter<ItemStack> create(final BiFunction<ItemStack, UidContext, Object> function) {
      return new ISubtypeInterpreter<ItemStack>() {
         @Nullable
         public Object getSubtypeData(@NotNull ItemStack stack, @NotNull UidContext uidContext) {
            return function.apply(stack, uidContext);
         }

         @NotNull
         public String getLegacyStringSubtypeInfo(@NotNull ItemStack stack, @NotNull UidContext uidContext) {
            return function.apply(stack, uidContext).toString();
         }
      };
   }
}
