package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.core.ref.IRItems;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CropStickBlock extends Block {
   public static final BooleanProperty CROSSING_BASE = BooleanProperty.create("crossing_base");

   public CropStickBlock(Properties paramProperties) {
      super(paramProperties);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{CROSSING_BASE});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)Objects.requireNonNull(super.getStateForPlacement(context)).setValue(CROSSING_BASE, false);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.is((Item)IRItems.CROP_STICK.get())) {
         if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(CROSSING_BASE, true));
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }
}
