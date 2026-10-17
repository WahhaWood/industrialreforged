package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemDrillUltimate extends ItemDrill {
   public ItemDrillUltimate(ElectricProperties properties) {
      super(800000, 16000, 500, 24.0F, properties.component(IRDataComponents.MODE, (byte)0), Tiers.NETHERITE);
   }

   private boolean use(ItemStack stack) {
      return this.use(stack, false);
   }

   private boolean use(ItemStack stack, boolean simulate) {
      return this.use(stack, this.getEnergyPerUse(stack), simulate);
   }

   protected int getEnergyPerUse(ItemStack stack) {
      return switch (this.getToolMode(stack, 4)) {
         case 1, 3, 4 -> 700;
         case 2 -> 500;
         default -> 1000;
      };
   }

   @Override
   public int getMiningEnergyCost(ItemStack stack) {
      return this.getEnergyPerUse(stack);
   }

   @Override
   public float getDestroySpeed(ItemStack stack, BlockState state) {
      if (this.use(stack, true) && this.isCorrectToolForDrops(stack, state)) {
         return switch (this.getToolMode(stack, 4)) {
            case 0 -> 35.0F;
            case 1, 3, 4 -> 16.0F;
            case 2 -> 10.0F;
            default -> 1.0F;
         };
      } else {
         return 1.0F;
      }
   }

   @Override
   public boolean mineBlock(ItemStack stack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      int mode = this.getToolMode(stack, 4);
      boolean result = level.isClientSide || this.use(stack);
      if (result && (mode == 3 || mode == 4) && livingEntity instanceof Player player) {
         this.breakAOEBlocks(stack, blockPos, 512, mode - 2, 0, player, false, this::use);
      }

      return result;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (player.isShiftKeyDown()) {
         int toolMode = this.nextToolMode(stack, 4, level.registryAccess());
         if (level.isClientSide) {
            player.displayClientMessage(Component.translatable("text.industrialreforged.tooltip.drill." + switch (toolMode) {
               case 1 -> "low";
               case 2 -> "fine";
               case 3 -> "holes.3x3";
               case 4 -> "holes.5x5";
               default -> "normal";
            }), false);
         }

         return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
      } else {
         return InteractionResultHolder.pass(stack);
      }
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

      tooltipComponents.add(Component.translatable("text.industrialreforged.tooltip.drill." + switch (this.getToolMode(stack, 4)) {
         case 1 -> "low";
         case 2 -> "fine";
         case 3 -> "holes.3x3";
         case 4 -> "holes.5x5";
         default -> "normal";
      }));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{"SHIFT"}));
   }
}
