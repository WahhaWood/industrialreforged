package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Recycler3LevelBlockEntity extends Recycler1LevelBlockEntity {
   public Recycler3LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(3, 6, IRBlockEntities.RECYCLER_3_LEVEL.get(), blockPos, blockState);
   }
}
