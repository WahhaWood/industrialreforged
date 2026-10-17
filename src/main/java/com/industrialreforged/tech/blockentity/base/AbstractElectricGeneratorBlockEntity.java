package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.blockentity.mix.EnergyAndContainerBlockEntity;
import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractElectricGeneratorBlockEntity extends EnergyAndContainerBlockEntity<BigEnergyStorage> implements IServerTick, MenuProvider {
   public int fuel = 0;
   public long production;
   private int ticksSinceLastActiveUpdate;
   private int activityMeter = 0;
   public Object audioSource;

   public AbstractElectricGeneratorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, long capacity, long production) {
      this(blockEntityType, blockPos, blockState, capacity, production, 1);
   }

   public AbstractElectricGeneratorBlockEntity(
      BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, long capacity, long production, int chargeSlots
   ) {
      super(blockEntityType, blockPos, blockState, new BigEnergyStorage(capacity, 0L, production * 2L), chargeSlots);
      this.production = production;
      this.ticksSinceLastActiveUpdate = IR.random.nextInt(256);
   }

   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.fuel = tag.getInt("burnTime");
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      // "fuel" is the name of the item container, so the remaining burn time
      // is stored under its own key; sharing the key wiped the counter (and
      // the container) whenever the block entity was saved.
      tag.putInt("burnTime", this.fuel);
   }

   public void onChunkUnloaded() {
      if (IR.sideProxy.isRendering() && this.audioSource != null) {
         SoundUtil.stop(this.audioSource);
      }

      super.onChunkUnloaded();
   }

   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      this.onServerTick();
      this.tickFluidSlots();
      com.industrialreforged.tech.core.util.EnergySlotHelper.tick(this.container, this.energy);

      boolean running = false;

      if (this.consumesFuel()) {
         // Only burn while the buffer can actually take the output, so an
         // idle generator does not eat fuel for nothing.
         boolean canStore = this.energy.hasSpace();

         if (this.fuel <= 0 && canStore) {
            int value = this.consumeFuel();
            if (value > 0) {
               this.fuel = value;
            }
         }

         if (this.fuel > 0 && canStore) {
            this.fuel--;
            running = true;
         }
      } else {
         running = this.isConverting();
      }

      if (running && this.energy.hasSpace()) {
         this.energy.injectForceEnergy(Math.min(this.production, this.energy.getSpace()));
      }

      com.industrialreforged.utils.EnergyUtils.transferEnergyAllNear(this, this.energy);

      if (--this.ticksSinceLastActiveUpdate <= 0) {
         this.ticksSinceLastActiveUpdate = 8;
         ActiveBlock.setActive(this, running);
      }
   }

   /**
    * The item slot of a generator charges batteries straight from the internal
    * buffer (IC2 style).  It keeps the historical NBT name "contents".
    */
   @Override
   protected com.industrialreforged.container.BasicSimpleContainer createContainer(int size, int limit) {
      long rate = Math.max(1L, this.energy.getExtract());
      return new com.industrialreforged.tech.container.EnergyContainer(
         this, "contents", (int)Math.min(rate, Integer.MAX_VALUE), size
      );
   }

   /** Hook for generators that need periodic work (solar visibility, etc.). */
   protected void onServerTick() {
   }

   /** Generators with fluid container slots move fluid here every tick. */
   protected void tickFluidSlots() {
   }

   /** True when this generator needs fuel to run. */
   protected boolean consumesFuel() {
      return false;
   }

   /** Consumes/tops up fuel, returning the amount of fuel ticks gained. */
   protected int consumeFuel() {
      return 0;
   }

   public boolean isConverting() {
      return !this.needsFuel() && ((BigEnergyStorage)this.energy).getMaxEnergyStored() - ((BigEnergyStorage)this.energy).getEnergyStored() >= this.production;
   }

   public boolean needsFuel() {
      return this.fuel <= 0 && ((BigEnergyStorage)this.energy).getMaxEnergyStored() - ((BigEnergyStorage)this.energy).getEnergyStored() >= this.production;
   }

   @Nullable
   public SoundEvent getOperationSoundFile() {
      return null;
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().asItem().getDescription();
   }

   public void setBlockState(BlockState blockState) {
      super.setBlockState(blockState);
      if (this.level.isClientSide) {
         if (this.audioSource == null && this.getOperationSoundFile() != null) {
            this.audioSource = SoundUtil.playLoop(this, this.getOperationSoundFile(), this.audioSource);
         }

         this.audioSource = ActiveBlock.isActive(this) ? SoundUtil.playSound(this.audioSource) : SoundUtil.stop(this.audioSource);
      }
   }
}
