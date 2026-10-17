package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemDrillTerrasteel extends ItemDrillUltimate {
   public ItemDrillTerrasteel(ElectricProperties properties) {
      super(properties);
   }

   @Override
   protected boolean isEffective(ItemStack stack, BlockState blockState) {
      return super.isEffective(stack, blockState) || blockState.is(BlockTags.MINEABLE_WITH_AXE) || blockState.is(BlockTags.MINEABLE_WITH_HOE);
   }

   @Override
   protected int getEnergyPerUse(ItemStack stack) {
      return switch (this.getToolMode(stack, 4)) {
         case 1, 3, 4 -> 700;
         case 2 -> 500;
         default -> 1000;
      };
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (player.isShiftKeyDown()) {
         int toolMode = this.getToolMode(stack, 4) + 1;
         if (toolMode != 2) {
            return super.use(level, player, hand);
         }

         this.setToolMode(stack, toolMode, level.registryAccess());
         if (level.isClientSide) {
            player.sendSystemMessage(Component.translatable("text.industrialreforged.tooltip.drill.trees"));
         }

         return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
      } else {
         return InteractionResultHolder.pass(stack);
      }
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (this.getToolMode(stack, 4) != 2) {
         super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      } else {
         this.appendEnergy(stack, tooltipComponents);
         tooltipComponents.add(Component.translatable("text.industrialreforged.fe.use.one", new Object[]{1000}));
         tooltipComponents.add(Component.translatable("text.industrialreforged.tooltip.drill.trees"));
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{"SHIFT"}));
      }
   }
}
