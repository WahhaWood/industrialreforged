package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Recycler4LevelBlockEntity extends Recycler1LevelBlockEntity {
   public Recycler4LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(4, 8, IRBlockEntities.RECYCLER_4_LEVEL.get(), blockPos, blockState);
   }
}
