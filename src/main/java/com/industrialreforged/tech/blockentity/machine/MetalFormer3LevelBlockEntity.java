package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MetalFormer3LevelBlockEntity extends MetalFormer1LevelBlockEntity {
   public MetalFormer3LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(3, 6, IRBlockEntities.METAL_FORMER_3_LEVEL.get(), blockPos, blockState);
   }
}
