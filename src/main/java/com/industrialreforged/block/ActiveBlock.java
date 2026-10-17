package com.industrialreforged.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class ActiveBlock extends BaseBlock {
   public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

   public ActiveBlock() {
      this(Properties.of());
   }

   public ActiveBlock(Properties properties) {
      super(properties);
      // BooleanProperty defaults to "true"; machines must start out inactive.
      if (this.defaultBlockState().hasProperty(ACTIVE)) {
         this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ACTIVE});
   }

   public static boolean isActive(BlockEntity blockEntity) {
      return isActive(blockEntity.getBlockState());
   }

   public static boolean isActive(BlockState blockState) {
      return blockState.hasProperty(ACTIVE) && (Boolean)blockState.getValue(ACTIVE);
   }

   public static boolean isActive(Level level, BlockPos blockPos) {
      return isActive(level.getBlockState(blockPos));
   }

   public static void setActive(BlockEntity blockEntity, boolean value) {
      setActive(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), value);
   }

   public static void setActive(Level level, BlockPos blockPos, boolean value) {
      setActive(level, blockPos, level.getBlockState(blockPos), value);
   }

   public static void setActive(Level level, BlockPos blockPos, BlockState blockState, boolean value) {
      if (!blockState.hasProperty(ACTIVE)) {
         return;
      }

      if (isActive(blockState) != value) {
         level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(ACTIVE, value));
      }
   }
}
