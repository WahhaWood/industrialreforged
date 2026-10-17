package com.industrialreforged.tech.item.tool;

import com.mojang.datafixers.util.Pair;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ItemToolHoeElectric extends ItemElectricTool {
   public ItemToolHoeElectric(ElectricProperties properties) {
      super(properties, 200, 40000, 400);
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
      return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility);
   }

   @Override
   public InteractionResult useOn(UseOnContext p_41341_) {
      if (!this.use(p_41341_.getItemInHand(), this.operationEnergyCost, true)) {
         return InteractionResult.FAIL;
      }

      Level level = p_41341_.getLevel();
      BlockPos blockpos = p_41341_.getClickedPos();
      BlockState toolModifiedState = level.getBlockState(blockpos).getToolModifiedState(p_41341_, ItemAbilities.HOE_TILL, false);
      Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null
         ? null
         : Pair.of((Predicate<UseOnContext>)ctx -> true, HoeItem.changeIntoState(toolModifiedState));
      if (pair == null) {
         return InteractionResult.PASS;
      }

      Predicate<UseOnContext> predicate = (Predicate<UseOnContext>)pair.getFirst();
      Consumer<UseOnContext> consumer = (Consumer<UseOnContext>)pair.getSecond();
      if (predicate.test(p_41341_)) {
         Player player = p_41341_.getPlayer();
         level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!level.isClientSide) {
            consumer.accept(p_41341_);
            if (player != null) {
               this.use(p_41341_.getItemInHand(), this.operationEnergyCost, false);
            }
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }
}
