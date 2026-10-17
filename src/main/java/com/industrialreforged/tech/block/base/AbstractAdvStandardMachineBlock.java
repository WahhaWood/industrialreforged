package com.industrialreforged.tech.block.base;

import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AbstractAdvStandardMachineBlock<T extends AbstractStandardMachineBlockEntity<?>> extends AbstractStandardMachineBlock<T> {
   public AbstractAdvStandardMachineBlock(Supplier<BlockEntityType<T>> supplier) {
      super(supplier);
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return IRBlocks.ADVANCED_MACHINE.toStack();
   }
}
