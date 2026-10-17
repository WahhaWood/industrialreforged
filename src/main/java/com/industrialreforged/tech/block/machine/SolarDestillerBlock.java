package com.industrialreforged.tech.block.machine;

import com.industrialreforged.block.BaseEntityBlock;
import com.industrialreforged.tech.api.tile.IWrenchable;
import com.industrialreforged.tech.blockentity.machine.SolarDestillerBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class SolarDestillerBlock extends BaseEntityBlock<SolarDestillerBlockEntity> implements IWrenchable {
   public static final BooleanProperty NORTH = BooleanProperty.create("north");
   public static final BooleanProperty EAST = BooleanProperty.create("east");
   public static final BooleanProperty SOUTH = BooleanProperty.create("south");
   public static final BooleanProperty WEST = BooleanProperty.create("west");

   public SolarDestillerBlock() {
      super(Properties.of().strength(2.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
      this.registerDefaultState(this.defaultBlockState().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, EAST, SOUTH, WEST});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.linkedState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
   }

   @Override
   protected BlockState updateShape(
      BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos
   ) {
      BooleanProperty property = propertyFor(direction);
      if (property != null) {
         if (level instanceof net.minecraft.server.level.ServerLevel serverLevel
            && neighborState.getBlock() instanceof SolarDestillerBlock) {
            // the neighbour may have joined (or left) this wall of distillers
            SolarDestillerBlockEntity.markGroupDirty(serverLevel, neighborPos);
         }

         return state.setValue(property, neighborState.getBlock() instanceof SolarDestillerBlock);
      }

      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   private BlockState linkedState(BlockState state, BlockGetter level, BlockPos pos) {
      BlockState result = state;

      for (Direction direction : Direction.Plane.HORIZONTAL) {
         BlockState neighbor = level.getBlockState(pos.relative(direction));
         result = result.setValue(propertyFor(direction), neighbor.getBlock() instanceof SolarDestillerBlock);
      }

      return result;
   }

   @Nullable
   public static BooleanProperty propertyFor(Direction direction) {
      return switch (direction) {
         case NORTH -> NORTH;
         case EAST -> EAST;
         case SOUTH -> SOUTH;
         case WEST -> WEST;
         default -> null;
      };
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SolarDestillerBlockEntity(pos, state);
   }

   @Override
   public float getWrenchDropRate() {
      return 0.9F;
   }

   protected ItemStack getInstantDrop(BlockState blockState, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return this.getWrenchDrops(blockState, builder);
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return IRBlocks.MACHINE.toStack();
   }
}
