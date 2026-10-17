package com.industrialreforged.tech.block.reactor;

import com.industrialreforged.block.BaseEntityBlock;
import com.industrialreforged.tech.api.tile.IWrenchable;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.blockentity.reactor.ReactorChamberElectricBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ReactorChamberElectricBlock extends BaseEntityBlock<ReactorChamberElectricBlockEntity> implements IWrenchable {
   public ReactorChamberElectricBlock() {
      super(Properties.of().strength(2.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ReactorChamberElectricBlockEntity(pos, state);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      return level.getBlockEntity(pos) instanceof ReactorChamberElectricBlockEntity entity ? entity.onActivated(player, hitResult) : InteractionResult.PASS;
   }

   protected ItemStack getInstantDrop(BlockState blockState, Builder builder) {
      return this.getWrenchDrops(blockState, builder);
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return IRBlocks.MACHINE.toStack();
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      if (level.getBlockEntity(pos) instanceof AbstractBlockEntity entity) {
         entity.updateRedstoneSignal(state, neighborPos);
      }
   }
}
