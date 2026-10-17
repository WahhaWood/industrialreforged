package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.api.item.IWrench;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ItemGraviTool extends ItemElectricTool implements IWrench {

   /** Our machines drop properly when broken with a wrench. */
   @Override
   public boolean isCorrectToolForDrops(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
      return state.getBlock() instanceof com.industrialreforged.tech.api.tile.IWrenchable
         || super.isCorrectToolForDrops(stack, state);
   }

   private final int energyPerTreeTap = 50;
   private final int energyPerSwitchSide = 50;
   private final int energyPerWrenchStandartOperation = 500;

   public ItemGraviTool(ElectricProperties properties) {
      super(properties.component(IRDataComponents.MODE, (byte)0), 50, 300000, 10000);
   }

   @Override
   public boolean canWrenchDrop(Level level, ItemStack stack, float dropChance) {
      return true;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!level.isClientSide && IR.keyboard.isModeSwitchKeyDown(player)) {
         player.displayClientMessage(
            Component.translatable("text.industrialreforged.tooltip.gravi_tool.mode." + switch (this.nextToolMode(stack, 2, level.registryAccess())) {
               case 1 -> "tree_tap";
               case 2 -> "wrench";
               default -> "hoe";
            }), false
         );
         return InteractionResultHolder.consume(stack);
      } else {
         return InteractionResultHolder.pass(stack);
      }
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

      tooltipComponents.add(Component.translatable("text.industrialreforged.tooltip.gravi_tool.mode." + switch (this.getToolMode(stack, 2)) {
         case 1 -> "tree_tap";
         case 2 -> "wrench";
         default -> "hoe";
      }));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }

   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
      if (this.getToolMode(stack, 2) == 2) {
         if (!this.use(stack, this.energyPerWrenchStandartOperation, true)) {
            return InteractionResult.FAIL;
         }

         ItemToolWrench.WrenchResult wrenchResult = ItemToolWrench.wrenchBlock(stack, context, true);
         if (wrenchResult != ItemToolWrench.WrenchResult.NOTHING) {
            if (!context.getLevel().isClientSide) {
               this.use(stack, wrenchResult == ItemToolWrench.WrenchResult.ROTATED ? this.energyPerSwitchSide : this.energyPerWrenchStandartOperation, false);
               return InteractionResult.SUCCESS;
            } else {
               SoundUtil.play(IRSounds.SOUND_TOOL_WRENCH, SoundSource.AMBIENT, 1.0F, 1.0F);
               return InteractionResult.PASS;
            }
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return super.onItemUseFirst(stack, context);
      }
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
      return this.getToolMode(stack, 2) == 0 && ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility);
   }

   @Override
   public InteractionResult useOn(UseOnContext context) {
      ItemStack stack = context.getItemInHand();
      switch (this.getToolMode(stack, 2)) {
         case 0:
            return ((Item)IRItems.ELECTRIC_HOE.get()).useOn(context);
         case 1:
            if (!this.use(stack, this.energyPerTreeTap, true)) {
               return InteractionResult.FAIL;
            } else {
               BlockState state = context.getLevel().getBlockState(context.getClickedPos());
               if (state.getBlock() == IRBlocks.RUBBER_LOG.get()) {
                  Player player = context.getPlayer();
                  if (ItemTreetap.attemptExtract(player, context.getLevel(), context.getClickedPos(), context.getClickedFace(), state, null)) {
                     if (!context.getLevel().isClientSide) {
                        this.use(stack, this.energyPerTreeTap, false);
                     }

                     return InteractionResult.SUCCESS;
                  }

                  return InteractionResult.FAIL;
               }

               return InteractionResult.PASS;
            }
         default:
            return super.useOn(context);
      }
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }
}
