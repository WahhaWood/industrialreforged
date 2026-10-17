package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.menu.machine.CropmatronMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class CropmatronBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IFluidWrapper, IContainerWrapper {
   public int scanX = -4;
   public int scanY = -1;
   public int scanZ = -4;
   public final InvSlotConsumable fertilizerSlot;
   public final InvSlotOutput wasseroutputSlot;
   public final InvSlotConsumableLiquidByTank wasserinputSlot;
   public final InvSlotConsumableItem exInputSlot;
   public final FluidTank waterTank = new FluidTank(2000, true, false, stack -> stack.is(Fluids.WATER));
   private CombinedInvWrapper wrapper;

   public CropmatronBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(40000, 128, IRBlockEntities.CROPMATRON.get(), blockPos, blockState);
      this.fertilizerSlot = new InvSlotConsumableItem(this, "fertilizer", 7, IRItems.FERTILIZER);
      this.wasserinputSlot = new InvSlotConsumableLiquidByTank(this, "wasserinputSlot", 1, InvSlotConsumableLiquid.OpType.Drain, this.waterTank);
      this.exInputSlot = new InvSlotConsumableItem(this, "exInputSlot", 7, IRItems.WEED_EX_CELL);
      this.wasseroutputSlot = new InvSlotOutput(this, "wasseroutputSlot", 1);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACHINE_pumpop.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.fertilizerSlot.load(tag, registries);
      this.wasseroutputSlot.load(tag, registries);
      this.wasserinputSlot.load(tag, registries);
      this.exInputSlot.load(tag, registries);
      this.waterTank.load(registries, tag, "water");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.fertilizerSlot.save(tag, registries);
      this.wasseroutputSlot.save(tag, registries);
      this.wasserinputSlot.save(tag, registries);
      this.exInputSlot.save(tag, registries);
      this.waterTank.save(registries, tag, "water");
   }

   /**
    * A single scan step costs 1 FE plus up to 3 × 10 FE for the work it actually does, while
    * the caller only checked for "some energy left" — the buffer used to end the tick
    * negative.  Every charge is now checked up front.
    */
   private boolean pay(int cost) {
      if (this.energy.getStored() < cost) {
         return false;
      }

      this.energy.extractForceEnergy(cost);
      return true;
   }

   public void scan() {
      this.scanX++;
      if (this.scanX > 4) {
         this.scanX = -4;
         this.scanZ++;
         if (this.scanZ > 4) {
            this.scanZ = -4;
            this.scanY++;
            if (this.scanY > 1) {
               this.scanY = -1;
            }
         }
      }

      if (!this.pay(1)) {
         return;
      }

      BlockPos blockPos = this.worldPosition.offset(this.scanX, this.scanY, this.scanZ);
      if (this.getLevel().getBlockEntity(blockPos) instanceof CropBlockEntity crop) {
         if (!this.fertilizerSlot.isEmpty() && this.fertilizerSlot.consume(1, true, false) != null && crop.applyFertilizer(false) && this.pay(10)) {
            this.fertilizerSlot.consume(1);
         }

         int n;
         if (!this.waterTank.isEmpty() && (n = crop.applyHydration(this.waterTank.getFluidAmount(), false)) > 0 && this.pay(10)) {
            this.waterTank.drainForce(n, FluidAction.EXECUTE);
         }

         if (!this.exInputSlot.isEmpty()) {
            for (ItemStack stack : this.exInputSlot.getItems()) {
               if (!stack.isEmpty()) {
                  int damage = stack.getDamageValue() + 1;
                  int maxDamage = stack.getMaxDamage();
                  int apply = crop.applyWeedEx(Math.min(50, maxDamage - damage), true, false);
                  if (apply > 0 && this.pay(10)) {
                     if (stack.isDamageableItem()) {
                        stack.hurtAndBreak(apply, (ServerLevel)this.level, null, item -> stack.setDamageValue(0));
                        if (stack.getDamageValue() >= 999) {
                           stack.setCount(0);
                        }
                     }

                     break;
                  }
               }
            }
         }
      } else if (!this.waterTank.isEmpty() && this.tryHydrateFarmland(blockPos)) {
         this.pay(10);
      }
   }

   /** Каждый тик обрабатывает один блок вокруг: удобряет, поливает, травит сорняки. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      boolean active = false;
      if (this.energy.getStored() > 0) {
         this.scan();
         active = true;
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, active);
      // active означает потраченную энергию (scan платит 1 FE за тик); без энергии — не пачкаем.
      if (active) {
         this.markForSave();
      }
   }

   private boolean tryHydrateFarmland(BlockPos blockPos) {
      Level level = this.getLevel();
      BlockState blockState = level.getBlockState(blockPos);
      int n;
      if (blockState.getBlock() == Blocks.FARMLAND && (n = (Integer)blockState.getValue(FarmBlock.MOISTURE)) < 7) {
         int n2 = Math.min(this.waterTank.getFluidAmount(), 7 - n);
         assert n2 > 0;
         assert n2 <= 7;
         this.waterTank.drainForce(n2, FluidAction.EXECUTE);
         level.setBlock(blockPos, (BlockState)blockState.setValue(FarmBlock.MOISTURE, n + n2), 2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.TRANSFORMER, UpgradableProperty.ENERGY_STORAGE, UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.FLUID_CONSUMING
      );
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new CropmatronMenu(containerId, playerInventory, this);
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.wasserinputSlot, this.wasseroutputSlot, this.waterTank);
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.waterTank;
   }

   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.wrapper == null) {
         this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.fertilizerSlot.createWrapper(), this.exInputSlot.createWrapper()});
      }

      return this.wrapper;
   }
}
