package com.industrialreforged.tech.block;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.tech.multiblock.tank.TankWallBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

/** Укреплённый камень — допустимая оболочка резервуара: поломка распускает танк. */
public class ReinforcedCasingBlock extends Block {
   public ReinforcedCasingBlock() {
      super(Properties.of().strength(40.0F, 180.0F).requiresCorrectToolForDrops());
      this.registerDefaultState(this.defaultBlockState().setValue(ActiveBlock.ACTIVE, false));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{ActiveBlock.ACTIVE});
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && !level.isClientSide) {
         TankWallBlock.notifyNearbyValves(level, pos);
      }
      super.onRemove(state, level, pos, newState, isMoving);
   }
}
