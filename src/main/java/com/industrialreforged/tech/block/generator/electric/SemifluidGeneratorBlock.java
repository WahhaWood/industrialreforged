package com.industrialreforged.tech.block.generator.electric;

import com.industrialreforged.tech.block.base.AbstractGeneratorBlock;
import com.industrialreforged.tech.blockentity.generator.electric.SemifluidGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SemifluidGeneratorBlock extends AbstractGeneratorBlock<SemifluidGeneratorBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SemifluidGeneratorBlockEntity(pos, state);
   }
}
