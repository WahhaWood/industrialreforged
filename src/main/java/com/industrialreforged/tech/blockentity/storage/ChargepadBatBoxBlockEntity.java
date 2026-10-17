package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChargepadBatBoxBlockEntity extends ChargepadBlockEntity {
   public ChargepadBatBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ElectricBatBoxBlockEntity.CAPACITY, ElectricBatBoxBlockEntity.RECEIVE, ElectricBatBoxBlockEntity.EXTRACT, IRBlockEntities.BATBOX_CHARGEPAD.get(), blockPos, blockState);
   }
}
