package com.industrialreforged.tech.blockentity.generator.kinetic;

import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.blockentity.base.AbstractKineticSourceBlockEntity;
import com.industrialreforged.tech.container.EnergyContainer;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.menu.generator.kinetic.ElectricKineticGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class ElectricKineticGeneratorBlockEntity extends AbstractKineticSourceBlockEntity implements IEnergyWrapper {

   @Override
   public void serverTick() {
      super.serverTick();
      com.industrialreforged.tech.core.util.EnergySlotHelper.tick(this.discharge, this.energy);

      // EU → KU: каждый электромотор даёт до 100 KU/т, тратя 100 EU/т
      int motors = 0;
      for (int i = 0; i < this.motors.getContainerSize(); i++) {
         if (!this.motors.isEmpty(i)) {
            motors++;
         }
      }

      int max = motors * 100;
      if (max > 0 && this.power < max) {
         int converted = (int)Math.min(max - this.power, Math.min(max, this.energy.getStored()));
         if (converted > 0) {
            this.energy.extractForceEnergy(converted);
            this.power += converted;
            this.markForSave();
         }
      }
   }
   public EnergyStorage energy = new EnergyStorage(640000, 16384, 0);
   public InvSlotConsumableItem motors = new InvSlotConsumableItem(this, "motors", 10, IRItems.ELECTRIC_MOTOR);
   public EnergyContainer discharge;

   public ElectricKineticGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.ELECTRIC_KINETIC_GENERATOR.get(), blockPos, blockState);
      this.motors.setMaxStackSize(1);
      this.discharge = new EnergyContainer(this, 4096, true);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.energy.load(tag, registries);
      this.motors.load(tag, registries);
      this.discharge.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.energy.save(tag, registries);
      this.motors.save(tag, registries);
      this.discharge.save(tag, registries);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new ElectricKineticGeneratorMenu(containerId, playerInventory, this);
   }

   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return this.energy;
   }

   public int getMaxPower() {
      int generation = 0;

      for (int i = 0; i < this.motors.getContainerSize(); i++) {
         if (!this.motors.isEmpty(i)) {
            generation += 100;
         }
      }

      return generation;
   }
}
