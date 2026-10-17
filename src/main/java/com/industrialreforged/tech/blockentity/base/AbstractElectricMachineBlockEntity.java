package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.container.EnergyContainer;
import com.industrialreforged.utils.EnergyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractElectricMachineBlockEntity extends AbstractMachineBlockEntity implements IEnergyWrapper {
   public final EnergyContainer energyContainer;
   public final EnergyStorage energy;

   public AbstractElectricMachineBlockEntity(int capacity, int receiveTick, BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
      this(capacity, receiveTick, type, blockPos, blockState, true);
   }

   public AbstractElectricMachineBlockEntity(
      int capacity, int receiveTick, BlockEntityType<?> type, BlockPos blockPos, BlockState blockState, boolean allowRedstoneDust
   ) {
      this(type, blockPos, blockState, new EnergyStorage(capacity, receiveTick, 0), allowRedstoneDust, false);
   }

   public AbstractElectricMachineBlockEntity(
      int capacity, int receiveTick, BlockEntityType<?> type, BlockPos blockPos, BlockState blockState, boolean allowRedstoneDust, boolean charge
   ) {
      this(type, blockPos, blockState, new EnergyStorage(capacity, receiveTick, 0), allowRedstoneDust, charge);
   }

   public AbstractElectricMachineBlockEntity(
      BlockEntityType<?> type, BlockPos blockPos, BlockState blockState, EnergyStorage energy, boolean allowRedstoneDust, boolean charge
   ) {
      super(type, blockPos, blockState);
      this.energy = energy;
      // no input limits anywhere: whatever the network can push in one tick is
      // accepted (the limit only ever starved machines behind one wire)
      this.energy.setReceive(Integer.MAX_VALUE);
      if (charge) {
         this.energyContainer = new EnergyContainer(this, energy.getReceive(), 1);
      } else {
         this.energyContainer = new EnergyContainer(this, energy.getReceive(), allowRedstoneDust, 1);
      }
   }

   @Override
   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.energy.load(tag, registries);
      this.energyContainer.load(tag, registries);
   }

   @Override
   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.energy.save(tag, registries);
      this.energyContainer.save(tag, registries);
   }

   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      this.tickEnergyContainer();

      if (this.energy.canExtract()) {
         EnergyUtils.transferEnergyAllNear(this, this.energy);
      }
   }

   /** Charges or discharges the item(s) sitting in the charge/discharge slots. */
   protected void tickEnergyContainer() {
      com.industrialreforged.tech.core.util.EnergySlotHelper.tick(this.energyContainer, this.energy);
   }

   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return this.energy;
   }

   /** Вход машин бесконечный: ограничивает только сеть/провода. */
   @Override
   public boolean unlimitedInput() {
      return true;
   }

   public void setExtractTick(int value) {
      this.energy.setExtract(value);
      if (this.energyContainer != null) {
         this.energyContainer.setReceiveTick(this.energy.getExtract());
      }
   }

   public void setReceiveTick(int value) {
      this.energy.setReceive(value);
      if (this.energyContainer != null) {
         this.energyContainer.setExtractTick(this.energy.getReceive());
      }
   }
}
