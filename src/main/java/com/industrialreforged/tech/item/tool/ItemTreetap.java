package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.block.RubberLogBlock;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.core.util.StackUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemTreetap extends Item {
   public ItemTreetap(Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level level = useOnContext.getLevel();
      BlockPos blockPos;
      BlockState blockState = level.getBlockState(blockPos = useOnContext.getClickedPos());
      Block block = blockState.getBlock();
      if (block == IRBlocks.RUBBER_LOG.get()) {
         Player player = useOnContext.getPlayer();
         if (attemptExtract(player, level, blockPos, useOnContext.getClickedFace(), blockState, null)) {
            if (!level.isClientSide) {
               StackUtil.damage(player, useOnContext.getHand(), null, 1);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean attemptExtract(Player player, Level level, BlockPos blockPos, Direction direction, BlockState blockState, List<ItemStack> list) {
      assert blockState.getBlock() == IRBlocks.RUBBER_LOG.get();
      RubberLogBlock.RubberWoodState rubberWoodState = (RubberLogBlock.RubberWoodState)blockState.getValue(RubberLogBlock.STATE);
      if (rubberWoodState.isPlain() || rubberWoodState.facing != direction) {
         return false;
      }

      if (rubberWoodState.wet) {
         if (!level.isClientSide) {
            level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(RubberLogBlock.STATE, rubberWoodState.getDry()));
            if (list != null) {
               list.add(new ItemStack((ItemLike)IRItems.RESIN.get(), level.random.nextInt(3) + 1));
            } else {
               ejectResin(level, blockPos, direction, level.random.nextInt(3) + 1);
            }
         }

         if (level.isClientSide && player != null) {
            SoundUtil.play(IRSounds.SOUND_TOOL_TREETAP, SoundSource.AMBIENT, 1.0F, 1.0F);
         }

         return true;
      } else {
         if (!level.isClientSide && level.random.nextInt(5) == 0) {
            level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(RubberLogBlock.STATE, RubberLogBlock.RubberWoodState.PLAIN));
         }

         if (level.random.nextInt(5) == 0) {
            if (!level.isClientSide) {
               ejectResin(level, blockPos, direction, 1);
               if (list != null) {
                  list.add(new ItemStack((ItemLike)IRItems.RESIN.get()));
               } else {
                  ejectResin(level, blockPos, direction, 1);
               }
            }

            if (level.isClientSide && player != null) {
               SoundUtil.play(IRSounds.SOUND_TOOL_TREETAP, SoundSource.AMBIENT, 1.0F, 1.0F);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private static void ejectResin(Level level, BlockPos blockPos, Direction direction, int n) {
      double d = blockPos.getX() + 0.5 + direction.getStepX() * 0.3;
      double d2 = blockPos.getY() + 0.5 + direction.getStepY() * 0.3;
      double d3 = blockPos.getZ() + 0.5 + direction.getStepZ() * 0.3;

      for (int i = 0; i < n; i++) {
         ItemEntity itemEntity = new ItemEntity(level, d, d2, d3, new ItemStack((ItemLike)IRItems.RESIN.get()));
         itemEntity.setDefaultPickUpDelay();
         level.addFreshEntity(itemEntity);
      }
   }
}
