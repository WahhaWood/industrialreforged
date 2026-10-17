package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.fluid.FluidTank;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * Tank of a single solar distiller.  While the block belongs to a group of
 * touching distillers every operation is forwarded to the shared group tank, so
 * the whole wall uses one common buffer; on its own the block falls back to its
 * private storage.
 */
public class SolarDestillerTank extends FluidTank {
   private final SolarDestillerBlockEntity owner;
   private final boolean output;
   private final FluidTank single;

   public SolarDestillerTank(SolarDestillerBlockEntity owner, boolean output, int capacity, Predicate<FluidStack> filter) {
      super(capacity, !output, output, filter);
      this.owner = owner;
      this.output = output;
      this.single = new FluidTank(capacity, true, true, filter);
   }

   /** Tank the operations are forwarded to: the group's, or this block's own. */
   public FluidTank target() {
      SolarDestillerGroup group = this.owner.getDistillerGroup();
      return group == null ? this.single : (this.output ? group.output : group.input);
   }

   /** Private storage of this single block. */
   public FluidTank single() {
      return this.single;
   }

   @Override
   public int fill(FluidStack resource, FluidAction action) {
      return this.canFill ? this.target().fill(resource, action) : 0;
   }

   @Override
   public FluidStack drain(FluidStack resource, FluidAction action) {
      return this.canDrain ? this.target().drain(resource, action) : FluidStack.EMPTY;
   }

   @Override
   public FluidStack drain(int maxDrain, FluidAction action) {
      return this.canDrain ? this.target().drain(maxDrain, action) : FluidStack.EMPTY;
   }

   @Override
   public int fillForce(FluidStack resource, FluidAction action) {
      return this.target().fillForce(resource, action);
   }

   @Override
   public FluidStack drainForce(int maxDrain, FluidAction action) {
      return this.target().drainForce(maxDrain, action);
   }

   @Override
   public FluidStack getFluid() {
      return this.target().getFluid();
   }

   @Override
   public int getFluidAmount() {
      return this.target().getFluidAmount();
   }

   @Override
   public int getCapacity() {
      return this.target().getCapacity();
   }

   @Override
   public boolean isEmpty() {
      return this.target().isEmpty();
   }

   @Override
   public boolean isFull() {
      return this.target().isFull();
   }

   @Override
   public boolean hasSpace() {
      return this.target().hasSpace();
   }

   @Override
   public boolean isFluidValid(FluidStack stack) {
      return this.target().isFluidValid(stack);
   }

   @Override
   public void setFluid(FluidStack stack) {
      this.target().setFluid(stack);
   }

   @Override
   public net.neoforged.neoforge.fluids.capability.templates.FluidTank setCapacity(int capacity) {
      return this.target().setCapacity(capacity);
   }

   @Override
   public void load(Provider registries, CompoundTag tag, String key) {
      this.target().load(registries, tag, key);
   }

   @Override
   public void save(Provider registries, CompoundTag tag, String key) {
      this.target().save(registries, tag, key);
   }
}
