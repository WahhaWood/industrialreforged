package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Compressor4LevelBlockEntity extends Compressor1LevelBlockEntity {
   public Compressor4LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(4, 8, IRBlockEntities.COMPRESSOR_4_LEVEL.get(), blockPos, blockState);
   }
}
