package com.industrialreforged.tech.api.crops;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum CropSoilType {
   FARMLAND(Blocks.FARMLAND),
   MYCELIUM(Blocks.MYCELIUM),
   SAND(Blocks.SAND),
   SOULSAND(Blocks.SOUL_SAND);

   private final Block block;

   CropSoilType(Block paramBlock) {
      if (paramBlock == null) {
         throw new NullPointerException("null block");
      }

      this.block = paramBlock;
   }

   public Block getBlock() {
      return this.block;
   }

   public static boolean contains(Block paramBlock) {
      for (CropSoilType cropSoilType : values()) {
         if (cropSoilType.getBlock() == paramBlock) {
            return true;
         }
      }

      return false;
   }
}
