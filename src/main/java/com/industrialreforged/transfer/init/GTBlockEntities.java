package com.industrialreforged.transfer.init;

import com.industrialreforged.transfer.block.WireBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GTBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "industrialreforged");
   public static final Supplier<BlockEntityType<WireBlockEntity>> WIRE = REGISTRY.register(
      "wire",
      () -> Builder.of(
            WireBlockEntity::new,
            new Block[]{
               (Block)GTBlocks.WIRE_1_LEVEL.get(),
               (Block)GTBlocks.WIRE_2_LEVEL.get(),
               (Block)GTBlocks.WIRE_3_LEVEL.get(),
               (Block)GTBlocks.WIRE_4_LEVEL.get(),
               (Block)GTBlocks.WIRE_5_LEVEL.get(),
               (Block)GTBlocks.WIRE_6_LEVEL.get(),
               (Block)GTBlocks.WIRE_7_LEVEL.get(),
               (Block)GTBlocks.WIRE_8_LEVEL.get(),
               (Block)GTBlocks.WIRE_9_LEVEL.get(),
               (Block)GTBlocks.WIRE_10_LEVEL.get()
            }
         )
         .build(null)
   );
}
