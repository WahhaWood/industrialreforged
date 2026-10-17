package com.industrialreforged.transfer.init;

import com.industrialreforged.transfer.block.WireBlock;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class GTBlocks {
   public static final Blocks REGISTRY = DeferredRegister.createBlocks("industrialreforged");
   public static final DeferredBlock<WireBlock> WIRE_1_LEVEL = register(1, 200);
   public static final DeferredBlock<WireBlock> WIRE_2_LEVEL = register(2, 800);
   public static final DeferredBlock<WireBlock> WIRE_3_LEVEL = register(3, 8000);
   public static final DeferredBlock<WireBlock> WIRE_4_LEVEL = register(4, 32000);
   public static final DeferredBlock<WireBlock> WIRE_5_LEVEL = register(5, 100000);
   public static final DeferredBlock<WireBlock> WIRE_6_LEVEL = register(6, 1000000);
   public static final DeferredBlock<WireBlock> WIRE_7_LEVEL = register(7, 10000000);
   public static final DeferredBlock<WireBlock> WIRE_8_LEVEL = register(8, 100000000);
   public static final DeferredBlock<WireBlock> WIRE_9_LEVEL = register(9, 1000000000);
   public static final DeferredBlock<WireBlock> WIRE_10_LEVEL = REGISTRY.register("wire_10_level", () -> new WireBlock(SoundType.GLASS, 10, -1));

   private static DeferredBlock<WireBlock> register(int level, int transfer) {
      return REGISTRY.register("wire_" + level + "_level", () -> new WireBlock(level, transfer));
   }

   public static final java.util.List<DeferredBlock<WireBlock>> WIRES = java.util.List.of(
      WIRE_1_LEVEL, WIRE_2_LEVEL, WIRE_3_LEVEL, WIRE_4_LEVEL, WIRE_5_LEVEL,
      WIRE_6_LEVEL, WIRE_7_LEVEL, WIRE_8_LEVEL, WIRE_9_LEVEL, WIRE_10_LEVEL
   );

   /** Returns the wire block of the given level, or null when out of range. */
   public static WireBlock getWire(int level) {
      return level >= 1 && level <= WIRES.size() ? WIRES.get(level - 1).get() : null;
   }
}
