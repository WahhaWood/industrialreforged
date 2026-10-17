package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Compressor3LevelBlockEntity extends Compressor1LevelBlockEntity {
   public Compressor3LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(3, 6, IRBlockEntities.COMPRESSOR_3_LEVEL.get(), blockPos, blockState);
   }
}
