package com.industrialreforged.tech.block.generator.heat;

import com.industrialreforged.tech.block.base.AbstractGeneratorBlock;
import com.industrialreforged.tech.blockentity.generator.heat.ElectricHeatGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricHeatGeneratorBlock extends AbstractGeneratorBlock<ElectricHeatGeneratorBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ElectricHeatGeneratorBlockEntity(pos, state);
   }
}
