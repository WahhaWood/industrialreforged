package com.industrialreforged.tech.block.machine;

import com.industrialreforged.tech.block.base.AbstractMachineBlock;
import com.industrialreforged.tech.blockentity.machine.FermenterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FermenterBlock extends AbstractMachineBlock<FermenterBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new FermenterBlockEntity(pos, state);
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }
}
