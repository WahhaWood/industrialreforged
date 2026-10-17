package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricFurnace2LevelBlockEntity extends ElectricFurnace1LevelBlockEntity {
   public ElectricFurnace2LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2, 5, IRBlockEntities.ELECTRIC_FURNACE_2_LEVEL.get(), blockPos, blockState);
   }
}
