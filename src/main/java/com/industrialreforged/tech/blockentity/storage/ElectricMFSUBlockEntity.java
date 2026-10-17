package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricMFSUBlockEntity extends ElectricBlockEntity {
   public static final int CAPACITY = 256000000;
   public static final int RECEIVE = 256000;
   public static final int EXTRACT = 128000;

   public ElectricMFSUBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(CAPACITY, RECEIVE, EXTRACT, IRBlockEntities.MFSU.get(), blockPos, blockState);
   }
}
