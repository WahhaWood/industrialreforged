package com.industrialreforged.tech.block.machine;

import com.industrialreforged.block.ActiveEntityBlock;
import com.industrialreforged.tech.api.tile.IWrenchable;
import com.industrialreforged.tech.blockentity.machine.QuantumQuarryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class QuantumQuarryBlock extends ActiveEntityBlock<QuantumQuarryBlockEntity> implements IWrenchable {
   public QuantumQuarryBlock() {
      super(Properties.of().strength(10.0F, 20.0F).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new QuantumQuarryBlockEntity(pos, state);
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      // дорогая машина: падает собой даже без ключа
      return this.asItem().getDefaultInstance();
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }
}
