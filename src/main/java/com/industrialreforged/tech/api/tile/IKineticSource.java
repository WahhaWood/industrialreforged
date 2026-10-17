package com.industrialreforged.tech.api.tile;

import net.minecraft.core.Direction;

public interface IKineticSource {
   int getKineticEnergy(Direction var1);

   int extractKineticEnergy(Direction var1, int var2, boolean var3);
}
