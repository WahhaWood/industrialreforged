package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotProcessableSmelting;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.menu.machine.InductionFurnaceMenu;
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
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class InductionFurnaceBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IClientTick, IContainerWrapper {
   public final InvSlotProcessableSmelting<?> inputSlot = new InvSlotProcessableSmelting<>(this, "input", 2);
   public final InvSlotOutput outputSlot = new InvSlotOutput(this, 2);
   public final InvSlotUpgrade upgradeSlot;
   protected Object soundInstance;
   private SoundUtil.SoundState soundState;
   public short heat = 0;
   public short progress = 0;
   private CombinedInvWrapper wrapper;

   public InductionFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(40000, 512, IRBlockEntities.INDUCTION_FURNACE.get(), blockPos, blockState);
      this.enableRedstoneSignal();
      this.upgradeSlot = new InvSlotUpgrade(this, 2);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.heat = tag.getShort("heat");
      this.progress = tag.getShort("progress");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putShort("heat", this.heat);
      tag.putShort("progress", this.progress);
   }

   public void onChunkUnloaded() {
      super.onChunkUnloaded();
      if (IR.sideProxy.isRendering() && this.soundInstance != null) {
         SoundUtil.stop(this.soundInstance);
      }
   }

   public void clientTick() {
      SoundUtil.SoundState currentState = this.isActive() ? SoundUtil.SoundState.RUNNING : SoundUtil.SoundState.IDLE;
      if (currentState != this.soundState) {
         this.soundState = currentState;
         switch (this.soundState) {
            case RUNNING:
               this.soundInstance = SoundUtil.playLoop(this, this.getStartingSoundFile(), this.soundInstance);
               break;
            case IDLE:
               this.soundInstance = SoundUtil.play(this, this.getInterruptSoundFile(), this.soundInstance);
         }
      }
   }

   /** Нагревается от энергии и плавит предметы, расходуя тепло. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      int heatBefore = this.heat;
      int progressBefore = this.progress;
      int storedBefore = this.energy.getStored();
      boolean canWork = this.canOperate();
      boolean active = false;
      if (this.energy.getStored() >= 512 && (canWork || this.heat < 10000)) {
         this.energy.extractForceEnergy(512);
         if (this.heat < 10000) {
            this.heat = (short)Math.min(10000, this.heat + 25);
            active = true;
         }
      }

      if (canWork && this.heat > 0) {
         this.heat = (short)Math.max(0, this.heat - 1);
         this.progress = (short)(this.progress + 40);
         active = true;
         if (this.progress >= 4000) {
            this.progress = 0;
            if (this.inputSlot.process(0) != null) {
               this.inputSlot.assemble(0);
            } else {
               this.inputSlot.assemble(1);
            }
         }
      } else if (!canWork) {
         this.progress = 0;
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, active);
      // В простое тепло/прогресс/энергия стоят — чанк не пачкаем.
      if (this.heat != heatBefore || this.progress != progressBefore || this.energy.getStored() != storedBefore) {
         this.markForSave();
      }
   }

   public String getHeat() {
      return this.heat * 100 / 10000 + "%";
   }

   public float getProgress() {
      return this.progress / 4000.0F;
   }

   public boolean canOperate() {
      return this.inputSlot.process(0) != null || this.inputSlot.process(1) != null;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new InductionFurnaceMenu(containerId, playerInventory, this);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(UpgradableProperty.REDSTONE_SENSITIVE, UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.ITEM_PRODUCING);
   }

   public SoundEvent getStartingSoundFile() {
      return IRSounds.SOUND_INDUCTION_START.get();
   }

   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_INDUCTION_LOOP.get();
   }

   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_INDUCTION_STOP.get();
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.outputSlot.createWrapper()});
         }
         return this.wrapper;
      }

      return switch (direction) {
         case UP -> this.inputSlot.createWrapper();
         case DOWN -> this.outputSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> {
            if (this.wrapper == null) {
               this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.outputSlot.createWrapper()});
            }
            yield this.wrapper;
         }
      };
   }
}
