package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Macerator3LevelBlockEntity extends Macerator1LevelBlockEntity {
   public Macerator3LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(3, 6, IRBlockEntities.MACERATOR_3_LEVEL.get(), blockPos, blockState);
   }
}
