package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricBatBoxBlockEntity extends ElectricBlockEntity {
   public static final int CAPACITY = 4000000;
   public static final int RECEIVE = 2048;
   public static final int EXTRACT = 1024;

   public ElectricBatBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(CAPACITY, RECEIVE, EXTRACT, IRBlockEntities.BATBOX.get(), blockPos, blockState);
   }
}
