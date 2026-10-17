package com.industrialreforged.tech.core.recipe.manager;

import com.industrialreforged.tech.api.recipe.ILiquidAcceptManager;
import com.industrialreforged.tech.api.recipe.ILiquidHeatExchangerManager;
import com.industrialreforged.tech.api.recipe.Recipes;
import com.industrialreforged.tech.core.IR;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.world.level.material.Fluid;

public class LiquidHeatExchangerManager implements ILiquidHeatExchangerManager {
   private final boolean heatup;
   private final LiquidHeatExchangerManager.SingleDirectionManager singleDirectionManager;
   private final Map<Fluid, ILiquidHeatExchangerManager.HeatExchangeProperty> map = new IdentityHashMap<>();

   public LiquidHeatExchangerManager(boolean bl) {
      this.heatup = bl;
      this.singleDirectionManager = new LiquidHeatExchangerManager.SingleDirectionManager();
   }

   @Override
   public boolean acceptsFluid(Fluid fluid) {
      return this.map.containsKey(fluid);
   }

   @Override
   public Set<Fluid> getAcceptedFluids() {
      return Collections.unmodifiableSet(this.map.keySet());
   }

   @Override
   public void addFluid(Fluid fluid, Fluid fluid2, int n) {
      if (this.map.containsKey(fluid)) {
         this.displayError("The fluid " + fluid + " does already have a HeatExchangerProperty assigned.");
      } else if (n == 0) {
         this.displayError("A mod tried to register a Fluid for the HeatExchanging recipe, without having an Energy value. Ignoring...");
      } else {
         int tempFluid = fluid.getFluidType().getTemperature();
         int tempFluid2 = fluid2.getFluidType().getTemperature();
         if (this.heatup) {
            if (tempFluid >= tempFluid2) {
               this.displayError("Cannot heat up a warm liquid into a cold one. " + fluid + " -> " + fluid2);
            }
         } else if (tempFluid <= tempFluid2) {
            this.displayError("Cannot cool down a cold liquid into a warm one. " + fluid + " -> " + fluid2);
         }

         this.map.put(fluid, new ILiquidHeatExchangerManager.HeatExchangeProperty(fluid2, Math.abs(n)));
      }
   }

   @Override
   public ILiquidHeatExchangerManager.HeatExchangeProperty getHeatExchangeProperty(Fluid fluid) {
      return this.map.get(fluid);
   }

   @Override
   public Map<Fluid, ILiquidHeatExchangerManager.HeatExchangeProperty> getHeatExchangeProperties() {
      return this.map;
   }

   private void displayError(String string) {
      IR.log.warn(string);
   }

   @Override
   public ILiquidAcceptManager getSingleDirectionLiquidManager() {
      return this.singleDirectionManager;
   }

   public ILiquidHeatExchangerManager getOpposite() {
      return this.heatup ? Recipes.liquidCooldownManager : Recipes.liquidHeatupManager;
   }

   public class SingleDirectionManager implements ILiquidAcceptManager {
      @Override
      public boolean acceptsFluid(Fluid fluid) {
         if (!LiquidHeatExchangerManager.this.acceptsFluid(fluid)) {
            return false;
         }

         ILiquidHeatExchangerManager.HeatExchangeProperty heatExchangeProperty = LiquidHeatExchangerManager.this.getHeatExchangeProperty(fluid);
         return !LiquidHeatExchangerManager.this.getOpposite().acceptsFluid(heatExchangeProperty.outputFluid());
      }

      @Override
      public Set<Fluid> getAcceptedFluids() {
         HashSet<Fluid> hashSet = new HashSet<>();
         ILiquidHeatExchangerManager iLiquidHeatExchangerManager = LiquidHeatExchangerManager.this.getOpposite();

         for (Entry<Fluid, ILiquidHeatExchangerManager.HeatExchangeProperty> entry : LiquidHeatExchangerManager.this.map.entrySet()) {
            if (!iLiquidHeatExchangerManager.acceptsFluid(entry.getValue().outputFluid())) {
               hashSet.add(entry.getKey());
            }
         }

         return hashSet;
      }
   }
}
