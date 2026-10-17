package com.industrialreforged.tech.core.ref;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.block.BrewBarrelBlock;
import com.industrialreforged.tech.blockentity.comp.Obscuration;
import com.industrialreforged.tech.crop.IRCrops;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.item.tool.FilterData;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.DataComponents;

public class IRDataComponents {
   public static final DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "industrialreforged");
   public static final Supplier<DataComponentType<NonNullList<IRDataComponents.ItemStackRecord>>> PATTERNS = REGISTRY.registerComponentType(
      "patterns",
      builder -> builder.persistent(
         NonNullList.codecOf(ItemStack.OPTIONAL_CODEC.xmap(IRDataComponents.ItemStackRecord::new, IRDataComponents.ItemStackRecord::stack))
      )
   );
   public static final Supplier<DataComponentType<IRDataComponents.ItemStackRecord>> PATTERN = REGISTRY.registerComponentType(
      "pattern",
      builder -> builder.persistent(ItemStack.OPTIONAL_CODEC.xmap(IRDataComponents.ItemStackRecord::new, IRDataComponents.ItemStackRecord::stack))
         .networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC.map(IRDataComponents.ItemStackRecord::new, IRDataComponents.ItemStackRecord::stack))
   );
   public static final Supplier<DataComponentType<CropData>> CROP = REGISTRY.registerComponentType(
      "crop",
      builder -> builder.persistent(
            RecordCodecBuilder.create(
               inst -> inst.group(
                     Codec.STRING.xmap(IRCrops.INSTANCE::getCropCard, CropCard::getId).fieldOf("id").forGetter(CropData::getCard),
                     Codec.BYTE.fieldOf("growth").forGetter(CropData::getGrowth),
                     Codec.BYTE.fieldOf("gain").forGetter(CropData::getGain),
                     Codec.BYTE.fieldOf("resistance").forGetter(CropData::getResistance),
                     Codec.BYTE.fieldOf("scan").forGetter(CropData::getScan)
                  )
                  .apply(inst, CropData::new)
            )
         )
         .networkSynchronized(
            StreamCodec.composite(
               ByteBufCodecs.STRING_UTF8.map(IRCrops.INSTANCE::getCropCard, CropCard::getId),
               CropData::getCard,
               ByteBufCodecs.BYTE,
               CropData::getGrowth,
               ByteBufCodecs.BYTE,
               CropData::getGain,
               ByteBufCodecs.BYTE,
               CropData::getResistance,
               ByteBufCodecs.BYTE,
               CropData::getScan,
               CropData::new
            )
         )
   );
   public static final Supplier<DataComponentType<Boolean>> ACTIVE = REGISTRY.registerComponentType(
      "active", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
   );
   public static final Supplier<DataComponentType<Boolean>> HOVER = REGISTRY.registerComponentType(
      "hover", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
   );
   public static final Supplier<DataComponentType<Byte>> MODE = REGISTRY.registerComponentType(
      "mode", builder -> builder.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE)
   );
   public static final Supplier<DataComponentType<FilterData>> ITEM_FILTER = REGISTRY.registerComponentType(
      "item_filter", builder -> builder.persistent(FilterData.CODEC).networkSynchronized(FilterData.STREAM_CODEC)
   );
   public static final Supplier<DataComponentType<Long>> TURNING_STATES = REGISTRY.registerComponentType(
      "turning_states", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
   );
   public static final Supplier<DataComponentType<BrewBarrelBlock.BrewBoozeData>> BOOZE = REGISTRY.registerComponentType(
      "booze", builder -> builder.persistent(BrewBarrelBlock.BrewBoozeData.CODEC)
   );
   public static final Supplier<DataComponentType<Direction>> DIRECTION = REGISTRY.registerComponentType(
      "direction", builder -> builder.persistent(Direction.CODEC).networkSynchronized(Direction.STREAM_CODEC)
   );
   public static final Supplier<DataComponentType<BlockPos>> LINKED = REGISTRY.registerComponentType(
      "linked", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC)
   );
   /** Измерение связанного телепортера (для межмировых связей). */
   public static final Supplier<DataComponentType<net.minecraft.resources.ResourceLocation>> LINKED_DIMENSION = REGISTRY.registerComponentType(
      "linked_dimension",
      builder -> builder.persistent(net.minecraft.resources.ResourceLocation.CODEC)
         .networkSynchronized(net.minecraft.resources.ResourceLocation.STREAM_CODEC)
   );
   public static final Supplier<DataComponentType<Obscuration.ObscurationData>> OBSCURATION = REGISTRY.registerComponentType(
      "obscuration", builder -> builder.persistent(Obscuration.ObscurationData.CODEC)
   );

   public record ItemStackRecord(ItemStack stack) {
      public static final IRDataComponents.ItemStackRecord EMPTY = new IRDataComponents.ItemStackRecord(ItemStack.EMPTY);
   }
}
