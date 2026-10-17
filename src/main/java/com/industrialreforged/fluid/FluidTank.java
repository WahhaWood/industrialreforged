package com.industrialreforged.fluid;

import java.util.function.Predicate;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidTank extends net.neoforged.neoforge.fluids.capability.templates.FluidTank {
   public final boolean canFill;
   public final boolean canDrain;

   public FluidTank(int capacity) {
      this(capacity, fluid -> true);
   }

   public FluidTank(int capacity, Predicate<FluidStack> validation) {
      this(capacity, true, true, validation);
   }

   public FluidTank(int capacity, boolean canFill, boolean canDrain) {
      this(capacity, canFill, canDrain, fluid -> true);
   }

   public FluidTank(int capacity, boolean canFill, boolean canDrain, Predicate<FluidStack> validation) {
      super(capacity, validation);
      this.canFill = canFill;
      this.canDrain = canDrain;
   }

   public int fill(FluidStack resource, FluidAction action) {
      return this.canFill ? super.fill(resource, action) : 0;
   }

   public FluidStack drain(FluidStack resource, FluidAction action) {
      return this.canDrain ? super.drain(resource, action) : FluidStack.EMPTY;
   }

   public FluidStack drain(int maxDrain, FluidAction action) {
      return this.canDrain ? super.drain(maxDrain, action) : FluidStack.EMPTY;
   }

   public int fillForce(FluidStack resource, FluidAction action) {
      return super.fill(resource, action);
   }

   public FluidStack drainForce(int maxDrain, FluidAction action) {
      return super.drain(maxDrain, action);
   }

   public boolean isFull() {
      return this.getFluidAmount() >= this.getCapacity();
   }

   public boolean hasSpace() {
      return this.getSpace() > 0;
   }

   public void load(Provider registries, CompoundTag tag, String key) {
      this.deserialize(registries, tag.getCompound(key));
   }

   public void deserialize(Provider registries, CompoundTag tag) {
      this.fluid = FluidStack.parseOptional(registries, tag);
   }

   public void save(Provider registries, CompoundTag tag, String key) {
      if (!this.fluid.isEmpty()) {
         tag.put(key, this.serialize(registries));
      }
   }

   public Tag serialize(Provider registries) {
      return this.fluid.save(registries);
   }
}
