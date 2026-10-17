package com.industrialreforged.tech.block.machine;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.block.base.AbstractMachineBlock;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.blockentity.machine.TeleporterBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.utils.MsgUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TeleporterBlock extends AbstractMachineBlock<TeleporterBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TeleporterBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return BaseBlockEntity.commonTick();
   }

   /** ПКМ пустой рукой — телепорт к связанной цели (сущности на площадке уезжают сами). */
   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (player.isShiftKeyDown()) {
         return InteractionResult.PASS;
      }

      if (!(level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter)) {
         return InteractionResult.PASS;
      }

      if (!level.isClientSide) {
         if (teleporter.hasTarget()) {
            teleporter.teleport(player);
         } else {
            MsgUtils.send(player, "text.industrialreforged.message.teleporter.no_target");
         }
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return IRBlocks.ADVANCED_MACHINE.toStack();
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      if (level.getBlockEntity(pos) instanceof AbstractBlockEntity entity) {
         entity.updateRedstoneSignal(state, neighborPos);
      }
   }
}
