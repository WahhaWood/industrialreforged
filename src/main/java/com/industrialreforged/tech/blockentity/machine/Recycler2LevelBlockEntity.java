package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Recycler2LevelBlockEntity extends Recycler1LevelBlockEntity {
   public Recycler2LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2, 5, IRBlockEntities.RECYCLER_2_LEVEL.get(), blockPos, blockState);
   }
}
