package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricFurnace3LevelBlockEntity extends ElectricFurnace1LevelBlockEntity {
   public ElectricFurnace3LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(3, 6, IRBlockEntities.ELECTRIC_FURNACE_3_LEVEL.get(), blockPos, blockState);
   }
}
