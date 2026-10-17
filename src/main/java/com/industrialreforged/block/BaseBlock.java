package com.industrialreforged.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BaseBlock extends Block {
   public BaseBlock() {
      this(Properties.of());
   }

   public BaseBlock(Properties properties) {
      super(properties);
   }
}
