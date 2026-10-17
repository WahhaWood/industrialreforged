package com.industrialreforged.tech.block.storage;

import com.industrialreforged.tech.block.base.AbstractElectricMachineBlock;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class ElectricBlock<T extends ElectricBlockEntity> extends AbstractElectricMachineBlock<T> {
   public ElectricBlock(Supplier<BlockEntityType<T>> supplier) {
      super(supplier);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }

   protected DirectionProperty getPropertyFacing() {
      return BlockStateProperties.FACING;
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      if (level.getBlockEntity(pos) instanceof AbstractBlockEntity entity) {
         entity.updateRedstoneSignal(state, neighborPos);
      }
   }
}
