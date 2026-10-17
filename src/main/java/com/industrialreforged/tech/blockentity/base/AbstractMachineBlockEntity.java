package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractMachineBlockEntity extends AbstractBlockEntity implements IServerTick, MenuProvider, IClientTick {
   private Object soundInstance;
   private SoundUtil.SoundState soundState;

   public AbstractMachineBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   /** Machines with fluid container slots move fluid here every tick. */
   protected void tickFluidSlots() {
   }

   @Override
   public void serverTick() {
      this.tickFluidSlots();
   }

   /** Машины переопределяют, чтобы дать свой звук; null = тишина. */
   public SoundEvent getStartSoundFile() {
      return null;
   }

   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_MACHINE_INTERRUPT.get();
   }

   /** Шумоизоляция: машины со слотом апгрейдов переопределяют. */
   protected boolean isSoundproofed() {
      return false;
   }

   /** Машина хочет работать, но не может (нет энергии/места) — для звука прерывания. */
   protected boolean isInterrupted() {
      return this.hasWork();
   }

   /** Есть ли незавершённая работа: машины с прогрессом переопределяют. */
   protected boolean hasWork() {
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

      SoundUtil.SoundState currentState = this.isActive()
         ? SoundUtil.SoundState.RUNNING
         : (this.isInterrupted() ? SoundUtil.SoundState.INTERRUPTED : SoundUtil.SoundState.IDLE);
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
}
