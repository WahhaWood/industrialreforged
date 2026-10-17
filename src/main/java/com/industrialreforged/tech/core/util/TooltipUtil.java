package com.industrialreforged.tech.core.util;

import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.utils.NumberUtils;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class TooltipUtil {
   public static Component ratio(IEnergyStorage energyStorage) {
      long storage;
      long capacity;
      if (energyStorage instanceof BigEnergyStorage s) {
         storage = s.getStored();
         capacity = s.getCapacity();
      } else {
         storage = energyStorage.getEnergyStored();
         capacity = energyStorage.getMaxEnergyStored();
      }

      return Component.translatable("text.industrialreforged.fe.ratio", new Object[]{NumberUtils.formatToSi(storage), NumberUtils.formatToSi(capacity)});
   }
}
