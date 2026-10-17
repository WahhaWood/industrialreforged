package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Extractor2LevelBlockEntity extends Extractor1LevelBlockEntity {
   public Extractor2LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2, 5, IRBlockEntities.EXTRACTOR_2_LEVEL.get(), blockPos, blockState);
   }
}
