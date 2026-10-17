package com.industrialreforged.tech.block;

import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;

public class RubberWoodBlock extends Block {
   public RubberWoodBlock(Properties properties) {
      super(properties);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      ItemStack itemStack = player.getMainHandItem();
      if (itemStack.getItem() instanceof AxeItem) {
         RubberLogBlock.strip(state, level, pos, player, itemStack, ((Block)IRBlocks.STRIPPED_RUBBER_WOOD.get()).defaultBlockState());
         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }
   }
}
