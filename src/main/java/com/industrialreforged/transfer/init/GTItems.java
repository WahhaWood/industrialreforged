package com.industrialreforged.transfer.init;

import com.industrialreforged.transfer.block.WireBlock;
import com.industrialreforged.transfer.block.WireBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class GTItems {
   public static final Items REGISTRY = DeferredRegister.createItems("industrialreforged");
   public static final DeferredItem<BlockItem> WIRE_1_LEVEL = register(1, Rarity.COMMON, GTBlocks.WIRE_1_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_2_LEVEL = register(2, Rarity.COMMON, GTBlocks.WIRE_2_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_3_LEVEL = register(3, Rarity.COMMON, GTBlocks.WIRE_3_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_4_LEVEL = register(4, Rarity.UNCOMMON, GTBlocks.WIRE_4_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_5_LEVEL = register(5, Rarity.UNCOMMON, GTBlocks.WIRE_5_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_6_LEVEL = register(6, Rarity.UNCOMMON, GTBlocks.WIRE_6_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_7_LEVEL = register(7, Rarity.RARE, GTBlocks.WIRE_7_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_8_LEVEL = register(8, Rarity.RARE, GTBlocks.WIRE_8_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_9_LEVEL = register(9, Rarity.RARE, GTBlocks.WIRE_9_LEVEL);
   public static final DeferredItem<BlockItem> WIRE_10_LEVEL = register(10, Rarity.EPIC, GTBlocks.WIRE_10_LEVEL);

   private static DeferredItem<BlockItem> register(int level, Rarity rarity, DeferredBlock<WireBlock> object) {
      return REGISTRY.register("wire_" + level + "_level", () -> new WireBlockItem((WireBlock)object.get(), rarity));
   }
}
