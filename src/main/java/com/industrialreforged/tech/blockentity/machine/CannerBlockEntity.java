package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.tile.FluidHandler;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableCanner;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableCanner;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.CannerRecipe;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.menu.machine.CannerMenu;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class CannerBlockEntity extends AbstractStandardMachineBlockEntity<CannerRecipe> implements IFluidWrapper {
   private CannerBlockEntity.Mode mode = CannerBlockEntity.Mode.BottleSolid;
   public static final int eventSwapTanks = CannerBlockEntity.Mode.values.length + 1;
   public final FluidTank inputTank;
   public final FluidTank outputTank;
   public final InvSlotConsumableCanner canInputSlot = new InvSlotConsumableCanner(this, "canInput", 1);
   private FluidHandler fluidHandler;
   private IItemHandlerModifiable containerWrapper;

   public CannerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(4, 200, 1, IRBlockEntities.CANNER.get(), blockPos, blockState);
      this.inputTank = new FluidTank(8000, true, false);
      this.outputTank = new FluidTank(8000, false, true);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableCanner(this, "input", 1);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_EXTRACTOR_START.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputTank.load(registries, tag, "inputTank");
      this.outputTank.load(registries, tag, "outputTank");
      this.canInputSlot.load(tag, registries);
      this.setMode(CannerBlockEntity.Mode.values[tag.getInt("mode")]);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputTank.save(registries, tag, "inputTank");
      this.outputTank.save(registries, tag, "outputTank");
      this.canInputSlot.save(tag, registries);
      tag.putInt("mode", this.mode.ordinal());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new CannerMenu(containerId, playerInventory, this);
   }

   public void onNetworkEvent(Player player, int n) {
      if (n >= 0 && n < CannerBlockEntity.Mode.values.length) {
         this.setMode(CannerBlockEntity.Mode.values[n]);
      } else if (n == eventSwapTanks) {
         this.switchTanks();
      }
   }

   public CannerBlockEntity.Mode getMode() {
      return this.mode;
   }

   public void setMode(CannerBlockEntity.Mode mode) {
      boolean changed = this.mode != mode;
      this.mode = mode;
      switch (mode) {
         case BottleSolid:
            this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.None);
            break;
         case EmptyLiquid:
            this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Drain);
            break;
         case BottleLiquid:
            this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Fill);
            break;
         case EnrichLiquid:
            this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Both);
      }

      if (IR.sideProxy.isRendering() && this.soundInstance != null) {
         SoundUtil.stop(this.soundInstance);
      }

      if (changed && this.level != null && !this.level.isClientSide) {
         this.markForSave();
      }
   }

   private boolean switchTanks() {
      if (this.progress[0] != 0) {
         return false;
      }

      FluidStack fluidStack = this.inputTank.getFluid();
      this.inputTank.setFluid(this.outputTank.getFluid());
      this.outputTank.setFluid(fluidStack);
      return true;
   }

   /**
    * Пакет укрепления: в твёрдом режиме за операцию набивается сразу несколько
    * банок, в жидкостных режимах операция всегда одна (там контейнеры).
    */
   @Override
   protected int getBatchSize(int index, CannerRecipe recipe) {
      if (this.getMode() != Mode.BottleSolid) {
         return 1;
      }

      ItemStack base = this.canInputSlot.getItem();
      ItemStack center = this.inputSlot.getItem();
      SolidCannerRecipe solid = SolidCannerRecipe.gets()
         .stream()
         .filter(r -> r.matches(base, center))
         .findFirst()
         .orElse(null);
      if (solid == null) {
         return 1;
      }

      int wanted = Math.max(1, this.operationsPerTick);
      int byInput = base.getCount() / Math.max(1, solid.inputCount);
      int byCenter = center.getCount() / Math.max(1, solid.centerCount);
      int byOutput = this.maxOutputCrafts(List.of(solid.result), wanted);
      return Math.max(1, Math.min(wanted, Math.min(byInput, Math.min(byCenter, byOutput))));
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
         UpgradableProperty.STRENGTHENING,
         UpgradableProperty.SOUNDPROOFING
      );
   }

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
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.containerWrapper == null) {
            this.containerWrapper = new CombinedInvWrapper(
               new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.canInputSlot.createWrapper(), this.outputSlot.createWrapper()}
            );
         }
         return this.containerWrapper;
      }

      return switch (direction) {
         case UP -> new com.industrialreforged.tech.container.InsertOnlyHandler(this.inputSlot.createWrapper());
         case DOWN -> this.outputSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> {
            if (this.containerWrapper == null) {
               this.containerWrapper = new CombinedInvWrapper(
                  new IItemHandlerModifiable[]{this.canInputSlot.createWrapper(), this.inputSlot.createWrapper(), this.outputSlot.createWrapper()}
               );
            }
            yield this.containerWrapper;
         }
      };
   }

   @Override
   public int get(int index) {
      return index == 3 ? this.mode.ordinal() : super.get(index);
   }

   @Override
   public void set(int index, int value) {
      if (index == 3) {
         this.setMode(CannerBlockEntity.Mode.values()[value]);
      } else {
         super.set(index, value);
      }
   }

   @Override
   public int getCount() {
      return 4;
   }

   public enum Mode {
      BottleSolid,
      EmptyLiquid,
      BottleLiquid,
      EnrichLiquid;

      public static final CannerBlockEntity.Mode[] values = values();
   }
}
