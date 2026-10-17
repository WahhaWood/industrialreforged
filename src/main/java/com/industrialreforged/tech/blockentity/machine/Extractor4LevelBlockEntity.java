package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Extractor4LevelBlockEntity extends Extractor1LevelBlockEntity {
   public Extractor4LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(4, 8, IRBlockEntities.EXTRACTOR_4_LEVEL.get(), blockPos, blockState);
   }
}
