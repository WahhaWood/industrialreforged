package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableStandard;
import com.industrialreforged.tech.core.recipe.CompressorRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.StandardLevelMachineMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Compressor1LevelBlockEntity extends AbstractStandardMachineBlockEntity<CompressorRecipe> {
   /** Base FE/t and ticks per operation; the ME version prices an operation from these. */
   public static final int ENERGY_CONSUME = 6;
   public static final int OPERATION_LENGTH = 300;
   public Compressor1LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      this(1, 4, IRBlockEntities.COMPRESSOR_1_LEVEL.get(), blockPos, blockState);
   }

   public Compressor1LevelBlockEntity(
      int size, int upgrades, BlockEntityType<? extends Compressor1LevelBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(128, ENERGY_CONSUME, OPERATION_LENGTH, size, upgrades, blockEntityType, blockPos, blockState);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableStandard<>(this, "input", outputs, IRRecipeTypes.COMPRESSOR.get());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new StandardLevelMachineMenu(containerId, playerInventory, this);
   }

   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_COMPRESSOR_START.get();
   }

   @Override
   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_MACHINE_INTERRUPT.get();
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
}
