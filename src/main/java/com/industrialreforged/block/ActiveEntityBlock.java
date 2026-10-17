package com.industrialreforged.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class ActiveEntityBlock<T extends BlockEntity> extends BaseEntityBlock<T> {
   public ActiveEntityBlock() {
      this(Properties.of());
   }

   public ActiveEntityBlock(Properties properties) {
      super(properties);
      if (this.defaultBlockState().hasProperty(ActiveBlock.ACTIVE)) {
         this.registerDefaultState(this.defaultBlockState().setValue(ActiveBlock.ACTIVE, false));
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ActiveBlock.ACTIVE});
   }
}
