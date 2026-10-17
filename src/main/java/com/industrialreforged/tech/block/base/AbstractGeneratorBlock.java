package com.industrialreforged.tech.block.base;

import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class AbstractGeneratorBlock<T extends BlockEntity> extends AbstractMachineBlock<T> {
   /** Дорогие генераторы (солнечные панели) падают собой даже без ключа. */
   protected final boolean dropsSelf;

   public AbstractGeneratorBlock() {
      this(false);
   }

   public AbstractGeneratorBlock(boolean dropsSelf) {
      this.dropsSelf = dropsSelf;
   }

   public AbstractGeneratorBlock(Properties properties) {
      this(properties, false);
   }

   public AbstractGeneratorBlock(Properties properties, boolean dropsSelf) {
      super(properties);
      this.dropsSelf = dropsSelf;
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return this.dropsSelf ? this.asItem().getDefaultInstance() : IRBlocks.GENERATOR.toStack();
   }
}
