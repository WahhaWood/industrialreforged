package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Extractor3LevelBlockEntity extends Extractor1LevelBlockEntity {
   public Extractor3LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(3, 6, IRBlockEntities.EXTRACTOR_3_LEVEL.get(), blockPos, blockState);
   }
}
