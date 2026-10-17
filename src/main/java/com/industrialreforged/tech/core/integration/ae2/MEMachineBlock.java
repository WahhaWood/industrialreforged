package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.block.BaseEntityBlock;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

/**
 * Block of every ME machine. Horizontal facing only; the block
 * entity does its work on AE2's grid ticker, so the block has no ticker of its own.
 */
public class MEMachineBlock extends BaseEntityBlock<AbstractMENodeBlockEntity> {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private final Supplier<? extends BlockEntityType<? extends AbstractMENodeBlockEntity>> type;

   public MEMachineBlock(Supplier<? extends BlockEntityType<? extends AbstractMENodeBlockEntity>> type) {
      super(Properties.of().strength(2.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion());
      this.type = type;
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   protected BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation(state.getValue(FACING)));
   }

   @Override
   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      // AE2 security: the grid node belongs to whoever placed it
      if (placer instanceof Player player && level.getBlockEntity(pos) instanceof AbstractMENodeBlockEntity machine) {
         machine.setOwner(player);
      }
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return this.type.get().create(pos, state);
   }

   @Nullable
   @Override
   public <V extends BlockEntity> BlockEntityTicker<V> getTicker(Level level, BlockState state, BlockEntityType<V> blockEntityType) {
      return null;
   }

   @Override
   protected void addAdditionalDrops(AbstractMENodeBlockEntity entity, List<ItemStack> drops) {
      entity.addContentDrops(drops);
   }
}
