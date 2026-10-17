package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyContainer extends BasicSimpleContainer {
   public int receiveTick;
   public int extractTick;
   public boolean allowRedstoneDust;

   public EnergyContainer(BaseBlockEntity entity, int receiveTick) {
      this(entity, receiveTick, 1);
   }

   public EnergyContainer(BaseBlockEntity entity, int receiveTick, int countSlot) {
      super(entity, "charge", countSlot);
      this.receiveTick = receiveTick;
   }

   /** Charge container stored under a custom NBT name (keeps old saves readable). */
   public EnergyContainer(BaseBlockEntity entity, String name, int receiveTick, int countSlot) {
      super(entity, name, countSlot);
      this.receiveTick = receiveTick;
   }

   public EnergyContainer(BaseBlockEntity entity, int extractTick, boolean allowRedstoneDust) {
      this(entity, extractTick, allowRedstoneDust, 1);
   }

   public EnergyContainer(BaseBlockEntity entity, int extractTick, boolean allowRedstoneDust, int countSlot) {
      super(entity, "discharge", countSlot);
      this.extractTick = extractTick;
      this.allowRedstoneDust = allowRedstoneDust;
   }

   public boolean canPlaceItem(int index, ItemStack itemStack) {
      IEnergyStorage storage = (IEnergyStorage)itemStack.getCapability(EnergyStorage.ITEM);
      if (storage == null) {
         return false;
      } else if (this.receiveTick > 0) {
         return storage.canReceive() && storage.getEnergyStored() < storage.getMaxEnergyStored();
      } else {
         return this.extractTick <= 0 ? false : storage.canExtract() && storage.getEnergyStored() > 0;
      }
   }

   public void setReceiveTick(int value) {
      this.receiveTick = value;
   }

   public void setExtractTick(int value) {
      this.extractTick = value;
   }
}
