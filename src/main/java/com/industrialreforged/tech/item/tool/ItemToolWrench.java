package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.api.item.IWrench;
import com.industrialreforged.tech.api.item.ItemToolMaterials;
import com.industrialreforged.tech.api.tile.IWrenchable;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.item.PriorityUsableItem;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class ItemToolWrench extends Item implements IWrench, PriorityUsableItem {

   /** Our machines drop properly when broken with a wrench. */
   @Override
   public boolean isCorrectToolForDrops(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
      return state.getBlock() instanceof com.industrialreforged.tech.api.tile.IWrenchable
         || super.isCorrectToolForDrops(stack, state);
   }

   public ItemToolWrench(Properties properties) {
      super(properties);
   }

   @Override
   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
      if (this.canTakeDamage(stack, 1)) {
         ItemToolWrench.WrenchResult wrenchResult = wrenchBlock(stack, context, this.canTakeDamage(stack, 10));
         if (wrenchResult != ItemToolWrench.WrenchResult.NOTHING) {
            Player player = context.getPlayer();
            if (!context.getLevel().isClientSide) {
               this.damage(stack, wrenchResult == ItemToolWrench.WrenchResult.ROTATED ? 1 : 10, player, context.getHand());
               return InteractionResult.SUCCESS;
            }

            SoundUtil.play(IRSounds.SOUND_TOOL_WRENCH, SoundSource.AMBIENT, 1.0F, 1.0F);
            return InteractionResult.PASS;
         }
      }

      return InteractionResult.FAIL;
   }

   public static ItemToolWrench.WrenchResult wrenchBlock(ItemStack wrench, UseOnContext context, boolean canRemove) {
      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();
      Direction direction = context.getClickedFace();
      Player player = context.getPlayer();
      BlockState blockState = level.getBlockState(blockPos);
      Block block = blockState.getBlock();
      if (blockState.isAir()) {
         return ItemToolWrench.WrenchResult.NOTHING;
      }

      // блоки с переключаемым режимом (клапан резервуара) — ключ меняет режим
      if (block instanceof com.industrialreforged.tech.api.tile.IWrenchModeSwitch modeSwitch) {
         if (level.isClientSide) {
            return ItemToolWrench.WrenchResult.ROTATED;
         }

         return modeSwitch.switchMode(level, blockPos, blockState, player)
            ? ItemToolWrench.WrenchResult.ROTATED
            : ItemToolWrench.WrenchResult.NOTHING;
      }

      if (player.isShiftKeyDown() && block instanceof IWrenchable wrenchable) {
         if (!canRemove) {
            return ItemToolWrench.WrenchResult.NOTHING;
         }

         if (level.isClientSide) {
            return ItemToolWrench.WrenchResult.REMOVED;
         }

         if (player.blockActionRestricted(level, blockPos, ((ServerPlayer)player).gameMode.getGameModeForPlayer())) {
            return ItemToolWrench.WrenchResult.NOTHING;
         }

         BlockEntity blockEntity = level.getBlockEntity(blockPos);
         if (!wrenchable.canWrench(player, blockState, blockEntity)) {
            return ItemToolWrench.WrenchResult.NOTHING;
         }

         block.playerWillDestroy(level, blockPos, blockState, player);
         if (level.removeBlock(blockPos, false)) {
            block.destroy(level, blockPos, blockState);
         }

         List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel)level, blockPos, blockEntity, player, wrench);
         if (!drops.isEmpty()) {
            for (ItemStack itemStack : drops) {
               StackUtil.dropAsEntity(level, blockPos, itemStack);
            }
         }

         if (!player.getAbilities().instabuild) {
            blockState.spawnAfterBreak((ServerLevel)level, blockPos, player.getUseItem(), false);
         }

         return ItemToolWrench.WrenchResult.REMOVED;
      } else if (block instanceof IWrenchable wrenchable && !wrenchable.canWrench(player, blockState, level.getBlockEntity(blockPos))) {
         return ItemToolWrench.WrenchResult.NOTHING;
      } else {
         Direction direction4 = context.getClickedFace();
         Rotation rotation = null;
         if (IR.keyboard.isAltKeyDown(player)) {
            rotation = isAltRotationClockwise(direction, player) ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
         } else if (blockState.hasProperty(BlockStateProperties.FACING)) {
            rotation = direction4.getAxis() == Axis.Y ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
         } else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            rotation = Rotation.CLOCKWISE_90;
         } else if (blockState.hasProperty(BlockStateProperties.VERTICAL_DIRECTION)) {
            rotation = Rotation.COUNTERCLOCKWISE_90;
         }

         BlockState blockState1;
         if (rotation != null && (blockState1 = blockState.rotate(level, blockPos, rotation)) != blockState) {
            level.setBlockAndUpdate(blockPos, blockState1);
            return ItemToolWrench.WrenchResult.ROTATED;
         } else {
            return ItemToolWrench.WrenchResult.NOTHING;
         }
      }
   }

   private static boolean isAltRotationClockwise(Direction direction, Player player) {
      return direction.getAxisDirection() == AxisDirection.POSITIVE != player.isShiftKeyDown();
   }

   private static Direction getNewFacing(Direction direction, Player player) {
      return player.isShiftKeyDown() ? direction.getOpposite() : direction;
   }

   public boolean isValidRepairItem(@NotNull ItemStack itemStack, @NotNull ItemStack itemStack2) {
      return ItemToolMaterials.BRONZE.getRepairIngredient().test(itemStack2);
   }

   enum WrenchResult {
      ROTATED,
      REMOVED,
      NOTHING;
   }
}
