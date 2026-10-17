package com.industrialreforged.tech.block.base;

import com.industrialreforged.block.RotateAndActiveEntityBlock;
import com.industrialreforged.tech.api.tile.IWrenchable;
import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.storage.loot.LootParams.Builder;

public abstract class AbstractMachineBlock<T extends BlockEntity> extends RotateAndActiveEntityBlock<T> implements IWrenchable {
   @Override
   public <V extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<V> getTicker(
      net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState state,
      net.minecraft.world.level.block.entity.BlockEntityType<V> blockEntityType
   ) {
      return com.industrialreforged.blockentity.BaseBlockEntity.commonTick();
   }

   public AbstractMachineBlock() {
      super(Properties.of().strength(2.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
   }

   public AbstractMachineBlock(Properties properties) {
      super(properties);
   }

   protected ItemStack getInstantDrop(BlockState blockState, Builder builder) {
      return this.getWrenchDrops(blockState, builder);
   }

   @Override
   public float getWrenchDropRate() {
      return 0.9F;
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return IRBlocks.MACHINE.toStack();
   }
}
