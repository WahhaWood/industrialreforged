package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricCESUBlockEntity extends ElectricBlockEntity {
   public static final int CAPACITY = 16000000;
   public static final int RECEIVE = 8192;
   public static final int EXTRACT = 4096;

   public ElectricCESUBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(CAPACITY, RECEIVE, EXTRACT, IRBlockEntities.CESU.get(), blockPos, blockState);
   }
}
