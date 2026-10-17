package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.blockentity.base.AbstractElectricGeneratorBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.generator.electric.GeothermalGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorBlockEntity extends AbstractElectricGeneratorBlockEntity implements IFluidWrapper {
   public final InvSlotConsumableLiquid input;
   public final InvSlotOutput output;
   public final FluidTank tank = new FluidTank(8000, true, false, s -> s.is(Fluids.LAVA));
   private IItemHandlerModifiable containerWrapper;

   public GeothermalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.GEO_GENERATOR.get(), blockPos, blockState, 153600L, 40L);
      this.input = new InvSlotConsumableLiquidByTank(this, "fluidSlot", 1, InvSlotConsumableLiquid.OpType.Drain, this.tank);
      this.output = new InvSlotOutput(this, 1);
   }

   @Override
   public void loadAdditional(CompoundTag tag, @NotNull Provider registries) {
      super.loadAdditional(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.input.load(tag, registries);
      this.output.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, @NotNull Provider registries) {
      super.saveAdditional(tag, registries);
      this.tank.save(registries, tag, "tank");
      this.input.save(tag, registries);
      this.output.save(tag, registries);
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.containerWrapper == null) {
            this.containerWrapper = new CombinedInvWrapper(
               new IItemHandlerModifiable[]{this.input.createWrapper(), this.output.createWrapper(), this.container.createWrapper()}
            );
         }
         return this.containerWrapper;
      }

      return switch (direction) {
         case UP -> this.input.createWrapper();
         case DOWN -> this.output.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.input.createWrapper(), this.output.createWrapper()}
         );
      };
   }

   @Override
   public boolean isConverting() {
      return this.fuel > 0;
   }

   @Override
   protected boolean consumesFuel() {
      return true;
   }

   @Override
   protected int consumeFuel() {
      net.neoforged.neoforge.fluids.FluidStack simulated = this.tank.drainForce(1000, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
      if (simulated.getAmount() < 1000) {
         return 0;
      }

      this.tank.drainForce(1000, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
      return 1000;
   }

   @Override
   protected void tickFluidSlots() {
      com.industrialreforged.tech.core.util.FluidSlotHelper.tick(this.input, this.output, this.tank);
   }

   @Override
   public SoundEvent getOperationSoundFile() {
      return IRSounds.SOUND_GEOTHERMAL_LOOP.get();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new GeothermalGeneratorMenu(containerId, playerInventory, this);
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }
}
