package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricEFSUBlockEntity extends ElectricBlockEntity {
   // ×16 от 1G не влезает в int-хранилище (16G > 2.1G): полка на максимуме int.
   public static final int CAPACITY = Integer.MAX_VALUE;
   public static final int RECEIVE = 1024000;
   public static final int EXTRACT = 512000;

   public ElectricEFSUBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(CAPACITY, RECEIVE, EXTRACT, IRBlockEntities.EFSU.get(), blockPos, blockState);
   }
}
