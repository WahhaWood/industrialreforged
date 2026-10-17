package com.industrialreforged.tech.block.reactor;

import com.industrialreforged.tech.block.base.AbstractMachineBlock;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.blockentity.reactor.NuclearReactorElectricBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorElectricBlock extends AbstractMachineBlock<NuclearReactorElectricBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new NuclearReactorElectricBlockEntity(pos, state);
   }

   @Override
   public float getWrenchDropRate() {
      return 0.8F;
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      // дорогая машина: падает собой даже без ключа
      return this.asItem().getDefaultInstance();
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      if (level.getBlockEntity(pos) instanceof AbstractBlockEntity entity) {
         entity.updateRedstoneSignal(state, neighborPos);
      }
   }
}
