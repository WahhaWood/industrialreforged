package com.industrialreforged.tech.multiblock.tank;

import com.industrialreforged.block.ActiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class TankWallBlock extends Block {
   public TankWallBlock() {
      super(BlockBehaviour.Properties.of()
         .strength(3.0F, 15.0F)
         .requiresCorrectToolForDrops()
         .sound(SoundType.METAL));
      this.registerDefaultState(this.defaultBlockState().setValue(ActiveBlock.ACTIVE, false));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ActiveBlock.ACTIVE});
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && !level.isClientSide) {
         notifyNearbyValves(level, pos);
      }
      super.onRemove(state, level, pos, newState, isMoving);
   }

   public static void notifyNearbyValves(Level level, BlockPos brokenPos) {
      // Через реестр собранных клапанов: находит свой танк в любом размере без скана округи
      TankValveBlockEntity.disbandTanksContaining(level, brokenPos);
   }
}
