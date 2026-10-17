package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Macerator4LevelBlockEntity extends Macerator1LevelBlockEntity {
   public Macerator4LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(4, 8, IRBlockEntities.MACERATOR_4_LEVEL.get(), blockPos, blockState);
   }
}
