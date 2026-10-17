package com.industrialreforged.tech.blockentity.generator.heat;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.container.wrapper.InputInvWrapper;
import com.industrialreforged.container.wrapper.OutputInvWrapper;
import com.industrialreforged.tech.blockentity.base.AbstractHeatSourceBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.SimpleFuelContainer;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.generator.heat.SolidHeatGeneratorMenu;
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

public class SolidHeatGeneratorBlockEntity extends AbstractHeatSourceBlockEntity implements IContainerWrapper {

   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      int fuelBefore = this.fuel;
      if (this.fuel <= 0) {
         int value = this.input.consumeFuel();
         if (value > 0) {
            this.fuel = value;
            // Denominator of the fuel gauge in the GUI; nothing ever set it, so the bar
            // had nothing sensible to divide by.
            this.itemFuelTime = value;
         }
      }

      if (this.fuel > 0) {
         this.fuel--;
         if (this.getHeat() < MAX_HEAT) {
            this.increaseHeat(this.getMaxHeatEmittedPerTick());
         }
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, this.fuel > 0);
      // Топливо не горит и не подобрано — менять нечего, чанк не пачкаем.
      if (this.fuel != fuelBefore) {
         this.markForSave();
      }
   }

   private int heatbuffer = 0;
   public int activityMeter = 0;
   public int ticksSinceLastActiveUpdate;
   public int fuel = 0;
   public int itemFuelTime = 0;
   public final SimpleFuelContainer input = new SimpleFuelContainer(this, false);
   public final InvSlotOutput output = new InvSlotOutput(this, 1);
   private IItemHandlerModifiable wrapper;

   public SolidHeatGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.SOLID_HEAT_GENERATOR.get(), blockPos, blockState);
      this.ticksSinceLastActiveUpdate = IR.random.nextInt(256);
   }

   public boolean needsFuel() {
      return this.fuel <= 0 && this.getHeat() == 0;
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_GENERATOR_LOOP.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      // "fuel" belongs to the fuel container (a list of items).  The counter used the same
      // key, so one of the two always overwrote the other on save — the remaining burn time
      // was reset to 0 on every chunk reload.
      this.fuel = tag.contains("burnTime") ? tag.getInt("burnTime") : tag.getInt("fuel");
      this.itemFuelTime = tag.getInt("itemFuelTime");
      this.input.load(tag, registries);
      this.output.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("burnTime", this.fuel);
      tag.putInt("itemFuelTime", this.itemFuelTime);
      this.input.save(tag, registries);
      this.output.save(tag, registries);
   }

   public boolean isConverting() {
      return this.fuel > 0;
   }

   @Override
   protected int fillHeatBuffer(int n) {
      if (this.heatbuffer - n >= 0) {
         this.heatbuffer -= n;
         return n;
      } else {
 n = this.heatbuffer;
         this.heatbuffer = 0;
         return n;
      }
   }

   @Override
   public int getMaxHeatEmittedPerTick() {
      return 20;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SolidHeatGeneratorMenu(containerId, playerInventory, this);
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{new InputInvWrapper(this.input), new OutputInvWrapper(this.output)});
         }
         return this.wrapper;
      }

      return switch (direction) {
         case UP -> new InputInvWrapper(this.input);
         case DOWN -> new OutputInvWrapper(this.output);
         case NORTH, SOUTH, EAST, WEST -> {
            if (this.wrapper == null) {
               this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{new InputInvWrapper(this.input), new OutputInvWrapper(this.output)});
            }
            yield this.wrapper;
         }
      };
   }
}
