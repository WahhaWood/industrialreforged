package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.item.ItemCell;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class IRFluids {
   public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(Registries.FLUID, "industrialreforged");
   public static final IRFluids.FluidRefs UU_MATTER = register("uu_matter", 3000, 3000, 0, 300, false, true);
   public static final IRFluids.FluidRefs CONSTRUCTION_FOAM = register("construction_foam", 10000, 50000, 0, 300, false, true);
   public static final IRFluids.FluidRefs COOLANT = register("coolant", 1000, 3000, 0, 300, false, true);
   public static final IRFluids.FluidRefs HOT_COOLANT = register("hot_coolant", 1000, 3000, 0, 1200, false, false);
   public static final IRFluids.FluidRefs PAHOEHOE_LAVA = register("pahoehoe_lava", 50000, 250000, 10, 1200, false, false);
   public static final IRFluids.FluidRefs BIOMASS = register("biomass", 1000, 3000, 0, 300, false, true);
   public static final IRFluids.FluidRefs BIOGAS = register("biogas", 1000, 3000, 0, 300, true, false);
   public static final IRFluids.FluidRefs DISTILLED_WATER = register("distilled_water", 1000, 1000, 0, 300, false, true);
   public static final IRFluids.FluidRefs STEAM = register("steam", -800, 300, 0, 420, true, false);
   public static final IRFluids.FluidRefs HOT_WATER = register("hot_water", 1000, 1000, 0, 350, false, true);

   private static IRFluids.FluidRefs register(String name, int density, int viscosity, int luminosity, int temperature, boolean gas, boolean flowingTex) {
      AtomicReference<IRFluids.FluidRefs> atomicReference = new AtomicReference<>();
      FluidType type = new FluidType(
         Properties.create().descriptionId("fluid.industrialreforged." + name).density(density).viscosity(viscosity).lightLevel(luminosity).temperature(temperature)
      );
      net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties properties = new net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties(
         () -> type, () -> atomicReference.get().getFluidStill(), () -> atomicReference.get().getFluidFlowing()
      );
      Supplier<Fluid> still = REGISTRY.register(name, () -> new Source(properties));
      Supplier<Fluid> flowing = flowingTex ? REGISTRY.register("flowing_" + name, () -> new Flowing(properties)) : null;
      atomicReference.set(new IRFluids.FluidRefs(still, flowing));
      return atomicReference.get();
   }

   public record FluidRefs(@NotNull Supplier<Fluid> still, @Nullable Supplier<Fluid> flowing) {
      public Fluid getFluidStill() {
         return this.still.get();
      }

      public Fluid getFluidFlowing() {
         return this.flowing == null ? null : this.flowing.get();
      }

      public ItemStack getCell() {
         return ItemCell.getCell(this.getFluidStill());
      }
   }
}
