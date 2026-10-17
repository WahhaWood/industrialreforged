package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.api.tile.IHeatSource;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractHeatSourceBlockEntity extends BaseBlockEntity implements IHeatSource, IServerTick, MenuProvider, IClientTick {
   private Object soundInstance;
   private SoundUtil.SoundState soundState;

   /** Machines with fluid container slots move fluid here every tick. */
   protected void tickFluidSlots() {
   }

   @Override
   public void serverTick() {
      this.tickFluidSlots();
   }

   /** Источники тепла переопределяют, чтобы дать свой звук; null = тишина. */
   public SoundEvent getStartSoundFile() {
      return null;
   }

   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_MACHINE_INTERRUPT.get();
   }

   /** Шумоизоляция: источники тепла со слотом апгрейдов переопределяют. */
   protected boolean isSoundproofed() {
      return false;
   }

   @Override
   public void clientTick() {
      if (this.isSoundproofed()) {
         SoundUtil.stop(this.soundInstance);
         this.soundInstance = null;
         this.soundState = SoundUtil.SoundState.IDLE;
         return;
      }

      // источник тепла «работает», пока в буфере есть тепло
      SoundUtil.SoundState currentState = this.getHeat() > 0 ? SoundUtil.SoundState.RUNNING : SoundUtil.SoundState.IDLE;
      if (currentState != this.soundState) {
         this.soundState = currentState;
         switch (currentState) {
            case RUNNING:
               this.soundInstance = SoundUtil.playLoop(this, this.getStartSoundFile(), this.soundInstance);
               break;
            case IDLE:
               SoundUtil.stop(this.soundInstance);
               this.soundInstance = null;
               break;
            case INTERRUPTED:
               this.soundInstance = SoundUtil.play(this, this.getInterruptSoundFile(), this.soundInstance);
         }
      }
   }

   /** Upper bound for the stored heat of a heat source. */
   public static final int MAX_HEAT = 10000;

   private int heat;

   public AbstractHeatSourceBlockEntity(BlockEntityType<? extends AbstractHeatSourceBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   @Override
   public int getHeat(Direction direction) {
      return this.facingMatchesDirection(direction) ? this.getMaxHeatEmittedPerTick() : 0;
   }

   @Override
   public int extractHeat(Direction direction, int amount, boolean simulation) {
      if (this.facingMatchesDirection(direction)) {
         int heat = this.getHeat();
         if (heat >= amount) {
            if (!simulation) {
               this.setHeat(heat - amount);
            }

            return amount;
         } else {
            if (!simulation) {
               this.setHeat(0);
            }

            return heat;
         }
      } else {
         return 0;
      }
   }

   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.heat = tag.getInt("heat");
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("heat", this.heat);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().asItem().getDescription();
   }

   public boolean facingMatchesDirection(Direction direction) {
      return direction == RotateBlock.getHorizontalFacing(this);
   }

   public int getHeat() {
      return this.heat;
   }

   public void setHeat(int value) {
      this.heat = value;
   }

   public void increaseHeat(int amount) {
      this.setHeat(this.getHeat() + amount);
   }

   public Component getOutputRatio() {
      // Формат строки «%s/%s HU» читается как «X из Y», поэтому вторым числом обязан
   // идти потолок, а не отдача за тик. Раньше здесь стояла getMaxHeatEmittedPerTick(),
      // и на экране было «9600/20 HU» — то есть в 480 раз «переполнено», хотя до
    // реального предела оставалось 400. Отдачу за тик показывает отдельный
      // тултип text.industrialreforged.tooltip.heat.transfer.
      return Component.translatable("text.industrialreforged.hu.ratio", new Object[]{this.getHeat(), MAX_HEAT});
   }

   protected abstract int fillHeatBuffer(int var1);

   public abstract int getMaxHeatEmittedPerTick();
}
