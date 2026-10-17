package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.tech.api.tile.IHeatSource;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableStandard;
import com.industrialreforged.tech.core.recipe.CentrifugeRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.CentrifugeMenu;
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
import org.jetbrains.annotations.Nullable;

public class CentrifugeBlockEntity extends AbstractStandardMachineBlockEntity<CentrifugeRecipe> {
   /** Base FE/t and ticks per operation; the ME version prices an operation from these. */
   public static final int ENERGY_CONSUME = 96;
   public static final int OPERATION_LENGTH = 500;
   public static final short maxHeat = 5000;
   public short heat = 0;
   public short workheat = 5000;

   public CentrifugeBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ENERGY_CONSUME, OPERATION_LENGTH, 3, IRBlockEntities.CENTRIFUGE.get(), blockPos, blockState);
      this.enableRedstoneSignal();
   }

   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACHINE_massfabloop.get();
   }

   @Override
   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_MACHINE_INTERRUPT.get();
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableStandard<>(this, "input", 1, IRRecipeTypes.CENTRIFUGE.get());
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.heat = tag.getShort("heat");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putShort("heat", this.heat);
   }

   public float getHeatRatio() {
      return (float)this.heat / this.workheat;
   }

   @Override
   public void onLoad() {
      super.onLoad();
      if (this.level != null && !this.level.isClientSide) {
         this.updateRedstoneSignal(this.getBlockState(), this.worldPosition);
      }
   }

   public boolean isRedstonePowered() {
      if (this.level != null) {
         this.redstoneSignal = this.level.hasNeighborSignal(this.worldPosition);
      }
      return this.upgradeSlot.invertRedstonePower != this.redstoneSignal;
   }

   /**
    * Centrifuge heating logic:
    * 1. Pulls heat from adjacent IHeatSource (e.g. solid/electric heat generator) if present.
    * 2. Self-heats electrically:
    *    - If redstone signal (lever) is ON: pre-heats up to maxHeat (5000) using energyConsume,
    *      then maintains maxHeat with a small idle cost (2 FE/t).
    *    - If input holds a recipe needing heat and heat < recipe.getMinHeat(): heats up using energyConsume.
    *    - While actively running a recipe: passively increases heat by +1/t up to maxHeat.
    * 3. Cools down gradually (-1/t) when idle and redstone is OFF.
    */
   private void heatup() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      short beforeHeat = this.heat;
      boolean redstone = this.isRedstonePowered();
      boolean working = false;
      for (int p : this.progress) {
         if (p > 0) {
            working = true;
            break;
         }
      }

      CentrifugeRecipe recipe = !this.inputSlot.isEmpty() ? this.getRecipe(0) : null;
      boolean needsHeatForRecipe = recipe != null && this.heat < recipe.getMinHeat();
      boolean shouldHeat = redstone || working || needsHeatForRecipe;
      int gained = 0;

      // 1. External heat source (IHeatSource) if present
      if (shouldHeat && this.heat < maxHeat) {
         int requested = maxHeat - this.heat;
         for (Direction direction : Direction.values()) {
            var neighbor = this.level.getBlockEntity(this.worldPosition.relative(direction));
            if (neighbor instanceof IHeatSource source) {
               gained = source.extractHeat(direction.getOpposite(), requested, false);
               if (gained > 0) {
                  this.heat = (short)Math.min(this.heat + gained, maxHeat);
                  break;
               }
            }
         }
      }

      // 2. Electric internal heating
      if (shouldHeat && this.heat < maxHeat) {
         if (working) {
            // Processing recipe already drains energyConsume in processSlot; warm up passively
            this.heat = (short)Math.min(this.heat + 1, maxHeat);
         } else if (gained == 0) {
            // Pre-heating with lever or heating up to reach recipe's minHeat:
            int cost = Math.max(1, this.energyConsume);
            if (this.energy.getStored() >= cost) {
               this.energy.extractForceEnergy(cost);
               // Heats up by 2 per tick (+40 heat/sec)
               this.heat = (short)Math.min(this.heat + 2, maxHeat);
               ActiveBlock.setActive(this, true);
            }
         }
      } else if (redstone && this.heat >= maxHeat) {
         // Maintaining max heat with redstone signal: small idle cost (2 FE/t)
         if (this.energy.getStored() >= 2) {
            this.energy.extractForceEnergy(2);
            ActiveBlock.setActive(this, true);
         } else {
            // Ran out of power: cools down
            this.heat = (short)Math.max(this.heat - 1, 0);
         }
      } else if (!shouldHeat && this.heat > 0) {
         // Idle without redstone: cools down 1 heat per tick
         this.heat = (short)Math.max(this.heat - 1, 0);
      }

      if (this.heat != beforeHeat) {
         this.markForSave();
      }
   }

   @Override
   public void serverTick() {
      super.serverTick();
      this.heatup();
   }

   public boolean canProcess(CentrifugeRecipe recipe) {
      return this.heat >= recipe.getMinHeat() && super.canProcess(recipe);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new CentrifugeMenu(containerId, playerInventory, this);
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
         UpgradableProperty.STRENGTHENING,
         UpgradableProperty.SOUNDPROOFING
      );
   }
}
