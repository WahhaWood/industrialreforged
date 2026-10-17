package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.recipe.Recipes;
import com.industrialreforged.tech.blockentity.base.AbstractElectricGeneratorBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByManager;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.recipe.manager.SemiFluidFuelManager;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.generator.electric.SemifluidGeneratorMenu;
import net.minecraft.core.BlockPos;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class SemifluidGeneratorBlockEntity extends AbstractElectricGeneratorBlockEntity implements IFluidWrapper {
   public final InvSlotConsumableLiquid input;
   public final InvSlotOutput output;
   public final FluidTank tank = new FluidTank(10000, true, false, stack -> Recipes.semiFluidGenerator.acceptsFluid(stack.getFluid()));

   public SemifluidGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.SEMIFLUID_GENERATOR.get(), blockPos, blockState, 2048000L, 128L);
      this.input = new InvSlotConsumableLiquidByManager(this, "fluidSlot", 1, Recipes.semiFluidGenerator);
      this.output = new InvSlotOutput(this, 1);
   }

   public static void init() {
      Recipes.semiFluidGenerator = new SemiFluidFuelManager();
      addFuel(IRFluids.BIOGAS.getFluidStill(), 10, 64);
   }

   public static void addFuel(Fluid fluid, int amount, int power) {
      Recipes.semiFluidGenerator.addFluid(fluid, amount, power);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.input.load(tag, registries);
      this.output.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.tank.save(registries, tag, "tank");
      this.input.save(tag, registries);
      this.output.save(tag, registries);
   }

   @Override
   public boolean isConverting() {
      return this.fuel > 0;
   }

   @Override
   protected boolean consumesFuel() {
      return true;
   }

   @Override
   protected int consumeFuel() {
      net.minecraft.world.level.material.Fluid fluid = this.tank.getFluid().getFluid();
      com.industrialreforged.tech.api.recipe.ISemiFluidFuelManager.BurnProperty property =
         Recipes.semiFluidGenerator.getBurnProperty(fluid);
      if (property == null) {
         return 0;
      }

      net.neoforged.neoforge.fluids.FluidStack simulated =
         this.tank.drainForce(property.amount(), net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
      if (simulated.getAmount() < property.amount()) {
         return 0;
      }

      this.tank.drainForce(property.amount(), net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
      this.production = property.power();
      return property.amount() * 20;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SemifluidGeneratorMenu(containerId, playerInventory, this);
   }

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         return new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.input.createWrapper(), this.output.createWrapper(), this.container.createWrapper()}
         );
      }

      return switch (direction) {
         case UP -> this.input.createWrapper();
         case DOWN -> this.output.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.input.createWrapper(), this.output.createWrapper()}
         );
      };
   }
}
