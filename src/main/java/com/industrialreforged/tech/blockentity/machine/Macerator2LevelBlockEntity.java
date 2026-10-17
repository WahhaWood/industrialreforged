package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Macerator2LevelBlockEntity extends Macerator1LevelBlockEntity {
   public Macerator2LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2, 5, IRBlockEntities.MACERATOR_2_LEVEL.get(), blockPos, blockState);
   }
}
