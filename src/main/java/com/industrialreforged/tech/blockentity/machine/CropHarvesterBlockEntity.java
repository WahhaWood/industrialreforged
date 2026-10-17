package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.CropHarvesterMenu;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class CropHarvesterBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IContainerWrapper {
   public final InvSlotOutput contentSlot = new InvSlotOutput(this, "content", 15);
   public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, 4);
   public int scanX = -4;
   public int scanY = -1;
   public int scanZ = -4;
   private IItemHandlerModifiable wrapper;

   public CropHarvesterBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(40000, 128, IRBlockEntities.CROP_HARVESTER.get(), blockPos, blockState, false);
      this.energyContainer.setExtractTick(500);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACERATOR_START.get();
   }

   @Override
   protected boolean isSoundproofed() {
      return this.upgradeSlot.soundproofing;
   }

   @Override
   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.contentSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
   }

   @Override
   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.contentSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
   }

   private boolean isInvFull() {
      for (int i = 0; i < this.contentSlot.getContainerSize(); i++) {
         ItemStack itemStack = this.contentSlot.getItem(i);
         if (itemStack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(UpgradableProperty.TRANSFORMER, UpgradableProperty.ENERGY_STORAGE, UpgradableProperty.ITEM_PRODUCING);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new CropHarvesterMenu(containerId, playerInventory, this);
   }

   @Nullable
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.wrapper == null) {
         this.wrapper = this.contentSlot.createWrapper();
      }

      return this.wrapper;
   }
}
