package com.industrialreforged.tech.multiblock.tank;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.block.BaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Контроллер резервуара-котла: допустимая оболочка мультиблока, при собранной структуре
 * ПКМ открывает GUI с водой/паром/теплом (по прототипу контроллера Extreme Reactors).
 */
public class TankControllerBlock extends BaseEntityBlock {
   public TankControllerBlock() {
      super(BlockBehaviour.Properties.of()
         .strength(3.0F, 15.0F)
         .requiresCorrectToolForDrops()
         .sound(SoundType.METAL));
      this.registerDefaultState(this.defaultBlockState().setValue(ActiveBlock.ACTIVE, false));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{ActiveBlock.ACTIVE});
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TankControllerBlockEntity(pos, state);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (!(level.getBlockEntity(pos) instanceof TankControllerBlockEntity controller)) {
         return InteractionResult.PASS;
      }

      if (level.isClientSide) {
         return InteractionResult.sidedSuccess(true);
      }

      if (!controller.isFormed()) {
         // Попытка собрать структуру и показать сообщение
         controller.validateStructure(player);
         return InteractionResult.sidedSuccess(false);
      }

      if (player.isShiftKeyDown()) {
         // Shift+ПКМ — принудительная перепроверка структуры
         controller.validateStructure(player);
         return InteractionResult.sidedSuccess(false);
      }

      // Перепроверка целостности: стенку могли сломать без уведомления
      if (!controller.validateStructure(null)) {
         controller.validateStructure(player);
         return InteractionResult.sidedSuccess(false);
      }

      if (player instanceof ServerPlayer serverPlayer) {
         serverPlayer.openMenu(controller, pos);
      }
      return InteractionResult.sidedSuccess(false);
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && !level.isClientSide) {
         if (level.getBlockEntity(pos) instanceof TankControllerBlockEntity controller) {
            controller.disbandStructure();
         }
         TankWallBlock.notifyNearbyValves(level, pos);
      }
      super.onRemove(state, level, pos, newState, isMoving);
   }
}
