package com.industrialreforged.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class RotateAndActiveBlock extends RotateBlock {
   public RotateAndActiveBlock() {
      this(Properties.of());
   }

   public RotateAndActiveBlock(Properties properties) {
      super(properties);
      if (this.defaultBlockState().hasProperty(ActiveBlock.ACTIVE)) {
         this.registerDefaultState(this.defaultBlockState().setValue(ActiveBlock.ACTIVE, false));
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{ActiveBlock.ACTIVE});
   }
}
