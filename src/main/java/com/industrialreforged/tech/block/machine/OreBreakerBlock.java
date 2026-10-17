package com.industrialreforged.tech.block.machine;

import com.industrialreforged.tech.api.tile.IWrenchable;
import com.industrialreforged.tech.block.base.AbstractMachineBlock;
import com.industrialreforged.tech.blockentity.machine.OreBreakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class OreBreakerBlock extends AbstractMachineBlock<OreBreakerBlockEntity> implements IWrenchable {
   public OreBreakerBlock() {
      super(
         Properties.of()
            .strength(2.0F, 10.0F)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .forceSolidOn()
            .sound(SoundType.NETHERITE_BLOCK)
      );
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new OreBreakerBlockEntity(pos, state);
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      // дорогая машина: падает собой даже без ключа
      return this.asItem().getDefaultInstance();
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }
}
