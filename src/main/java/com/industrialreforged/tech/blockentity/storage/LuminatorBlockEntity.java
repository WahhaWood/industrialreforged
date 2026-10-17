package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.blockentity.base.AbstractElectricBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class LuminatorBlockEntity extends AbstractElectricBlockEntity<EnergyStorage> implements IServerTick {
   public boolean lswitch = true;
   public int ticker = -1;

   public LuminatorBlockEntity(BlockPos pos, BlockState state) {
      super(IRBlockEntities.LUMINATOR.get(), pos, state, EnergyStorage.asRecipient(32));
      this.enableRedstoneSignal();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.lswitch = tag.getBoolean("lswitch");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putBoolean("lswitch", this.lswitch);
   }

   public void serverTick() {
      this.setActive(this.energy.getStored() > 0 && !this.lswitch == this.redstoneSignal);
      this.ticker++;
      if (this.ticker % 4 == 0 && this.isActive()) {
         this.energy.extractForceEnergy(1);
      }
   }
}
