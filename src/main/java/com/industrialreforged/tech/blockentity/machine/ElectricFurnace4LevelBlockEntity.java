package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricFurnace4LevelBlockEntity extends ElectricFurnace1LevelBlockEntity {
   public ElectricFurnace4LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(4, 8, IRBlockEntities.ELECTRIC_FURNACE_4_LEVEL.get(), blockPos, blockState);
   }
}
