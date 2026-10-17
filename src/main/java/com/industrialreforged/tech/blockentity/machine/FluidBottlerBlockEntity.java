package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.menu.machine.FluidBottlerMenu;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class FluidBottlerBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IFluidWrapper, IContainerWrapper {
   public final InvSlotConsumableLiquid drainInputSlot;
   public final InvSlotConsumableLiquid fillInputSlot;
   public final InvSlotOutput outputSlot;
   public final InvSlotUpgrade upgradeSlot;
   public final FluidTank tank;
   public final int defaultEnergyConsume;
   public final int defaultOperationLength;
   public int energyConsume;
   public int operationLength;
   public int operationsPerTick;
   public float guiProgress;
   protected int progress;
   private boolean starved;
   private CombinedInvWrapper wrapper;
   private final InvWrapper drainInputHandler;
   private final InvWrapper fillInputHandler;
   private final InvWrapper outputHandler;

   public FluidBottlerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(256, 128, IRBlockEntities.FLUID_BOTTLER.get(), blockPos, blockState);
      this.defaultEnergyConsume = this.energyConsume = 2;
      this.defaultOperationLength = this.operationLength = 100;
      this.operationsPerTick = 1;
      this.tank = new FluidTank(8000, true, true);
      this.drainInputSlot = new InvSlotConsumableLiquidByTank(this, "drainInput", 1, InvSlotConsumableLiquid.OpType.Drain, this.tank);
      this.fillInputSlot = new InvSlotConsumableLiquidByTank(this, "fillInput", 1, InvSlotConsumableLiquid.OpType.Fill, this.tank);
      this.outputSlot = new InvSlotOutput(this, 1);
      this.upgradeSlot = new InvSlotUpgrade(this, 4);
      this.upgradeSlot.addListener(container -> this.setBottlerRates());
      this.drainInputHandler = new InvWrapper(this.drainInputSlot);
      this.fillInputHandler = new InvWrapper(this.fillInputSlot);
      this.outputHandler = new InvWrapper(this.outputSlot);
   }

   public float getProgress() {
      return this.guiProgress;
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_EXTRACTOR_START.get();
   }

   @Override
   protected boolean isSoundproofed() {
      return this.upgradeSlot.soundproofing;
   }

   @Override
   protected boolean hasWork() {
      return this.progress > 0;
   }

   public void onLoad() {
      super.onLoad();
      this.upgradeSlot.setChanged();
      this.setBottlerRates();
   }

   /**
    * Пересчёт ставок от улучшений. Укрепление на фасовщике — это скорость цикла,
    * а не пакет предметов (FluidSlotHelper и так двигает до 64 контейнеров за
    * завершение): цикл короче в batch раз, тиковый расход — полный
    * energyConsume × batch × 1.5, как у стандартных машин. Штраф 1.15^batch из
    * getOperationLength здесь не применяем: ему нечего компенсировать, скорость
    * и есть пакет. Ёмкость растим через demandBuffer, не урезаем (старые миры).
    */
   public void setBottlerRates() {
      double d = this.operationLength <= 0 ? 0.0 : (double)this.progress / this.operationLength;
      this.operationsPerTick = this.upgradeSlot.getOperationsPerTick();
      int batch = Math.max(1, this.operationsPerTick);
      this.operationLength = Math.max(
         1, (int)Math.round(this.defaultOperationLength * this.upgradeSlot.processTimeMultiplier / batch)
      );
      this.energyConsume = this.upgradeSlot.getEnergyDemand(this.defaultEnergyConsume);
      long demandBuffer = (long)this.energyConsume * Math.max(1, this.operationLength) * batch;
      int capacity = this.upgradeSlot.getEnergyCapacity(this.energy.getDefCapacity());
      if (demandBuffer > capacity) {
         capacity = demandBuffer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)demandBuffer;
      }
      this.energy.setCapacity(capacity);
      this.progress = (int)Math.floor(d * this.operationLength + 0.1);
   }

   /** Полный тиковый расход: база × пакет укрепления × 1.5 (как списывает тик). */
   public int fullTickCost() {
      return (int)Math.min(
         Integer.MAX_VALUE,
         Math.round((double)this.energyConsume * Math.max(1, this.operationsPerTick)
            * this.upgradeSlot.getStrengtheningEnergyMultiplier())
      );
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.drainInputSlot.load(tag, registries);
      this.fillInputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.progress = tag.getInt("progress");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.drainInputSlot.save(tag, registries);
      this.fillInputSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      this.tank.save(registries, tag, "tank");
      tag.putInt("progress", this.progress);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new FluidBottlerMenu(containerId, playerInventory, this);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.FLUID_CONSUMING,
         UpgradableProperty.FLUID_PRODUCING,
         UpgradableProperty.STRENGTHENING
      );
   }

   @Override
   protected void tickFluidSlots() {
   }

   private boolean hasFluidWork() {
      return !this.drainInputSlot.isEmpty(0) && this.tank.hasSpace()
         || !this.fillInputSlot.isEmpty(0) && this.tank.getFluidAmount() > 0;
   }

   /** Наполняет и опустошает контейнеры: цикл 100 тиков, тиковый расход — полный (см. fullTickCost). */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      boolean active = false;
      boolean work = this.hasFluidWork();
      int cost = this.fullTickCost();
      if (work && this.energy.getStored() >= cost) {
         this.energy.extractForceEnergy(cost);
         active = true;
         this.progress++;
         if (this.progress >= this.operationLength) {
            this.progress = 0;
            FluidSlotHelper.tick(this.drainInputSlot, this.outputSlot, this.tank);
            FluidSlotHelper.tick(this.fillInputSlot, this.outputSlot, this.tank);
         }
      } else if (!work) {
         this.progress = 0;
      }

      this.starved = work && this.progress > 0 && this.energy.getStored() < cost;
      this.guiProgress = this.operationLength <= 0 ? 0.0F : (float)this.progress / this.operationLength;
      com.industrialreforged.block.ActiveBlock.setActive(this, active);
   }

   @Override
   public int getConsumption() {
      return this.fullTickCost();
   }

   @Override
   public int getTimeCreation() {
      return this.operationLength;
   }

   @Override
   public boolean isStarved() {
      return this.starved;
   }

   @Override
   public void setStarved(boolean value) {
      this.starved = value;
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }

   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.wrapper == null) {
         this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.fillInputHandler, this.drainInputHandler, this.outputHandler});
      }

      if (direction == null) {
         return this.wrapper;
      }

      return switch (direction) {
         case UP -> this.drainInputHandler;
         case DOWN -> this.outputHandler;
         case NORTH, SOUTH, EAST, WEST -> this.fillInputHandler;
         default -> throw new MatchException(null, null);
      };
   }
}
