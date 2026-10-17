package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChargepadMFSUBlockEntity extends ChargepadBlockEntity {
   public ChargepadMFSUBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ElectricMFSUBlockEntity.CAPACITY, ElectricMFSUBlockEntity.RECEIVE, ElectricMFSUBlockEntity.EXTRACT, IRBlockEntities.MFSU_CHARGEPAD.get(), blockPos, blockState);
   }
}
