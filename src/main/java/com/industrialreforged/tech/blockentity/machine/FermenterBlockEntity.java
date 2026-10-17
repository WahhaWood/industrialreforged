package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.tile.FluidHandler;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByList;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.menu.machine.FermenterMenu;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class FermenterBlockEntity extends AbstractMachineBlockEntity implements IUpgradableBlock, IFluidWrapper, IContainerWrapper, ContainerData {
   private final FluidTank inputTank;
   private final FluidTank outputTank;
   public final InvSlotConsumableLiquidByList fluidInputCellInSlot;
   public final InvSlotConsumableLiquidByTank fluidOutputCellInSlot;
   public final InvSlotOutput fluidInputCellOutSlot;
   public final InvSlotOutput fluidOutputCellOutSlot;
   public final InvSlotOutput fertiliserSlot;
   public final InvSlotUpgrade upgradeSlot;
   private int heatBuffer = 0;
   public int progress = 0;
   private FluidHandler fluidHandler;
   private IItemHandlerModifiable wrapper;

   public FermenterBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.FERMENTER.get(), blockPos, blockState);
      this.outputTank = new FluidTank(2000, false, true);
      this.inputTank = new FluidTank(10000, true, false, fluid -> fluid.is(IRFluids.BIOMASS.getFluidStill()));
      this.fluidInputCellInSlot = new InvSlotConsumableLiquidByList(
         this, "biomassInput", 1, InvSlotConsumableLiquid.OpType.Drain, IRFluids.BIOMASS.getFluidStill()
      );
      this.fluidOutputCellInSlot = new InvSlotConsumableLiquidByTank(this, "biogasInput", 1, InvSlotConsumableLiquid.OpType.Fill, this.outputTank);
      this.fluidInputCellOutSlot = new InvSlotOutput(this, "biomassOutput", 1);
      this.fluidOutputCellOutSlot = new InvSlotOutput(this, "biogassOutput", 1);
      this.fertiliserSlot = new InvSlotOutput(this, 1);
      this.upgradeSlot = new InvSlotUpgrade(this, 2);
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
      this.fluidInputCellInSlot.load(tag, registries);
      this.fluidOutputCellInSlot.load(tag, registries);
      this.fluidInputCellOutSlot.load(tag, registries);
      this.fluidOutputCellOutSlot.load(tag, registries);
      this.fertiliserSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.progress = tag.getInt("progress");
      this.heatBuffer = tag.getInt("heatBuffer");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputTank.save(registries, tag, "inputTank");
      this.outputTank.save(registries, tag, "outputTank");
      this.fluidInputCellInSlot.save(tag, registries);
      this.fluidOutputCellInSlot.save(tag, registries);
      this.fluidInputCellOutSlot.save(tag, registries);
      this.fluidOutputCellOutSlot.save(tag, registries);
      this.fertiliserSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putInt("progress", this.progress);
      tag.putInt("heatBuffer", this.heatBuffer);
   }

   /** Сбраживает биомассу в биогаз, подогреваясь от соседних источников тепла. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      int heatBefore = this.heatBuffer;
      int progressBefore = this.progress;
      int inBefore = this.inputTank.getFluidAmount();
      int outBefore = this.outputTank.getFluidAmount();
      if (this.heatBuffer < 4000) {
         this.heatBuffer += this.requestHeat(Math.min(20, 4000 - this.heatBuffer));
      }

      boolean canWork = this.heatBuffer > 0 && this.inputTank.getFluidAmount() > 0 && this.outputTank.getCapacity() - this.outputTank.getFluidAmount() > 0;
      boolean active = false;
      // Укрепление — пакет: прогрев идёт в batch раз быстрее и завершение
      // перерабатывает batch мБиомассы за раз. Топливо здесь — тепло, и оно же
      // цена: расход тепла растёт вместе со скоростью, так что предел задаёт
      // подача тепла (запрос ограничен 20/тик). Отдельной наценки ×1.5 нет —
      // ей не к чему применяться: энергии ферментер не тратит.
      int batch = Math.max(1, this.upgradeSlot.getOperationsPerTick());
      if (canWork) {
         active = true;
         this.progress += batch;
         this.heatBuffer = Math.max(0, this.heatBuffer - 8 * batch);
         if (this.progress >= 500) {
            this.progress = 0;
            int convert = Math.min(batch, Math.min(this.inputTank.getFluidAmount(), this.outputTank.getCapacity() - this.outputTank.getFluidAmount()));
            if (convert > 0) {
               this.inputTank.drainForce(convert, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
               this.outputTank.fillForce(
                  new net.neoforged.neoforge.fluids.FluidStack(IRFluids.BIOGAS.getFluidStill(), convert),
                  net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE
               );
               for (int i = 0; i < convert; i++) {
                  if (this.level.random.nextInt(20) == 0) {
                     this.fertiliserSlot.add(new net.minecraft.world.item.ItemStack(com.industrialreforged.tech.core.ref.IRItems.FERTILIZER.get()));
                  }
               }
            }
         }
      } else if (this.progress > 0) {
         this.progress--;
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, active);
      // Нагрев, брожение, спад прогресса — по делу; пустой простой (буфер полон,
      // прогресса нет, баки стоят) — не пачкаем.
      if (this.heatBuffer != heatBefore || this.progress != progressBefore
         || this.inputTank.getFluidAmount() != inBefore || this.outputTank.getFluidAmount() != outBefore) {
         this.markForSave();
      }
   }

   private int requestHeat(int n) {
      int left = n;

      for (net.minecraft.core.Direction direction : com.industrialreforged.tech.core.util.Util.ALL_DIRS) {
         net.minecraft.world.level.block.entity.BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(direction));
         if (blockEntity instanceof com.industrialreforged.tech.api.tile.IHeatSource source) {
            int available = source.extractHeat(direction.getOpposite(), left, true);
            if (available > 0) {
               left -= source.extractHeat(direction.getOpposite(), available, false);
               if (left <= 0) {
                  break;
               }
            }
         }
      }

      return n - left;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new FermenterMenu(containerId, playerInventory, this);
   }

   public FluidTank getInputTank() {
      return this.inputTank;
   }

   public FluidTank getOutputTank() {
      return this.outputTank;
   }

   @Override
   public int getEnergyStored() {
      return 40;
   }

   @Override
   public boolean useEnergy(int d) {
      return true;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.FLUID_CONSUMING, UpgradableProperty.FLUID_PRODUCING,
         UpgradableProperty.STRENGTHENING
      );
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.fluidInputCellInSlot, this.fluidInputCellOutSlot, this.inputTank);
      FluidSlotHelper.tick(this.fluidOutputCellInSlot, this.fluidOutputCellOutSlot, this.outputTank);
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

   @Nullable
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.wrapper == null) {
         this.wrapper = this.fertiliserSlot.createWrapper();
      }

      return this.wrapper;
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> this.heatBuffer;
         case 1 -> this.progress;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.heatBuffer = value;
            break;
         case 1:
            this.progress = value;
      }
   }

   public int getCount() {
      return 2;
   }
}
