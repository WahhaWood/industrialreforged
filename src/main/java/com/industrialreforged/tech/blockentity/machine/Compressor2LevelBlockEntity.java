package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Compressor2LevelBlockEntity extends Compressor1LevelBlockEntity {
   public Compressor2LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2, 5, IRBlockEntities.COMPRESSOR_2_LEVEL.get(), blockPos, blockState);
   }
}
