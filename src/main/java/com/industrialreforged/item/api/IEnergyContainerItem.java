package com.industrialreforged.item.api;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.utils.NumberUtils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface IEnergyContainerItem extends ItemLike {
   default int getEnergyStored(ItemStack stack) {
      return (Integer)stack.getOrDefault(GEDataComponents.ENERGY, 0);
   }

   int getEnergyCapacity(ItemStack var1);

   int getEnergyReceiveTick(ItemStack var1);

   int getEnergyExtractTick(ItemStack var1);

   default void appendEnergy(ItemStack stack, List<Component> tooltipComponents) {
      tooltipComponents.add(
         Component.translatable(
            "text.industrialreforged.fe.ratio",
            new Object[]{NumberUtils.formatToSi(this.getEnergyStored(stack)), NumberUtils.formatToSi(this.getEnergyCapacity(stack))}
         )
      );
   }

   default int receiveEnergy(ItemStack stack, int amount, boolean simulate) {
      int storage = this.getEnergyStored(stack);
      if (storage < this.getEnergyCapacity(stack) && amount > 0) {
         if (this.getEnergyReceiveTick(stack) < amount) {
            amount = this.getEnergyReceiveTick(stack);
         }

         int free = this.getEnergyCapacity(stack) - storage;
         if (free < amount) {
            amount = free;
         }

         if (!simulate && amount > 0) {
            stack.set(GEDataComponents.ENERGY, storage + amount);
         }

         return amount;
      } else {
         return 0;
      }
   }

   default int extractEnergy(ItemStack stack, int amount, boolean simulate) {
      int storage = this.getEnergyStored(stack);
      if (storage > 0 && amount > 0) {
         if (this.getEnergyExtractTick(stack) < amount) {
            amount = this.getEnergyExtractTick(stack);
         }

         if (storage < amount) {
            amount = storage;
         }

         if (!simulate && amount > 0) {
            stack.set(GEDataComponents.ENERGY, storage - amount);
         }

         return amount;
      } else {
         return 0;
      }
   }

   default boolean use(ItemStack stack, int amount, boolean simulate) {
      int storage = this.getEnergyStored(stack);
      if (storage < amount) {
         return false;
      }

      if (!simulate) {
         stack.set(GEDataComponents.ENERGY, storage - amount);
      }

      return true;
   }

   default float getRatio(ItemStack stack) {
      return (float)this.getEnergyStored(stack) / this.getEnergyCapacity(stack);
   }
}
