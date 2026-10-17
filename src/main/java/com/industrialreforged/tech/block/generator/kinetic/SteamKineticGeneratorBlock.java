package com.industrialreforged.tech.block.generator.kinetic;

import com.industrialreforged.tech.block.base.AbstractGeneratorBlock;
import com.industrialreforged.tech.blockentity.generator.kinetic.SteamKineticGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SteamKineticGeneratorBlock extends AbstractGeneratorBlock<SteamKineticGeneratorBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SteamKineticGeneratorBlockEntity(pos, state);
   }
}
