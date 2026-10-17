package com.industrialreforged.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class BaseEntityBlock<T extends BlockEntity> extends BaseBlock implements EntityBlock {
   public BaseEntityBlock() {
      this(Properties.of());
   }

   public BaseEntityBlock(Properties properties) {
      super(properties);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (player.isShiftKeyDown()) {
         return InteractionResult.PASS;
      }

      if (level.getBlockEntity(pos) instanceof MenuProvider provider) {
         if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(provider, pos);
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public List<ItemStack> getDrops(BlockState blockState, Builder builder) {
      List<ItemStack> drops = new ArrayList<>();
      drops.add(this.getInstantDrop(blockState, builder));
      BlockEntity blockEntity = (BlockEntity)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (blockEntity != null) {
         this.addAdditionalDrops((T)blockEntity, drops);
      }

      return drops;
   }

   /**
    * Block entities of this block tick on both sides. Without this override the
    * vanilla default returns no ticker at all, which silently kept machines
    * such as the solar distiller from ever running.
    */
   @Nullable
   public <V extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<V> getTicker(
      Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<V> blockEntityType
   ) {
      return com.industrialreforged.blockentity.BaseBlockEntity.commonTick();
   }

   /** Adds the items stored inside the block entity (machines keep their contents). */
   protected void addAdditionalDrops(T entity, List<ItemStack> drops) {
      if (entity instanceof com.industrialreforged.blockentity.BaseBlockEntity base) {
         base.addDrops(drops);
      }
   }

   protected ItemStack getInstantDrop(BlockState blockState, Builder builder) {
      return blockState.getBlock().asItem().getDefaultInstance();
   }

   @Nullable
   protected T getBlockEntity(Level level, BlockPos blockPos) {
      return (T)level.getBlockEntity(blockPos);
   }
}
