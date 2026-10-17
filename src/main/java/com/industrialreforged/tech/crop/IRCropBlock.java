package com.industrialreforged.tech.crop;

import com.industrialreforged.block.BaseEntityBlock;
import com.industrialreforged.tech.api.block.BreakableBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IRCropBlock extends BaseEntityBlock<CropBlockEntity> implements BreakableBlock {
   public static final BooleanProperty CROSSING_BASE = BooleanProperty.create("crossing_base");
   private static final Properties PROPERTIES = Properties.of().strength(0.8F, 0.2F).sound(SoundType.CROP).noCollission();
   private static IRCropType init;
   private final IRCropType cropType;
   private final Supplier<BlockEntityType<CropBlockEntity>> supplier;

   private IRCropBlock(Supplier<BlockEntityType<CropBlockEntity>> supplier) {
      super(PROPERTIES);
      this.cropType = init;
      this.supplier = supplier;
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(CROSSING_BASE, false));
   }

   public IRCropType getType() {
      return this.cropType;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      IntegerProperty property = init.getProperty();
      if (property != null) {
         builder.add(new Property[]{property});
      }

      builder.add(new Property[]{CROSSING_BASE});
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return this.supplier.get().create(pos, state);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      CropBlockEntity crop = (CropBlockEntity)this.getBlockEntity(level, pos);
      if (crop == null) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      InteractionResult result = crop.onActivated(player, hand, hitResult.getDirection(), hitResult.getLocation());
      return result.consumesAction() ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   @Override
   public InteractionResult startBreak(Player player, Level level, InteractionHand hand, BlockPos pos, BlockState state, Direction direction) {
      CropBlockEntity crop = (CropBlockEntity)this.getBlockEntity(level, pos);
      if (crop == null) {
         return InteractionResult.PASS;
      } else {
         return false ? InteractionResult.PASS : crop.onClicked(player, direction);
      }
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
      CropBlockEntity crop = (CropBlockEntity)this.getBlockEntity(level, pos);
      if (crop != null) {
         crop.onNeighborChange(state, neighborPos);
      }
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      CropBlockEntity crop = (CropBlockEntity)this.getBlockEntity(level, pos);
      if (crop != null) {
         crop.onBlockBreak();
      }

      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
   }

   public static synchronized IRCropBlock create(Supplier<BlockEntityType<CropBlockEntity>> supplier, IRCropType type) {
      init = type;
      IRCropBlock block = new IRCropBlock(supplier);
      init = null;
      return block;
   }
}
