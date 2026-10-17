package com.industrialreforged.init;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GEDataComponents {
   public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "industrialreforged");
   public static final Supplier<DataComponentType<Integer>> ENERGY = register(
      "energy", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
   );
   public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID = register(
      "fluid", builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC)
   );

   private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<Builder<T>> operator) {
      return REGISTRY.register(name, () -> operator.apply(DataComponentType.builder()).build());
   }
}
