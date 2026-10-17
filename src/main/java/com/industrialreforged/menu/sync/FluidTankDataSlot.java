package com.industrialreforged.menu.sync;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidTankDataSlot extends AbstractCustomDataSlot {
   private final FluidTank tank;
   private Fluid fluid;
   private int amount;
   private int capacity;

   public FluidTankDataSlot(FluidTank tank) {
      this.tank = tank;
   }

   @Override
   public boolean check() {
      FluidStack stack = this.tank.getFluid();
      return this.fluid == null || !stack.is(this.fluid) || stack.getAmount() != this.amount || this.tank.getCapacity() != this.capacity;
   }

   @Override
   public void update(ServerPlayer player) {
      this.fluid = this.tank.getFluid().getFluid();
      this.amount = this.tank.getFluidAmount();
      this.capacity = this.tank.getCapacity();
      super.update(player);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeNullable(buf.registryAccess().registryOrThrow(Registries.FLUID).getKey(this.fluid), FriendlyByteBuf::writeResourceLocation);
      buf.writeVarInt(this.amount);
      buf.writeVarInt(this.capacity);
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.fluid = (Fluid)buf.registryAccess().registryOrThrow(Registries.FLUID).get((ResourceLocation)buf.readNullable(FriendlyByteBuf::readResourceLocation));
      this.amount = buf.readVarInt();
      this.capacity = buf.readVarInt();
      FluidStack stack = this.tank.getFluid();
      if (!stack.is(this.fluid)) {
         this.tank.setFluid(new FluidStack(this.fluid, this.amount));
      } else {
         stack.setAmount(this.amount);
      }

      this.tank.setCapacity(this.capacity);
   }
}
