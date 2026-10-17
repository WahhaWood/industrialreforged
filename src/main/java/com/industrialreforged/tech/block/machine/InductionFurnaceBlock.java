package com.industrialreforged.tech.block.machine;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.block.base.AbstractMachineBlock;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.blockentity.machine.InductionFurnaceBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InductionFurnaceBlock extends AbstractMachineBlock<InductionFurnaceBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new InductionFurnaceBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return BaseBlockEntity.commonTick();
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return IRBlocks.ADVANCED_MACHINE.toStack();
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      if (level.getBlockEntity(pos) instanceof AbstractBlockEntity entity) {
         entity.updateRedstoneSignal(state, neighborPos);
      }
   }
}
