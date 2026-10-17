package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.EnergyContainer;
import com.industrialreforged.tech.menu.storage.ElectricBlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public abstract class ElectricBlockEntity extends AbstractElectricMachineBlockEntity {
   public final EnergyContainer charge;
   public byte redstoneMode = 0;
   public static byte redstoneModes = 7;
   private int emitValue;

   public ElectricBlockEntity(int capacity, int receiveTick, int extractTick, BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
      super(type, blockPos, blockState, EnergyStorage.as(capacity, receiveTick, extractTick), false, false);
      this.enableRedstoneSignal();
      this.charge = new EnergyContainer(this, extractTick, 1);
   }

   @Override
   public void serverTick() {
      super.serverTick();
      // the extra charge slot of the storage blocks was never ticked, so
      // batteries put into a batbox/cesu/mfe/mfsu/efsu simply never charged
      com.industrialreforged.tech.core.util.EnergySlotHelper.tick(this.charge, this.energy);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putByte("redstoneMode", this.redstoneMode);
      this.charge.save(tag, registries);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.redstoneMode = tag.getByte("redstoneMode");
      this.charge.load(tag, registries);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new ElectricBlockMenu(containerId, playerInventory, this);
   }

   @Nullable
   @Override
   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return super.getEnergyWrapper(direction);
   }

   /** Накопители ограничены своей скоростью приёма. */
   @Override
   public boolean unlimitedInput() {
      return false;
   }

   protected boolean shouldEmitRedstone() {
      if (this.redstoneMode > 0 && this.redstoneMode <= 4) {
         long storage = this.energy.getStored();
         long capacity = this.energy.getCapacity();
         long extract = this.energy.getExtract();

         return switch (this.redstoneMode) {
            case 1 -> storage >= capacity - extract * 20.0;
            case 2 -> storage > extract && storage < capacity - extract;
            case 3 -> storage < capacity - extract;
            case 4 -> storage < extract;
            default -> false;
         };
      } else {
         return false;
      }
   }

   protected boolean shouldEmitEnergy() {
      boolean hasSignal = this.redstoneSignal;
      if (this.redstoneMode == 5) {
         return !hasSignal;
      } else {
         return this.redstoneMode != 6 ? true : !hasSignal || this.energy.getStored() > this.energy.getCapacity() - this.energy.getExtract() * 20;
      }
   }

   public void onNetworkEvent(Player player, int n) {
      this.redstoneMode++;
      if (this.redstoneMode >= redstoneModes) {
         this.redstoneMode = 0;
      }

      MsgUtils.send(player, this.getRedstoneMode());
   }

   public Component getRedstoneMode() {
      return this.redstoneMode < redstoneModes && this.redstoneMode >= 0
         ? Component.translatable("text.industrialreforged.tooltip.charge_pad.mod.redstone." + this.redstoneMode)
         : Component.empty();
   }
}
