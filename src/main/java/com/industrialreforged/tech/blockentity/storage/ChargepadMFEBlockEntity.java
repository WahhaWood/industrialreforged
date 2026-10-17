package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChargepadMFEBlockEntity extends ChargepadBlockEntity {
   public ChargepadMFEBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ElectricMFEBlockEntity.CAPACITY, ElectricMFEBlockEntity.RECEIVE, ElectricMFEBlockEntity.EXTRACT, IRBlockEntities.MFE_CHARGEPAD.get(), blockPos, blockState);
   }
}
