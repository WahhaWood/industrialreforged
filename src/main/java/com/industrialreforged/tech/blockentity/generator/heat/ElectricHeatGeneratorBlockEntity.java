package com.industrialreforged.tech.blockentity.generator.heat;

import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.blockentity.base.AbstractHeatSourceBlockEntity;
import com.industrialreforged.tech.container.EnergyContainer;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.generator.heat.ElectricHeatGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class ElectricHeatGeneratorBlockEntity extends AbstractHeatSourceBlockEntity implements IEnergyWrapper {

   @Override
   public void serverTick() {
      super.serverTick();
      com.industrialreforged.tech.core.util.EnergySlotHelper.tick(this.dischargeSlot, this.energy);

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      // every coil turns EU into heat, like in IC2
      int coils = 0;
      for (int i = 0; i < this.coilSlot.getContainerSize(); i++) {
         if (!this.coilSlot.getItem(i).isEmpty()) {
            coils++;
         }
      }
      int drain = Math.max(1, coils) * 10;
      boolean heating = coils > 0 && this.energy.getStored() >= drain && this.getHeat() < MAX_HEAT;
      if (heating) {
         this.energy.extractForceEnergy(drain);
         this.increaseHeat(coils * 10);
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, heating);
   }
   public final EnergyStorage energy = new EnergyStorage(640000, 16384, 0);
   public final InvSlotConsumable coilSlot = new InvSlotConsumableItem(this, "coils", 10, IRItems.COIL);
   public final EnergyContainer dischargeSlot;
   private boolean newActive;

   public ElectricHeatGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.ELECTRIC_HEAT_GENERATOR.get(), blockPos, blockState);
      this.coilSlot.setMaxStackSize(1);
      this.dischargeSlot = new EnergyContainer(this, 4, false);
      this.newActive = false;
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_INDUCTION_LOOP.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.energy.load(tag, registries);
      this.coilSlot.load(tag, registries);
      this.dischargeSlot.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.energy.save(tag, registries);
      this.coilSlot.save(tag, registries);
      this.dischargeSlot.save(tag, registries);
   }

   @Override
   protected int fillHeatBuffer(int value) {
      if (value > 0) {
         this.energy.extractEnergy(value * 3);
         this.newActive = true;
      } else {
         this.newActive = false;
      }

      return value;
   }

   @Override
   public int getMaxHeatEmittedPerTick() {
      int n = 0;

      for (int i = 0; i < this.coilSlot.getContainerSize(); i++) {
         if (!this.coilSlot.isEmpty(i)) {
 n += 10;
         }
      }

      return Math.min(n, this.energy.getEnergyStored() / 3);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new ElectricHeatGeneratorMenu(containerId, playerInventory, this);
   }

   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return this.energy;
   }
}
