package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MetalFormer2LevelBlockEntity extends MetalFormer1LevelBlockEntity {
   public MetalFormer2LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2, 5, IRBlockEntities.METAL_FORMER_2_LEVEL.get(), blockPos, blockState);
   }
}
