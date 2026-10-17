package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.recipe.IPatternStorage;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByList;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.manager.UuRecipeManager;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.menu.machine.ReplicatorMenu;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class ReplicatorBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IFluidWrapper, IContainerWrapper, ContainerData {
   private int uuPerTick = 10;
   private int energyConsume = 512;
   private int extraUuStored = 0;
   private int refreshCooldown = 20;
   public int uuProcessed = 0;
   public ItemStack pattern = ItemStack.EMPTY;
   private ReplicatorBlockEntity.Mode mode = ReplicatorBlockEntity.Mode.STOPPED;
   public int index;
   public int maxIndex;
   public int patternUu;
   public final InvSlotConsumableLiquid fluidSlot = new InvSlotConsumableLiquidByList(this, "fluid", 1, IRFluids.UU_MATTER.getFluidStill());
   public final InvSlotOutput cellSlot = new InvSlotOutput(this, "cell", 1);
   public final InvSlotOutput outputSlot = new InvSlotOutput(this, 1);
   public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, 4);
   public final FluidTank tank;
   private IItemHandlerModifiable wrapper;

   public ReplicatorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2000000, 16000, IRBlockEntities.REPLICATOR.get(), blockPos, blockState);
      this.upgradeSlot.addListener(container -> this.setOverclockRates());
      this.tank = new FluidTank(16000, true, false, stack -> stack.is(IRFluids.UU_MATTER.getFluidStill()));
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACHINE_massfabloop.get();
   }

   @Override
   protected boolean isSoundproofed() {
      return this.upgradeSlot.soundproofing;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.fluidSlot.load(tag, registries);
      this.cellSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.extraUuStored = tag.getInt("extraUuStored");
      this.uuProcessed = tag.getInt("uuProcessed");
      this.index = tag.getInt("index");
      int n = tag.getInt("mode");
      this.mode = n < ReplicatorBlockEntity.Mode.values().length ? ReplicatorBlockEntity.Mode.values()[n] : ReplicatorBlockEntity.Mode.STOPPED;
      this.pattern = ItemStack.parseOptional(registries, tag.getCompound("pattern"));
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.fluidSlot.save(tag, registries);
      this.cellSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      this.tank.save(registries, tag, "tank");
      tag.putDouble("extraUuStored", this.extraUuStored);
      tag.putDouble("uuProcessed", this.uuProcessed);
      tag.putInt("index", this.index);
      tag.putInt("mode", this.mode.ordinal());
      if (!this.pattern.isEmpty()) {
         tag.put("pattern", this.pattern.save(registries));
      }
   }

   /** Тратит материю и энергию, пока не наберёт стоимость шаблона, затем выдаёт предмет. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      // Output only: the replicator has no input slot to pull into.
      com.industrialreforged.tech.core.util.ItemMoveHelper.tickOutputOnly(
         this.level, this.worldPosition, this.upgradeSlot, this.outputSlot
      );
      if (--this.refreshCooldown <= 0) {
         this.refreshCooldown = 20;
         this.refreshInfo();
      }

      boolean active = false;
      // «Укрепление» дороже по энергии: +50% за каждое улучшение.  The guard has to check
      // the same number the tick actually spends, otherwise the buffer ends up negative.
      int tickCost = (int)Math.round(this.energyConsume * this.upgradeSlot.getStrengtheningEnergyMultiplier());
      if (this.mode != Mode.STOPPED
         && !this.pattern.isEmpty()
         && this.patternUu > 0
         && this.energy.getStored() >= tickCost
         && this.outputSlot.canAdd(this.getBatchOutput())) {
         // «Укрепление»: операция копит стоимость сразу за пакет и выдаёт 4 предмета
         int cost = this.patternUu * this.getBatchSize();
         int remaining = cost - this.uuProcessed;
         if (remaining <= 0) {
            remaining = cost;
            this.uuProcessed = 0;
         }

         boolean finish = remaining <= this.uuPerTick;
         int step = finish ? remaining : this.uuPerTick;
         if (this.consumeUu(step)) {
            this.energy.extractForceEnergy(tickCost);
            this.uuProcessed += step;
            active = true;
            if (this.uuProcessed >= cost) {
               this.uuProcessed = 0;
               this.outputSlot.add(this.getBatchOutput());
               if (this.mode == Mode.SINGLE) {
                  this.mode = Mode.STOPPED;
               } else {
                  this.refreshInfo();
               }
            }
         }
      }

      this.setActive(active);
   }

   /** Размер пакета «Укрепления»: 4 предмета за улучшение, не больше стака. */
   public int getBatchSize() {
      return Math.min(Math.max(1, this.upgradeSlot.getOperationsPerTick()), this.pattern.getMaxStackSize());
   }

   private ItemStack getBatchOutput() {
      return this.pattern.copyWithCount(this.getBatchSize());
   }

   private boolean consumeUu(int amount) {
      if (amount <= this.extraUuStored) {
         this.extraUuStored -= amount;
         return true;
      }

      amount -= this.extraUuStored;
      int toDrain = (int)Math.max(1.0, Math.ceil(amount / 100.0));
      FluidStack drained = this.tank.drainForce(toDrain, FluidAction.SIMULATE);
      if (drained != null && drained.getFluid() == IRFluids.UU_MATTER.getFluidStill() && drained.getAmount() == toDrain) {
         this.tank.drainForce(toDrain, FluidAction.EXECUTE);
         amount -= drained.getAmount() * 100;
         if (amount < 0) {
            this.extraUuStored = -amount;
         } else {
            this.extraUuStored = 0;
         }

         return true;
      } else {
         return false;
      }
   }

   public void refreshInfo() {
      IPatternStorage iPatternStorage = this.getPatternStorage();
      ItemStack itemStack = this.pattern;
      if (iPatternStorage == null) {
         this.pattern = ItemStack.EMPTY;
      } else {
         List<ItemStack> list = iPatternStorage.getPatterns();
         if (this.index < 0 || this.index >= list.size()) {
            this.index = 0;
         }

         this.maxIndex = list.size();
         if (list.isEmpty()) {
            this.pattern = ItemStack.EMPTY;
         } else {
            this.pattern = list.get(this.index);
            this.patternUu = UuRecipeManager.instance.getCost(this.level, this.pattern);
            if (!StackUtil.checkItemEqualityStrict(this.pattern, itemStack)) {
               this.uuProcessed = 0;
               this.mode = ReplicatorBlockEntity.Mode.STOPPED;
            }
         }
      }

      if (this.pattern.isEmpty()) {
         this.uuProcessed = 0;
         this.mode = ReplicatorBlockEntity.Mode.STOPPED;
      }
   }

   public IPatternStorage getPatternStorage() {
      Level level = this.getLevel();

      for (Direction direction : Util.ALL_DIRS) {
         BlockEntity blockEntity = level.getBlockEntity(this.worldPosition.relative(direction));
         if (blockEntity instanceof IPatternStorage) {
            return (IPatternStorage)blockEntity;
         }
      }

      return null;
   }

   public void setOverclockRates() {
      this.uuPerTick = (int)Math.max(10.0, 10.0 / Math.min(1.0, this.upgradeSlot.processTimeMultiplier));
      this.energyConsume = (int)(512.0 * this.upgradeSlot.energyDemandMultiplier);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new ReplicatorMenu(containerId, playerInventory, this);
   }

   public void onLoad() {
      super.onLoad();
      if (IR.sideProxy.isSimulating()) {
         this.upgradeSlot.setChanged();
         this.setOverclockRates();
         this.refreshInfo();
      }
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.fluidSlot, this.cellSlot, this.tank);
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }

   @Nullable
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.wrapper == null) {
         this.wrapper = this.outputSlot.createWrapper();
      }

      return this.wrapper;
   }

   public void onNetworkEvent(int buttonId) {
      switch (buttonId) {
         case 0:
         case 1:
            List<ItemStack> list;
            IPatternStorage iPatternStorage;
            if (this.mode == ReplicatorBlockEntity.Mode.STOPPED
               && (iPatternStorage = this.getPatternStorage()) != null
               && !(list = iPatternStorage.getPatterns()).isEmpty()) {
               this.index = buttonId == 0 ? (this.index <= 0 ? list.size() - 1 : --this.index) : (this.index >= list.size() - 1 ? 0 : ++this.index);
               this.refreshInfo();
            }
         case 2:
         default:
            break;
         case 3:
            if (this.mode != ReplicatorBlockEntity.Mode.STOPPED) {
               this.uuProcessed = 0;
               this.mode = ReplicatorBlockEntity.Mode.STOPPED;
            }
            break;
         case 4:
            if (!this.pattern.isEmpty()) {
               this.mode = ReplicatorBlockEntity.Mode.SINGLE;
            }
            break;
         case 5:
            if (!this.pattern.isEmpty()) {
               this.mode = ReplicatorBlockEntity.Mode.CONTINUOUS;
            }
      }
   }

   public ReplicatorBlockEntity.Mode getMode() {
      return this.mode;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING,
         UpgradableProperty.REDSTONE_SENSITIVE,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.FLUID_CONSUMING,
         UpgradableProperty.STRENGTHENING,
         UpgradableProperty.SOUNDPROOFING
      );
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> this.uuProcessed;
         case 1 -> this.mode.ordinal();
         case 2 -> this.index;
         case 3 -> this.maxIndex;
         case 4 -> this.patternUu;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.uuProcessed = value;
            break;
         case 1:
            this.mode = ReplicatorBlockEntity.Mode.values()[value];
            break;
         case 2:
            this.index = value;
            break;
         case 3:
            this.maxIndex = value;
            break;
         case 4:
            this.patternUu = value;
      }
   }

   public int getCount() {
      return 5;
   }

   public enum Mode {
      STOPPED,
      SINGLE,
      CONTINUOUS;
   }
}
