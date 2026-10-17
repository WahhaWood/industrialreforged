package com.industrialreforged.tech.api.tile;

import net.minecraft.core.Direction;

public interface IHeatSource {
   int getHeat(Direction var1);

   int extractHeat(Direction var1, int var2, boolean var3);
}
