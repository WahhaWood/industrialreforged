package com.industrialreforged.tech.block.generator.kinetic;

import com.industrialreforged.tech.block.base.AbstractGeneratorBlock;
import com.industrialreforged.tech.blockentity.generator.kinetic.ElectricKineticGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricKineticGeneratorBlock extends AbstractGeneratorBlock<ElectricKineticGeneratorBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ElectricKineticGeneratorBlockEntity(pos, state);
   }
}
