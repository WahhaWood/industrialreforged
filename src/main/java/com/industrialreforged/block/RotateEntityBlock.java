package com.industrialreforged.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public abstract class RotateEntityBlock<T extends BlockEntity> extends BaseEntityBlock<T> {
   public RotateEntityBlock() {
      this(Properties.of());
   }

   public RotateEntityBlock(Properties properties) {
      super(properties);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{this.getPropertyFacing()});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      DirectionProperty facing = this.getPropertyFacing();
      return (BlockState)this.defaultBlockState()
         .setValue(
            facing,
            (facing == BlockStateProperties.HORIZONTAL_FACING
                  ? context.getHorizontalDirection()
                  : (facing == BlockStateProperties.VERTICAL_DIRECTION ? context.getNearestLookingVerticalDirection() : context.getNearestLookingDirection()))
               .getOpposite()
         );
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      DirectionProperty facing = this.getPropertyFacing();
      return (BlockState)state.setValue(facing, rotation.rotate((Direction)state.getValue(facing)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      DirectionProperty facing = this.getPropertyFacing();
      return (BlockState)state.setValue(facing, mirror.mirror((Direction)state.getValue(facing)));
   }

   protected DirectionProperty getPropertyFacing() {
      return BlockStateProperties.HORIZONTAL_FACING;
   }
}
