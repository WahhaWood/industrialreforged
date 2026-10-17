package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricMFEBlockEntity extends ElectricBlockEntity {
   public static final int CAPACITY = 64000000;
   public static final int RECEIVE = 64000;
   public static final int EXTRACT = 32000;

   public ElectricMFEBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(CAPACITY, RECEIVE, EXTRACT, IRBlockEntities.MFE.get(), blockPos, blockState);
   }
}
