package com.industrialreforged.tech.client;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRFluids;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

/** Client-only fluid appearance registration. */
public final class IRClientFluids {
   private IRClientFluids() {
   }

   public static void registerExtensions(RegisterClientExtensionsEvent event) {
      event.registerFluidType(
         ClientFluidTypeExtensions.of("uu_matter", "uu_matter", -5041231), new FluidType[]{IRFluids.UU_MATTER.getFluidStill().getFluidType()}
      );
      event.registerFluidType(
         ClientFluidTypeExtensions.of("fluid_3", "hot_coolant", -199089630), new FluidType[]{IRFluids.CONSTRUCTION_FOAM.getFluidStill().getFluidType()}
      );
      event.registerFluidType(ClientFluidTypeExtensions.of("coolant", "coolant", -14908012), new FluidType[]{IRFluids.COOLANT.getFluidStill().getFluidType()});
      event.registerFluidType(
         ClientFluidTypeExtensions.of("hot_coolant", null, -2275758), new FluidType[]{IRFluids.HOT_COOLANT.getFluidStill().getFluidType()}
      );
      event.registerFluidType(
         ClientFluidTypeExtensions.of("pahoehoe_lava", null, -1998041), new FluidType[]{IRFluids.PAHOEHOE_LAVA.getFluidStill().getFluidType()}
      );
      event.registerFluidType(ClientFluidTypeExtensions.of("fluid", "fluid", -1237485016), new FluidType[]{IRFluids.BIOMASS.getFluidStill().getFluidType()});
      event.registerFluidType(ClientFluidTypeExtensions.of("fluid_3", null, -188435879), new FluidType[]{IRFluids.BIOGAS.getFluidStill().getFluidType()});
      event.registerFluidType(
         ClientFluidTypeExtensions.of("fluid_water", "fluid_water", -630950925), new FluidType[]{IRFluids.DISTILLED_WATER.getFluidStill().getFluidType()}
      );
      event.registerFluidType(ClientFluidTypeExtensions.of("fluid_3", null, -186852132), new FluidType[]{IRFluids.STEAM.getFluidStill().getFluidType()});
      event.registerFluidType(
         ClientFluidTypeExtensions.of("fluid_water", "fluid_water", -632884747), new FluidType[]{IRFluids.HOT_WATER.getFluidStill().getFluidType()}
      );
   }

   private record ClientFluidTypeExtensions(@NotNull ResourceLocation still, @Nullable ResourceLocation flow, int color) implements IClientFluidTypeExtensions {
      @NotNull
      public ResourceLocation getStillTexture() {
         return this.still;
      }

      @Nullable
      public ResourceLocation getFlowingTexture() {
         return this.flow;
      }

      public int getTintColor() {
         return this.color;
      }

      public static ClientFluidTypeExtensions of(String stillTex, String flowingTex, int color) {
         return new ClientFluidTypeExtensions(
            IR.location("block/fluid/" + stillTex + "_still"), flowingTex == null ? null : IR.location("block/fluid/" + flowingTex + "_flow"), color
         );
      }
   }

}
