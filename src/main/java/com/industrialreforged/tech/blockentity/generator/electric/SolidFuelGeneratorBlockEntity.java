package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.tech.blockentity.base.AbstractElectricGeneratorBlockEntity;
import com.industrialreforged.tech.container.SimpleFuelContainer;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.ParticleUtil;
import com.industrialreforged.tech.menu.generator.electric.SolidFuelGeneratorMenu;
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

public class SolidFuelGeneratorBlockEntity extends AbstractElectricGeneratorBlockEntity implements IClientTick {
   public final SimpleFuelContainer input = new SimpleFuelContainer(this, false);
   public int totalFuel = 0;

   public SolidFuelGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.GENERATOR.get(), blockPos, blockState, 128000L, 20L);
   }

   public void clientTick() {
      if (ActiveBlock.isActive(this)) {
         ParticleUtil.showFurnaceFlames(this.level, this.worldPosition, RotateBlock.getHorizontalFacing(this));
      }
   }

   public float getFuelRatio() {
      return this.fuel <= 0 ? 0.0F : (float)this.fuel / this.totalFuel;
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
      int value = this.input.consumeFuel();
      if (value > 0) {
         this.totalFuel = value;
      }

      return value;
   }

   @Override
   public SoundEvent getOperationSoundFile() {
      return IRSounds.SOUND_GENERATOR_LOOP.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.totalFuel = tag.getInt("totalFuel");
      this.input.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("totalFuel", this.totalFuel);
      this.input.save(tag, registries);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SolidFuelGeneratorMenu(containerId, playerInventory, this);
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.input.createWrapper(), this.container.createWrapper()});
         }
         return this.wrapper;
      }

      return switch (direction) {
         case DOWN -> this.container.createWrapper();
         case UP, NORTH, SOUTH, EAST, WEST -> this.input.createWrapper();
      };
   }
}
