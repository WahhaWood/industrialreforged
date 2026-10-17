package com.industrialreforged.tech.block.generator.electric;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.block.base.AbstractGeneratorBlock;
import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlock extends AbstractGeneratorBlock<SolarPanelBlockEntity> {
   private final int level;

   public SolarPanelBlock(int level) {
      super(true);
      this.level = level;
   }

   public int getLevel() {
      return this.level;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SolarPanelBlockEntity(pos, state);
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }

   /** Панели живут только на сервере: на клиенте тикер был пустым, а панелей сотни. */
   @Nullable
   @Override
   public <V extends BlockEntity> BlockEntityTicker<V> getTicker(Level level, BlockState state, BlockEntityType<V> type) {
      return BaseBlockEntity.serverTick(level);
   }
}
