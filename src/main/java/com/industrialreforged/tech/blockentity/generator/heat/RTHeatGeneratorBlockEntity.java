package com.industrialreforged.tech.blockentity.generator.heat;

import com.industrialreforged.tech.blockentity.base.AbstractHeatSourceBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.generator.heat.RTHeatGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RTHeatGeneratorBlockEntity extends AbstractHeatSourceBlockEntity {

   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      boolean burning = !this.fuelSlot.isEmpty();
      if (burning && this.getHeat() < MAX_HEAT) {
         this.increaseHeat(this.getMaxHeatEmittedPerTick());
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, burning);
   }

   public final InvSlotConsumable fuelSlot = new InvSlotConsumableItem(this, "fuelSlot", 6, IRItems.URANIUM_PELLET, IRItems.MOX_PELLET, IRItems.RTG_PELLET);

   public RTHeatGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.RT_HEAT_GENERATOR.get(), blockPos, blockState);
      this.fuelSlot.setMaxStackSize(1);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_GENERATOR_LOOP.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.fuelSlot.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.fuelSlot.save(tag, registries);
   }

   @Override
   protected int fillHeatBuffer(int n) {
      return Math.min(n, this.getMaxHeatEmittedPerTick());
   }

   @Override
   public int getMaxHeatEmittedPerTick() {
      float counter = 2.0F;

      for (int i = 0; i < this.fuelSlot.getContainerSize(); i++) {
         ItemStack stack = this.fuelSlot.getItem(i);
         if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item == IRItems.URANIUM_PELLET.get()) {
               counter *= 1.588F;
            } else if (item == IRItems.MOX_PELLET.get()) {
               counter *= 1.782F;
            } else if (item == IRItems.RTG_PELLET.get()) {
               counter *= 2.0F;
            }
         }
      }

      return counter == 2.0F ? 0 : Mth.floor(counter / 2.0F);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new RTHeatGeneratorMenu(containerId, playerInventory, this);
   }
}
