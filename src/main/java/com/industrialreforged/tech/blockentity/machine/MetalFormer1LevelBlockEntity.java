package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableForming;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.MetalFormerMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MetalFormer1LevelBlockEntity extends AbstractStandardMachineBlockEntity<MetalFormerRecipe> {
   /** Base FE/t and ticks per operation; the ME version prices an operation from these. */
   public static final int ENERGY_CONSUME = 10;
   public static final int OPERATION_LENGTH = 200;
   private MetalFormerRecipe.Mode mode = MetalFormerRecipe.Mode.EXTRUDING;

   public MetalFormer1LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      this(1, 4, IRBlockEntities.METAL_FORMER_1_LEVEL.get(), blockPos, blockState);
   }

   public MetalFormer1LevelBlockEntity(
      int size, int upgrades, BlockEntityType<? extends MetalFormer1LevelBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(128, ENERGY_CONSUME, OPERATION_LENGTH, size, upgrades, blockEntityType, blockPos, blockState);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableForming(this, "input", outputs);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_COMPRESSOR_START.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.setMode(MetalFormerRecipe.Mode.values()[tag.getByte("mode")]);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putByte("mode", (byte)this.mode.ordinal());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new MetalFormerMenu(containerId, playerInventory, this);
   }

   public MetalFormerRecipe.Mode getMode() {
      return this.mode;
   }

   public void setMode(MetalFormerRecipe.Mode value) {
      this.mode = value;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.STRENGTHENING,
         UpgradableProperty.SOUNDPROOFING
      );
   }

   @Override
   public int get(int index) {
      return index == 3 ? this.mode.ordinal() : super.get(index);
   }

   @Override
   public void set(int index, int value) {
      if (index == 3) {
         this.mode = MetalFormerRecipe.Mode.values()[value];
      }

      super.set(index, value);
   }

   @Override
   public int getCount() {
      return 4;
   }
}
