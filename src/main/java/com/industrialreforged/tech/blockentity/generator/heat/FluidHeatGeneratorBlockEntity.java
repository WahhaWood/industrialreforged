package com.industrialreforged.tech.blockentity.generator.heat;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.recipe.IFluidHeatManager;
import com.industrialreforged.tech.api.recipe.Recipes;
import com.industrialreforged.tech.blockentity.base.AbstractHeatSourceBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByManager;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.recipe.manager.FluidHeatManager;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.generator.heat.FluidHeatGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidHeatGeneratorBlockEntity extends AbstractHeatSourceBlockEntity implements IFluidWrapper {

   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      boolean burning = this.tank.getFluidAmount() > 0;
      if (burning) {
         this.tank.drainForce(1, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
         if (this.getHeat() < MAX_HEAT) {
            this.increaseHeat(this.getMaxHeatEmittedPerTick());
         }
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, burning);
      // В простое бак и тепло не меняются — чанк не пачкаем.
      if (burning) {
         this.markForSave();
      }
   }

   public final InvSlotConsumableLiquid fluidSlot;
   public final InvSlotOutput outputSlot;
   public final FluidTank tank;
   private short ticker = 0;
   protected int burnAmount = 0;
   protected int production = 0;
   boolean newActive = false;

   public FluidHeatGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.FLUID_HEAT_GENERATOR.get(), blockPos, blockState);
      this.tank = new FluidTank(10000, true, false, stack -> Recipes.semiFluidGenerator.acceptsFluid(stack.getFluid()));
      this.fluidSlot = new InvSlotConsumableLiquidByManager(this, "fluidSlot", 1, Recipes.fluidHeatGenerator);
      this.outputSlot = new InvSlotOutput(this, 1);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_GENERATOR_LOOP.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.fluidSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.tank.save(registries, tag, "tank");
      this.fluidSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
   }

   @Override
   protected int fillHeatBuffer(int n) {
      if (this.isConverting()) {
         if (this.ticker >= 19) {
            this.tank.drainForce(this.burnAmount, FluidAction.EXECUTE);
            this.ticker = 0;
         } else {
            this.ticker++;
         }

         this.newActive = true;
         return this.production;
      } else {
         this.newActive = false;
         return 0;
      }
   }

   @Override
   public int getMaxHeatEmittedPerTick() {
      return this.calcHeatProduction();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new FluidHeatGeneratorMenu(containerId, playerInventory, this);
   }

   public boolean isConverting() {
      return this.tank.getFluidAmount() > 0 && this.getHeat() < this.getMaxHeatEmittedPerTick();
   }

   private int calcHeatProduction() {
      IFluidHeatManager.BurnProperty burnProperty;
      if (!this.tank.isEmpty() && (burnProperty = Recipes.fluidHeatGenerator.getBurnProperty(this.tank.getFluid().getFluid())) != null) {
         this.production = burnProperty.heat();
         return this.production;
      } else {
         this.production = 0;
         return 0;
      }
   }

   private void calcBurnAmount() {
      IFluidHeatManager.BurnProperty burnProperty;
      if (!this.tank.isEmpty() && (burnProperty = Recipes.fluidHeatGenerator.getBurnProperty(this.tank.getFluid().getFluid())) != null) {
         this.burnAmount = burnProperty.amount();
      } else {
         this.burnAmount = 0;
      }
   }

   public static void init() {
      Recipes.fluidHeatGenerator = new FluidHeatManager();
      addFuel(IRFluids.BIOGAS.getFluidStill(), 10, 32);
   }

   public static void addFuel(Fluid fluid, int n, int n2) {
      Recipes.fluidHeatGenerator.addFluid(fluid, n, n2);
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }
}
