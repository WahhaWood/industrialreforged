package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.tile.IKineticSource;
import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableClass;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.item.turning.ItemTurningMold;
import com.industrialreforged.tech.item.turning.ItemTurningProduct;
import com.industrialreforged.tech.item.turning.TurningProgram;
import com.industrialreforged.tech.menu.machine.LatheMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LatheBlockEntity extends AbstractMachineBlockEntity {
   public InvSlotConsumable toolSlot = new InvSlotConsumableItem(this, "slotTool", 1, IRItems.TURNING_TOOL);
   public InvSlotConsumableClass latheSlot = new InvSlotConsumableClass(this, "lathe", 1, ItemTurningMold.class);
   public InvSlotOutput outputSlot = new InvSlotOutput(this, "dusts", 1);
   public int kUBuffer = 0;

   public LatheBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.TURNING.get(), blockPos, blockState);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACERATOR_START.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.toolSlot.load(tag, registries);
      this.latheSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.kUBuffer = tag.getInt("kUBuffer");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.toolSlot.save(tag, registries);
      this.latheSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      tag.putInt("kUBuffer", this.kUBuffer);
   }

   private void getKU() {
      for (Direction dir : Direction.values()) {
         BlockEntity te = this.level.getBlockEntity(this.worldPosition.relative(dir));
         if (te instanceof IKineticSource) {
            int kineticEnergy = ((IKineticSource)te).getKineticEnergy(dir.getOpposite());
            if (kineticEnergy != 0) {
               int diff = Math.min(kineticEnergy, 10000 - this.kUBuffer);
               this.kUBuffer = this.kUBuffer + ((IKineticSource)te).extractKineticEnergy(dir.getOpposite(), diff, false);
               break;
            }
         }
      }
   }

   public boolean process(int position) {
      if (!this.canWork(false)) {
         return false;
      }

      ItemStack moldStack = this.latheSlot.getItem();
      ItemTurningMold mold = (ItemTurningMold)moldStack.getItem();
      ItemStack turningTool = this.toolSlot.getItem();
      if (!this.outputSlot.canAdd(mold.getOutputItem(moldStack))) {
         return false;
      }

      long currentState = mold.getCurrentState(moldStack);
      byte state = TurningProgram.toState(currentState, position);
      if (state <= 1) {
         return false;
      }

      mold.setState(moldStack, position, (byte)(state - 1));
      if (this.level.random.nextFloat() < mold.getOutputChance()) {
         this.outputSlot.add(mold.getOutputItem(moldStack));
      }

      turningTool.setDamageValue(turningTool.getDamageValue() + 1);
      if (turningTool.getDamageValue() >= turningTool.getMaxDamage()) {
         this.toolSlot.setItem(ItemStack.EMPTY);
      }

      this.kUBuffer -= 1000;
      long states = mold.getCurrentState(moldStack);

      for (TurningProgram type : mold.getTypes()) {
         if (type.states() == states) {
            this.latheSlot.clearContent();
            this.latheSlot.setItem(new ItemStack((ItemLike)ItemTurningProduct.PROGRAMS.get(type)));
         }
      }

      return true;
   }

   public boolean canWork(boolean power) {
      return !this.toolSlot.isEmpty()
         && !this.latheSlot.isEmpty()
         && this.latheSlot.getItem().getItem() instanceof ItemTurningMold
         && (this.kUBuffer >= 1000 || power);
   }

   /** Каждый тик подтягиваем вращение с соседей — без этого kUBuffer вечно 0
    * и canWork никогда не даёт резать (getKU никто не вызывал). */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      int before = this.kUBuffer;
      this.getKU();
      if (this.kUBuffer != before) {
         this.markForSave();
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new LatheMenu(containerId, playerInventory, this);
   }
}
