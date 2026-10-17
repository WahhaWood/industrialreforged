package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractElectricBlockEntity<T extends IEnergyStorage & INBTSerializable<Tag>> extends AbstractBlockEntity implements IEnergyWrapper {
   public final T energy;

   public AbstractElectricBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState, T energy) {
      super(type, blockPos, blockState);
      this.energy = energy;
   }

   @Override
   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.put("energy", this.energy.serializeNBT(registries));
   }

   @Override
   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      Tag energy = tag.get("energy");
      if (energy != null) {
         this.energy.deserializeNBT(registries, energy);
      }
   }

   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return this.energy;
   }
}
