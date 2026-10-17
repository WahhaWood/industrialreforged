package com.industrialreforged.tech.block.generator.electric;

import com.industrialreforged.tech.block.base.AbstractGeneratorBlock;
import com.industrialreforged.tech.blockentity.generator.electric.StirlingGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StirlingGeneratorBlock extends AbstractGeneratorBlock<StirlingGeneratorBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new StirlingGeneratorBlockEntity(pos, state);
   }
}
