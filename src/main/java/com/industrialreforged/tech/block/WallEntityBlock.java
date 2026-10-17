package com.industrialreforged.tech.block;

import com.industrialreforged.block.BaseEntityBlock;
import com.industrialreforged.tech.blockentity.WallBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class WallEntityBlock extends BaseEntityBlock<WallBlockEntity> {
   public WallEntityBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new WallBlockEntity(pos, state);
   }
}
