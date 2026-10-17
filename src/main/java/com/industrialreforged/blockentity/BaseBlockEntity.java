package com.industrialreforged.blockentity;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.IServerTick;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseBlockEntity extends BlockEntity {
   public BaseBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state) {
      this((BlockEntityType<?>)holder.value(), blockPos, state);
   }

   public BaseBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state) {
      super(type, blockPos, state);
   }

   public void markForUpdate() {
      if (this.level instanceof ServerLevel level) {
         level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public void markForSave() {
      if (this.level != null) {
         this.level.blockEntityChanged(this.worldPosition);
      }
   }

   // ------------------------------------------------------------------
   // Contents drops: containers register themselves here, so breaking a
   // machine by hand no longer voids the items stored inside it.
   // ------------------------------------------------------------------
   private final List<Container> dropContainers = new ArrayList<>();

   public void registerDropContainer(Container container) {
      this.dropContainers.add(container);
   }

   public void addDrops(List<ItemStack> drops) {
      for (Container container : this.dropContainers) {
         for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
               drops.add(stack.copy());
               container.setItem(i, ItemStack.EMPTY);
            }
         }
      }
   }

   @Nullable
   public static <T extends BlockEntity> BlockEntityTicker<T> clientTick(Level level) {
      return level.isClientSide ? commonTick() : null;
   }

   /**
    * Ticker only on the server: for blocks whose entities have no client logic. A client
    * ticker that does nothing still costs a chunk/state check every tick per block, and
    * wires or solar panels come in hundreds.
    */
   @Nullable
   public static <T extends BlockEntity> BlockEntityTicker<T> serverTick(Level level) {
      return level.isClientSide ? null : commonTick();
   }

   public static <T extends BlockEntity> BlockEntityTicker<T> commonTick() {
      return BaseBlockEntity.Ticker.INSTANCE;
   }

   private static class Ticker implements BlockEntityTicker {
      static final BaseBlockEntity.Ticker INSTANCE = new BaseBlockEntity.Ticker();

      public void tick(Level level, BlockPos pos, BlockState state, BlockEntity t) {
         if (level.isClientSide) {
            if (t instanceof IClientTick client) {
               client.clientTick();
            }
         } else if (t instanceof IServerTick server) {
            server.serverTick();
            com.industrialreforged.tech.core.util.FluidUpgradeHelper.tick(t);
         }
      }
   }
}
