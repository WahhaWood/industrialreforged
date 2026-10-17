package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.tile.FluidHandler;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.menu.machine.CondenserMenu;
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
import org.jetbrains.annotations.Nullable;

public class CondenserBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IFluidWrapper {
   public int progress = 0;
   private final FluidTank inputTank = new FluidTank(
      100000, true, false, stack -> stack.is(IRFluids.STEAM.getFluidStill())
   );
   private final FluidTank outputTank = new FluidTank(100000, false, true);
   public final InvSlotConsumableLiquidByTank waterInputSlot = new InvSlotConsumableLiquidByTank(
      this, "waterInputSlot", 1, InvSlotConsumableLiquid.OpType.Fill, this.outputTank
   );
   public final InvSlotOutput waterOutputSlot = new InvSlotOutput(this, "waterOutputSlot", 1);
   public final InvSlotConsumableItem ventSlots = new InvSlotConsumableItem(this, "ventSlots", 4, IRItems.HEAT_VENT);
   public final InvSlotUpgrade upgradeSlot;
   private FluidHandler fluidHandler;

   public CondenserBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(10000, 512, IRBlockEntities.CONDENSER.get(), blockPos, blockState);
      this.ventSlots.setMaxStackSize(1);
      this.upgradeSlot = new InvSlotUpgrade(this, 1);
      this.upgradeSlot.addListener(container -> this.updateTier());
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
   protected boolean hasWork() {
      return this.progress > 0;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputTank.load(registries, tag, "inputTank");
      this.outputTank.load(registries, tag, "outputTank");
      this.waterInputSlot.load(tag, registries);
      this.waterOutputSlot.load(tag, registries);
      this.ventSlots.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.progress = tag.getInt("progress");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputTank.save(registries, tag, "inputTank");
      this.outputTank.save(registries, tag, "outputTank");
      this.waterInputSlot.save(tag, registries);
      this.waterOutputSlot.save(tag, registries);
      this.ventSlots.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putInt("progress", this.progress);
   }

   public void onLoad() {
      super.onLoad();
      if (!this.getLevel().isClientSide) {
         this.upgradeSlot.setChanged();
      }
   }

   public byte getVents() {
      byte by = 0;

      for (int i = 0; i < this.ventSlots.getContainerSize(); i++) {
         if (!this.ventSlots.isEmpty(i)) {
            by++;
         }
      }

      return by;
   }

   private void updateTier() {
      this.energy.setReceive(Integer.MAX_VALUE);
   }

   /**
    * Конденсирует пар в воду: каждый вентиль даёт 500 мБ за тик (4 вентиля = 2000 мБ/тик, под усиленный котёл-резервуар).
    * Укрепление — множитель выхода: скорость и есть пакет (отдельной цены в энергии у конденсатора нет,
    * предел задаёт подача пара). Читаем пакет вживую из upgradeSlot — listener не нужен.
    */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      int progressBefore = this.progress;
      int inBefore = this.inputTank.getFluidAmount();
      int outBefore = this.outputTank.getFluidAmount();
      int vents = this.getVents();
      boolean active = false;
      int room = this.outputTank.getCapacity() - this.outputTank.getFluidAmount();
      if (vents > 0 && this.inputTank.getFluidAmount() > 0 && room > 0) {
         int amount = Math.min(vents * 500 * Math.max(1, this.upgradeSlot.getOperationsPerTick()), Math.min(this.inputTank.getFluidAmount(), room));
         if (amount > 0) {
            this.inputTank.drainForce(amount, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
            this.outputTank.fillForce(
               new net.neoforged.neoforge.fluids.FluidStack(net.minecraft.world.level.material.Fluids.WATER, amount),
               net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE
            );
            this.progress = Math.min(10000, this.progress + amount);
            active = true;
         }
      } else if (this.progress > 0) {
         this.progress = Math.max(0, this.progress - 1);
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, active);
      // Конденсация, спад прогресса — по делу; пустой простой — не пачкаем.
      if (this.progress != progressBefore || this.inputTank.getFluidAmount() != inBefore
         || this.outputTank.getFluidAmount() != outBefore) {
         this.markForSave();
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new CondenserMenu(containerId, playerInventory, this);
   }

   public FluidTank getInputTank() {
      return this.inputTank;
   }

   public FluidTank getOutputTank() {
      return this.outputTank;
   }

   @Override
   public int getEnergyStored() {
      return 0;
   }

   @Override
   public boolean useEnergy(int d) {
      return false;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.FLUID_CONSUMING,
         UpgradableProperty.FLUID_PRODUCING,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.STRENGTHENING
      );
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.waterInputSlot, this.waterOutputSlot, this.outputTank);
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
}
