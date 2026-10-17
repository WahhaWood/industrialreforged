package com.industrialreforged.tech.block.machine;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.block.base.AbstractMachineBlock;
import com.industrialreforged.tech.blockentity.machine.IronFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class IronFurnaceBlock extends AbstractMachineBlock<IronFurnaceBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new IronFurnaceBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return BaseBlockEntity.commonTick();
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }
}
