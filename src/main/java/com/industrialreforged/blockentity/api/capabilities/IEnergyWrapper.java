package com.industrialreforged.blockentity.api.capabilities;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public interface IEnergyWrapper {
   IEnergyStorage getEnergyWrapper(@Nullable Direction var1);

   /** Машины принимают энергию без ограничений по скорости; накопители переопределяют. */
   default boolean unlimitedInput() {
      return false;
   }
}
