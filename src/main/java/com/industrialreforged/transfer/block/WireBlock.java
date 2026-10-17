package com.industrialreforged.transfer.block;

import com.industrialreforged.block.BaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WireBlock extends BaseEntityBlock<WireBlockEntity> {
   public final int level;
   public final int transfer;

   public WireBlock(int level, int transfer) {
      this(SoundType.METAL, level, transfer);
   }

   public WireBlock(SoundType soundType, int level, int transfer) {
      super(Properties.of().strength(1.0F, 10.0F).sound(soundType).noOcclusion().dynamicShape().forceSolidOn());
      this.level = level;
      this.transfer = transfer;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      // holding a wire item: do not interfere, any other item falls through to
      // useWithoutItem so the configuration GUI can open
      return stack.getItem() instanceof WireBlockItem
         ? ItemInteractionResult.FAIL
         : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new WireBlockEntity(pos, state);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
      Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type
   ) {
      return com.industrialreforged.blockentity.BaseBlockEntity.serverTick(level);
   }

   @Override
   protected net.minecraft.world.InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult
   ) {
      if (player.isShiftKeyDown()) {
         return net.minecraft.world.InteractionResult.PASS;
      }

      if (level.getBlockEntity(pos) instanceof WireBlockEntity cable) {
         if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            Direction direction = hitResult.getDirection();
            serverPlayer.openMenu(
               new net.minecraft.world.SimpleMenuProvider(
                  (id, inventory, p) -> new com.industrialreforged.transfer.core.WireMenu(id, inventory, cable, direction),
                  this.getName()
               ),
               buf -> {
                  buf.writeBlockPos(pos);
                  buf.writeByte(direction.ordinal());
               }
            );
         }

         return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
      }

      return net.minecraft.world.InteractionResult.PASS;
   }

   public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return getter.getBlockEntity(pos) instanceof WireBlockEntity cable ? cable.getShape() : Shapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return getter.getBlockEntity(pos) instanceof WireBlockEntity cable ? cable.getCollisionShape(context) : Shapes.empty();
   }

   public BlockState updateShape(BlockState blockState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if (level.getBlockEntity(currentPos) instanceof WireBlockEntity cable) {
         cable.invalidateShapes();
      }

      return super.updateShape(blockState, facing, facingState, level, currentPos, facingPos);
   }
}
