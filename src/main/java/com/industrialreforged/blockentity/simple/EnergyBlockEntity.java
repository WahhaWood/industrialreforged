package com.industrialreforged.blockentity.simple;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyBlockEntity<T extends IEnergyStorage & INBTSerializable<Tag>> extends BaseBlockEntity implements IEnergyWrapper {
   public final T energy;

   public EnergyBlockEntity(Holder<BlockEntityType<?>> holder, BlockPos blockPos, BlockState state, T energyStorage) {
      this((BlockEntityType<?>)holder.value(), blockPos, state, energyStorage);
   }

   public EnergyBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state, T energyStorage) {
      super(type, blockPos, state);
      this.energy = energyStorage;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.put("energy", this.energy.serializeNBT(registries));
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      Tag energy = tag.get("energy");
      if (energy != null) {
         this.energy.deserializeNBT(registries, energy);
      }
   }

   @Override
   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return this.energy;
   }
}
