package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChargepadCESUBlockEntity extends ChargepadBlockEntity {
   public ChargepadCESUBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ElectricCESUBlockEntity.CAPACITY, ElectricCESUBlockEntity.RECEIVE, ElectricCESUBlockEntity.EXTRACT, IRBlockEntities.CESU_CHARGEPAD.get(), blockPos, blockState);
   }
}
