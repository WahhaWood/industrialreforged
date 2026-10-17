package com.industrialreforged.tech.blockentity.generator.kinetic;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.recipe.Recipes;
import com.industrialreforged.tech.api.tile.FluidHandler;
import com.industrialreforged.tech.api.tile.IHeatSource;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractKineticSourceBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByManager;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.menu.generator.kinetic.StirlingKineticGeneratorMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class StirlingKineticGeneratorBlockEntity extends AbstractKineticSourceBlockEntity implements IUpgradableBlock, IFluidWrapper {
   public FluidTank inputTank;
   public FluidTank outputTank;
   public InvSlotOutput hotoutputSlot;
   public InvSlotOutput cooloutputSlot;
   public InvSlotConsumableLiquidByTank hotfluidinputSlot;
   public InvSlotConsumableLiquidByManager coolfluidinputSlot;
   public InvSlotUpgrade upgradeSlot;
   private int heatbuffer = 0;
   private final int maxHeatbuffer;
   private final int maxkUBuffer;
   private int liquidHeatStored;
   private FluidHandler fluidHandler;

   public StirlingKineticGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.STIRLING_KINETIC_GENERATOR.get(), blockPos, blockState);
      this.inputTank = new FluidTank(2000, true, false, stack -> Recipes.liquidHeatupManager.getSingleDirectionLiquidManager().acceptsFluid(stack.getFluid()));
      this.outputTank = new FluidTank(2000, false, true, fluid -> true);
      this.hotoutputSlot = new InvSlotOutput(this, "hotOutputSlot", 1);
      this.cooloutputSlot = new InvSlotOutput(this, "outputSlot", 1);
      this.coolfluidinputSlot = new InvSlotConsumableLiquidByManager(
         this, "coolfluidinputSlot", 1, InvSlotConsumableLiquid.OpType.Drain, Recipes.liquidHeatupManager.getSingleDirectionLiquidManager()
      );
      this.hotfluidinputSlot = new InvSlotConsumableLiquidByTank(this, "hotfluidoutputSlot", 1, InvSlotConsumableLiquid.OpType.Fill, this.outputTank);
      this.upgradeSlot = new InvSlotUpgrade(this, 3);
      this.maxHeatbuffer = 1000;
      this.maxkUBuffer = 2000;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputTank.load(registries, tag, "input_tank");
      this.outputTank.load(registries, tag, "output_tank");
      this.hotoutputSlot.load(tag, registries);
      this.cooloutputSlot.load(tag, registries);
      this.hotfluidinputSlot.load(tag, registries);
      this.coolfluidinputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.heatbuffer = tag.getInt("heatbuffer");
      // Saved as "kUBuffer" below; the old lower-case spelling made every
      // reload drop the stored kinetic buffer.
      this.power = tag.getInt("kUBuffer");
      this.liquidHeatStored = tag.getInt("liquidHeatStored");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputTank.save(registries, tag, "input_tank");
      this.outputTank.save(registries, tag, "output_tank");
      this.hotoutputSlot.save(tag, registries);
      this.cooloutputSlot.save(tag, registries);
      this.hotfluidinputSlot.save(tag, registries);
      this.coolfluidinputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putInt("heatbuffer", this.heatbuffer);
      tag.putInt("kUBuffer", this.power);
      tag.putInt("liquidHeatStored", this.liquidHeatStored);
   }

   private int drawHu(int n) {
      if (n <= 0) {
         return 0;
      }

      Level level = this.getLevel();
      int n3 = n;
      Direction[] arrdirection = Util.ALL_DIRS;
      int n4 = arrdirection.length;
      int i = 0;

      IHeatSource iHeatSource;
      int n2;
      BlockEntity blockEntity;
      Direction direction;
      while (
         i < n4
            && (
               (direction = arrdirection[i]) == RotateBlock.getHorizontalFacing(this)
                  || !((blockEntity = level.getBlockEntity(this.worldPosition.relative(direction))) instanceof IHeatSource)
                  || (n2 = (iHeatSource = (IHeatSource)blockEntity).extractHeat(direction.getOpposite(), n3, true)) <= 0
                  || (n3 -= iHeatSource.extractHeat(direction.getOpposite(), n2, false)) > 0
            )
      ) {
         i++;
      }

      return n - n3;
   }

   /** Греется от соседей и от горячей жидкости, превращая тепло в кинетику. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      int heatBefore = this.heatbuffer;
      int powerBefore = this.getPower();
      int inBefore = this.inputTank.getFluidAmount();
      int outBefore = this.outputTank.getFluidAmount();
      if (this.heatbuffer < this.maxHeatbuffer) {
         this.heatbuffer += this.drawHu(this.maxHeatbuffer - this.heatbuffer);
      }

      boolean active = false;
      if (this.inputTank.getFluidAmount() > 0
         && this.outputTank.getCapacity() - this.outputTank.getFluidAmount() > 0
         && this.power < this.maxkUBuffer) {
         var property = Recipes.liquidHeatupManager.getHeatExchangeProperty(this.inputTank.getFluid().getFluid());
         if (property != null && this.heatbuffer >= property.huPerMB()) {
            this.heatbuffer -= property.huPerMB();
            this.inputTank.drainForce(1, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
            this.outputTank.fillForce(
               new net.neoforged.neoforge.fluids.FluidStack(property.outputFluid(), 1),
               net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE
            );
            this.power = Math.min(this.maxkUBuffer, this.power + property.huPerMB());
            active = true;
         }
      }

      if (!active && this.heatbuffer > 0 && this.power < this.maxkUBuffer) {
         int convert = Math.min(this.heatbuffer, this.maxkUBuffer - this.power);
         this.power += convert;
         this.heatbuffer -= convert;
         active = true;
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, active || this.getPower() > 0);
      // Теплообмен шёл (буфер/мощность/баки изменились) — по делу; пустой простой — не пачкаем.
      if (this.heatbuffer != heatBefore || this.getPower() != powerBefore
         || this.inputTank.getFluidAmount() != inBefore || this.outputTank.getFluidAmount() != outBefore) {
         this.markForSave();
      }
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.FLUID_CONSUMING, UpgradableProperty.FLUID_PRODUCING
      );
   }

   @Override
   public int getEnergyStored() {
      return 40;
   }

   @Override
   public boolean useEnergy(int d) {
      return true;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new StirlingKineticGeneratorMenu(containerId, playerInventory, this);
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
