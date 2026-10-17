package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.recipe.ILiquidHeatExchangerManager;
import com.industrialreforged.tech.api.recipe.Recipes;
import com.industrialreforged.tech.api.tile.FluidHandler;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractHeatSourceBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByManager;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.recipe.manager.LiquidHeatExchangerManager;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.menu.machine.LiquidHeatExchangerMenu;
import java.util.EnumSet;
import java.util.Set;
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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class LiquidHeatExchangerBlockEntity extends AbstractHeatSourceBlockEntity implements IUpgradableBlock, IFluidWrapper {
   public final FluidTank inputTank = new FluidTank(2000, true, false, stack -> Recipes.liquidCooldownManager.acceptsFluid(stack.getFluid()));
   public final FluidTank outputTank = new FluidTank(2000, false, true);
   public final InvSlotConsumable heatexchangerslots = new InvSlotConsumableItem(this, "conductors", 10, IRItems.HEAT_CONDUCTOR);
   public final InvSlotOutput hotoutputSlot;
   public final InvSlotOutput cooloutputSlot;
   public final InvSlotConsumableLiquid hotfluidinputSlot;
   public final InvSlotConsumableLiquid coolfluidinputSlot;
   public final InvSlotUpgrade upgradeSlot;
   private FluidHandler fluidHandler;

   public LiquidHeatExchangerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.LIQUID_HEAT_EXCHANGER.get(), blockPos, blockState);
      this.heatexchangerslots.setMaxStackSize(1);
      this.hotoutputSlot = new InvSlotOutput(this, "hotOutputSlot", 1);
      this.cooloutputSlot = new InvSlotOutput(this, "outputSlot", 1);
      this.hotfluidinputSlot = new InvSlotConsumableLiquidByManager(
         this, "hotFluidInput", 1, InvSlotConsumableLiquid.OpType.Drain, Recipes.liquidCooldownManager
      );
      this.coolfluidinputSlot = new InvSlotConsumableLiquidByTank(this, "coolFluidOutput", 1, InvSlotConsumableLiquid.OpType.Fill, this.outputTank);
      this.upgradeSlot = new InvSlotUpgrade(this, 3);
   }

   public static void init() {
      Recipes.liquidCooldownManager = new LiquidHeatExchangerManager(false);
      Recipes.liquidHeatupManager = new LiquidHeatExchangerManager(true);
      addCooldownRecipe(Fluids.LAVA, IRFluids.PAHOEHOE_LAVA.getFluidStill(), 20);
      addBiDiRecipe(IRFluids.HOT_COOLANT.getFluidStill(), IRFluids.COOLANT.getFluidStill(), 20);
      addHeatupRecipe(IRFluids.HOT_WATER.getFluidStill(), Fluids.WATER, 1);
   }

   public static void addBiDiRecipe(Fluid fluid, Fluid fluid2, int n) {
      addHeatupRecipe(fluid, fluid2, n);
      addCooldownRecipe(fluid, fluid2, n);
   }

   public static void addHeatupRecipe(Fluid fluid, Fluid fluid2, int n) {
      Recipes.liquidHeatupManager.addFluid(fluid2, fluid, n);
   }

   public static void addCooldownRecipe(Fluid fluid, Fluid fluid2, int n) {
      Recipes.liquidCooldownManager.addFluid(fluid, fluid2, n);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_GEOTHERMAL_LOOP.get();
   }

   @Override
   protected boolean isSoundproofed() {
      return this.upgradeSlot.soundproofing;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.heatexchangerslots.load(tag, registries);
      this.hotoutputSlot.load(tag, registries);
      this.cooloutputSlot.load(tag, registries);
      this.hotfluidinputSlot.load(tag, registries);
      this.coolfluidinputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.inputTank.load(registries, tag, "inputTank");
      this.outputTank.load(registries, tag, "outputTank");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.heatexchangerslots.save(tag, registries);
      this.hotoutputSlot.save(tag, registries);
      this.cooloutputSlot.save(tag, registries);
      this.hotfluidinputSlot.save(tag, registries);
      this.coolfluidinputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      this.inputTank.save(registries, tag, "inputTank");
      this.outputTank.save(registries, tag, "outputTank");
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new LiquidHeatExchangerMenu(containerId, playerInventory, this);
   }

   @Override
   public int getMaxHeatEmittedPerTick() {
      int n = 0;

      for (int i = 0; i < this.heatexchangerslots.getContainerSize(); i++) {
         if (!this.heatexchangerslots.isEmpty(i)) {
 n += 10;
         }
      }

      return n;
   }

   @Override
   protected int fillHeatBuffer(int n) {
      if (n > 0) {
         int n2 = this.inputTank.getFluidAmount();
         int n3 = this.outputTank.getSpace();
         if (n3 == 0 || n2 == 0) {
            return 0;
         }

         Fluid fluid = this.inputTank.getFluid().getFluid();
         Fluid fluid2 = null;
         int n4 = 0;
         if (Recipes.liquidCooldownManager.acceptsFluid(fluid)) {
            ILiquidHeatExchangerManager.HeatExchangeProperty heatExchangeProperty = Recipes.liquidCooldownManager.getHeatExchangeProperty(fluid);
            fluid2 = heatExchangeProperty.outputFluid();
            n4 = heatExchangeProperty.huPerMB();
         }

         if (fluid2 == null) {
            return 0;
         }

         if (this.outputTank.getFluidAmount() > 0 && this.outputTank.getFluid().getFluid() != fluid2) {
            return 0;
         }

         int n5 = n / n4;
         FluidStack fluidStack = n3 >= n2
            ? (n5 <= n2 ? this.inputTank.drainForce(n5, FluidAction.SIMULATE) : this.inputTank.drainForce(n2, FluidAction.SIMULATE))
            : (n5 <= n3 ? this.inputTank.drainForce(n5, FluidAction.SIMULATE) : this.inputTank.drainForce(n3 * 20, FluidAction.SIMULATE));
         if (!fluidStack.isEmpty()) {
            this.inputTank.drainForce(fluidStack.getAmount(), FluidAction.EXECUTE);
            this.outputTank.fillForce(new FluidStack(fluid2, fluidStack.getAmount()), FluidAction.EXECUTE);
            return fluidStack.getAmount() * n4;
         }
      }

      return 0;
   }

   /** Превращает горячую жидкость в холодную, наполняя буфер тепла. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      boolean active = false;
      if (this.getHeat() < MAX_HEAT) {
         int produced = this.fillHeatBuffer(this.getMaxHeatEmittedPerTick());
         if (produced > 0) {
            this.increaseHeat(Math.min(produced, MAX_HEAT - this.getHeat()));
            active = true;
         }
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, active);
      // active означает произведённое тепло (бак и тепло изменились); в простое — не пачкаем.
      if (active) {
         this.markForSave();
      }
   }

   public FluidTank getInputTank() {
      return this.inputTank;
   }

   public FluidTank getOutputTank() {
      return this.outputTank;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.FLUID_CONSUMING, UpgradableProperty.FLUID_PRODUCING
      );
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.hotfluidinputSlot, this.hotoutputSlot, this.inputTank);
      FluidSlotHelper.tick(this.coolfluidinputSlot, this.cooloutputSlot, this.outputTank);
   }

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      if (direction == Direction.UP) {
         return this.inputTank;
      }
      if (direction == Direction.DOWN) {
         return this.outputTank;
      }
      if (this.fluidHandler == null) {
         this.fluidHandler = new FluidHandler(this.inputTank, this.outputTank);
      }

      return this.fluidHandler;
   }

   @Override
   public int getEnergyStored() {
      return 40;
   }

   @Override
   public boolean useEnergy(int d) {
      return true;
   }
}
