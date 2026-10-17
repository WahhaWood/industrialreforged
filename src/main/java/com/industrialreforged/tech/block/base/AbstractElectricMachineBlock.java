package com.industrialreforged.tech.block.base;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class AbstractElectricMachineBlock<T extends AbstractElectricMachineBlockEntity> extends AbstractMachineBlock<T> {
   private final Supplier<BlockEntityType<T>> supplier;

   public AbstractElectricMachineBlock(Supplier<BlockEntityType<T>> supplier) {
      this.supplier = supplier;
   }

   public AbstractElectricMachineBlock(Properties properties, Supplier<BlockEntityType<T>> supplier) {
      super(properties);
      this.supplier = supplier;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return this.supplier.get().create(pos, state);
   }

   @Nullable
   public <V extends BlockEntity> BlockEntityTicker<V> getTicker(Level level, BlockState state, BlockEntityType<V> blockEntityType) {
      return BaseBlockEntity.commonTick();
   }
}
