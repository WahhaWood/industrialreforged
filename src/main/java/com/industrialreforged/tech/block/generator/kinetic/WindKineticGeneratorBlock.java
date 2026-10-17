package com.industrialreforged.tech.block.generator.kinetic;

import com.industrialreforged.tech.block.base.AbstractGeneratorBlock;
import com.industrialreforged.tech.blockentity.generator.kinetic.WindKineticGeneratorBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WindKineticGeneratorBlock extends AbstractGeneratorBlock<WindKineticGeneratorBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new WindKineticGeneratorBlockEntity(pos, state);
   }

   protected void addAdditionalDrops(WindKineticGeneratorBlockEntity entity, List<ItemStack> drops) {
      drops.addAll(entity.rotorSlot.removeAllItems());
   }
}
