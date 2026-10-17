package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChargepadEFSUBlockEntity extends ChargepadBlockEntity {
   public ChargepadEFSUBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ElectricEFSUBlockEntity.CAPACITY, ElectricEFSUBlockEntity.RECEIVE, ElectricEFSUBlockEntity.EXTRACT, IRBlockEntities.EFSU_CHARGEPAD.get(), blockPos, blockState);
   }
}
