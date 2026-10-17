package com.industrialreforged.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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

public class RotateBlock extends BaseBlock {
   public RotateBlock() {
      this(Properties.of());
   }

   public RotateBlock(Properties properties) {
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

   public static Direction getFacing(BlockEntity blockEntity) {
      return getFacing(blockEntity.getBlockState());
   }

   public static Direction getFacing(BlockState blockState) {
      return (Direction)blockState.getValue(BlockStateProperties.FACING);
   }

   public static Direction getFacing(Level level, BlockPos blockPos) {
      return getFacing(level.getBlockState(blockPos));
   }

   public static Direction getVerticalFacing(BlockEntity blockEntity) {
      return getVerticalFacing(blockEntity.getBlockState());
   }

   public static Direction getVerticalFacing(BlockState blockState) {
      return (Direction)blockState.getValue(BlockStateProperties.VERTICAL_DIRECTION);
   }

   public static Direction getVerticalFacing(Level level, BlockPos blockPos) {
      return getVerticalFacing(level.getBlockState(blockPos));
   }

   public static Direction getHorizontalFacing(BlockEntity blockEntity) {
      return getHorizontalFacing(blockEntity.getBlockState());
   }

   public static Direction getHorizontalFacing(BlockState blockState) {
      return (Direction)blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
   }

   public static Direction getHorizontalFacing(Level level, BlockPos blockPos) {
      return getHorizontalFacing(level.getBlockState(blockPos));
   }

   public static void setFacing(BlockEntity blockEntity, Direction value) {
      setFacing(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), value);
   }

   public static void setFacing(Level level, BlockPos blockPos, Direction value) {
      setFacing(level, blockPos, level.getBlockState(blockPos), value);
   }

   public static void setFacing(Level level, BlockPos blockPos, BlockState blockState, Direction value) {
      if (getFacing(blockState) != value) {
         level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(BlockStateProperties.FACING, value));
      }
   }

   public static void setVerticalFacing(BlockEntity blockEntity, Direction value) {
      setVerticalFacing(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), value);
   }

   public static void setVerticalFacing(Level level, BlockPos blockPos, Direction value) {
      setVerticalFacing(level, blockPos, level.getBlockState(blockPos), value);
   }

   public static void setVerticalFacing(Level level, BlockPos blockPos, BlockState blockState, Direction value) {
      if (getVerticalFacing(blockState) != value) {
         level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(BlockStateProperties.VERTICAL_DIRECTION, value));
      }
   }

   public static void setHorizontalFacing(BlockEntity blockEntity, Direction value) {
      setHorizontalFacing(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), value);
   }

   public static void setHorizontalFacing(Level level, BlockPos blockPos, Direction value) {
      setHorizontalFacing(level, blockPos, level.getBlockState(blockPos), value);
   }

   public static void setHorizontalFacing(Level level, BlockPos blockPos, BlockState blockState, Direction value) {
      if (getHorizontalFacing(blockState) != value) {
         level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, value));
      }
   }
}
