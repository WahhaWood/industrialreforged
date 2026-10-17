package com.industrialreforged.tech.block.machine;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.block.base.AbstractElectricMachineBlock;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace1LevelBlockEntity;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceBlock<T extends ElectricFurnace1LevelBlockEntity> extends AbstractElectricMachineBlock<T> {
   public ElectricFurnaceBlock(Supplier<BlockEntityType<T>> supplier) {
      super(supplier);
   }

   @Nullable
   @Override
   public <K extends BlockEntity> BlockEntityTicker<K> getTicker(Level level, BlockState state, BlockEntityType<K> blockEntityType) {
      return BaseBlockEntity.commonTick();
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }
}
