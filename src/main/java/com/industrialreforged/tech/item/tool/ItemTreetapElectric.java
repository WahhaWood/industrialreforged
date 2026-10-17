package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.item.ElectricProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemTreetapElectric extends ItemElectricTool {
   public ItemTreetapElectric(ElectricProperties properties) {
      super(properties, 50, 40000, 128);
   }

   @Override
   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();
      BlockState blockState = level.getBlockState(blockPos);
      Block block = blockState.getBlock();
      ItemStack itemStack = context.getItemInHand();
      if (block == IRBlocks.RUBBER_LOG.get() && this.use(itemStack, this.operationEnergyCost, true)) {
         Player player = context.getPlayer();
         if (ItemTreetap.attemptExtract(player, level, blockPos, context.getClickedFace(), blockState, null)) {
            this.use(itemStack, this.operationEnergyCost, false);
            return InteractionResult.SUCCESS;
         } else {
            return super.useOn(context);
         }
      } else {
         return InteractionResult.PASS;
      }
   }
}
