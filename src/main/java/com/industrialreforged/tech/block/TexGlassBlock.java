package com.industrialreforged.tech.block;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.tech.multiblock.tank.TankWallBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TexGlassBlock extends TintedGlassBlock {
   public TexGlassBlock() {
      super(Properties.of().noOcclusion().strength(5.0F, 180.0F).sound(SoundType.GLASS).isValidSpawn((blockState, blockGetter, blockPos, entityType) -> false));
      this.registerDefaultState(this.defaultBlockState().setValue(ActiveBlock.ACTIVE, false));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{ActiveBlock.ACTIVE});
   }

   public VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return Shapes.empty();
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      // укреплённое стекло — допустимая оболочка резервуара: поломка распускает танк
      if (!state.is(newState.getBlock()) && !level.isClientSide) {
         TankWallBlock.notifyNearbyValves(level, pos);
      }
      super.onRemove(state, level, pos, newState, isMoving);
   }
}
