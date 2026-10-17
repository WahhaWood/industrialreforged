package com.industrialreforged.tech.api.recipe;

import java.util.Map;
import net.minecraft.world.level.material.Fluid;

public interface ISemiFluidFuelManager extends ILiquidAcceptManager {
   void addFluid(Fluid var1, int var2, int var3);

   ISemiFluidFuelManager.BurnProperty getBurnProperty(Fluid var1);

   Map<Fluid, ISemiFluidFuelManager.BurnProperty> getBurnProperties();

   record BurnProperty(int amount, int power) {
   }
}
