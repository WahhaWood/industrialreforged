package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MetalFormer4LevelBlockEntity extends MetalFormer1LevelBlockEntity {
   public MetalFormer4LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(4, 8, IRBlockEntities.METAL_FORMER_4_LEVEL.get(), blockPos, blockState);
   }
}
