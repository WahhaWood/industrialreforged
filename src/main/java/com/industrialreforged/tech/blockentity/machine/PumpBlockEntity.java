package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.menu.machine.PumpMenu;
import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class PumpBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IFluidWrapper, IClientTick {
   public int energyConsume = 1;
   public int operationsPerTick;
   public final int defaultEnergyCapacity;
   public final int defaultEnergyConsume;
   public final int defaultOperationLength;
   private Object audioSource;
   public final InvSlotConsumableLiquid containerSlot = new InvSlotConsumableLiquid(this, "input", 1, InvSlotConsumableLiquid.OpType.Fill);
   public final InvSlotOutput outputSlot = new InvSlotOutput(this, 1);
   public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, 4);
   public final FluidTank tank;
   public short progress = 0;
   public int operationLength = 20;
   public float guiProgress;
   private boolean starved;
   private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
   private int searchOffsetY;
   private int delay = IR.random.nextInt(200);

   public PumpBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2048, 128, IRBlockEntities.PUMP.get(), blockPos, blockState);
      this.enableRedstoneSignal();
      this.defaultEnergyConsume = 1;
      this.defaultOperationLength = 20;
      this.defaultEnergyCapacity = 2048;
      this.upgradeSlot.addListener(container -> this.setUpgradestat());
      this.tank = new FluidTank(8000, false, true);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.containerSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.progress = tag.getShort("progress");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.containerSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      this.tank.save(registries, tag, "tank");
      tag.putShort("progress", this.progress);
   }

   public void onLoad() {
      super.onLoad();
      this.upgradeSlot.setChanged();
      this.setUpgradestat();
   }

   public void onChunkUnloaded() {
      if (IR.sideProxy.isRendering() && this.audioSource != null) {
         SoundUtil.stop(this.audioSource);
      }

      super.onChunkUnloaded();
   }

   public void clientTick() {
      if (this.isActive()) {
         if (this.audioSource == null) {
            this.audioSource = SoundUtil.loop(this, IRSounds.SOUND_MACHINE_pumpop);
         }

         if (SoundUtil.isStopped(this.audioSource)) {
            this.audioSource = SoundUtil.playSound(this.audioSource);
         }
      } else if (SoundUtil.isActive(this.audioSource)) {
         SoundUtil.stop(this.audioSource);
      }
   }

   public void setUpgradestat() {
      double d = (double)this.progress / this.operationLength;
      this.operationsPerTick = this.upgradeSlot.getOperationsPerTick();
      this.operationLength = this.upgradeSlot.getOperationLength(this.defaultOperationLength);
      this.energyConsume = this.upgradeSlot.getEnergyDemand(this.defaultEnergyConsume);
      this.progress = (short)Math.floor(d * this.operationLength + 0.1);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new PumpMenu(containerId, playerInventory, this);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.FLUID_PRODUCING,
         UpgradableProperty.STRENGTHENING
      );
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.containerSlot, this.outputSlot, this.tank);
   }

   /**
    * Цена одного блока-источника с наценкой укрепления (×1.5): сам пакет —
    * в operationsPerTick, его setUpgradestat уже читает.
    */
   public int sourceCost() {
      return Math.max(1, (int)Math.round(this.energyConsume * this.upgradeSlot.getStrengtheningEnergyMultiplier()));
   }

   /** Максимальный тиковый расход: пакет источников × цена источника (для панели). */
   @Override
   public int getConsumption() {
      return (int)Math.min(Integer.MAX_VALUE, (long)this.sourceCost() * Math.max(1, this.operationsPerTick));
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

   /**
    * Nothing used to call searchFluidSource(), so the pump did nothing at all.
    * Scan for fluid sources around the block, then pump them one per operation
    * into the internal tank, consuming energy per source.
    * Укрепление качает пакетом: до operationsPerTick источников за тик, каждый
    * по sourceCost(). Гейт — по одному источнику, дальше сколько хватит энергии:
    * помпа деградирует плавно (качает меньше), а не встаёт молча.
    */
   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (this.delay > 0) {
         this.delay--;
      }

      boolean active = false;
      // hasSpace() only asks for 1 mB of headroom, but a source block is worth 1000 mB:
      // with less room the old code still broke the block and destroyed the difference.
      int batch = Math.max(1, this.operationsPerTick);
      int cost = this.sourceCost();
      if (this.tank.getSpace() >= 1000 && this.energy.getStored() >= cost) {
         if (this.queue.isEmpty() && this.delay <= 0) {
            this.delay = 20;
            this.searchFluidSource(this.level, this.worldPosition.below());
         }

         for (int i = 0; i < batch && this.tank.getSpace() >= 1000 && this.energy.getStored() >= cost; i++) {
            BlockPos source = this.queue.poll();
            if (source == null) {
               break;
            }
            if (!this.isSource(this.level, source)) {
               continue;
            }
            net.minecraft.world.level.material.FluidState fluid = this.level.getFluidState(source);
            this.energy.extractForceEnergy(cost);
            int filled = this.tank.fillForce(
               new net.neoforged.neoforge.fluids.FluidStack(fluid.getType(), 1000),
               net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE
            );
            if (filled >= 1000) {
               this.level.setBlockAndUpdate(source, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
            }

            this.markForSave();
            if (filled > 0) {
               active = true;
            }
         }
      }
      this.starved = !this.queue.isEmpty() && this.tank.getSpace() >= 1000 && this.energy.getStored() < cost;

      if (active) {
         this.progress = (short)Math.min(this.operationLength, this.progress + 1);
         if (this.progress >= this.operationLength) {
            this.progress = 0;
         }
      }

      this.guiProgress = this.operationLength <= 0 ? 0.0F : (float)this.progress / this.operationLength;
      com.industrialreforged.block.ActiveBlock.setActive(this, active);
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }

   private void searchFluidSource(Level level, BlockPos blockPos) {
      Direction direction = RotateBlock.getHorizontalFacing(this);
      Direction left = direction.getClockWise();
      Direction right = direction.getCounterClockWise();
      blockPos = blockPos.above(this.searchOffsetY);
      HashSet<BlockPos> checks = new HashSet<>();
      ArrayDeque<BlockPos> search = new ArrayDeque<>();
      search.add(blockPos);

      while (!search.isEmpty()) {
         BlockPos pos = search.poll();
         if (!(blockPos.distSqr(pos) > 9.0) && !checks.contains(pos)) {
            checks.add(pos);
            if (level.isLoaded(pos)) {
               if (this.searchOffsetY < 16 && this.isSource(level, pos.above())) {
                  this.queue.clear();
                  this.searchOffsetY++;
               }

               if (this.isSource(level, pos)) {
                  this.queue.add(pos);
               }

               search.add(pos.relative(left));
               search.add(pos.relative(right));
               search.add(pos.relative(direction));
            }
         }
      }
   }

   private boolean isSource(Level level, BlockPos blockPos) {
      return level.getFluidState(blockPos).isSource();
   }
}
