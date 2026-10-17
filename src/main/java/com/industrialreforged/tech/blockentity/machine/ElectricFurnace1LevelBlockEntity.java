package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableSmelting;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.StandardLevelMachineMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnace1LevelBlockEntity extends AbstractStandardMachineBlockEntity<SmeltingRecipe> implements IUpgradableBlock {
   /** Base FE/t and ticks per operation; the ME version prices an operation from these. */
   public static final int ENERGY_CONSUME = 8;
   public static final int OPERATION_LENGTH = 100;
   public ElectricFurnace1LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      this(1, 4, IRBlockEntities.ELECTRIC_FURNACE_1_LEVEL.get(), blockPos, blockState);
   }

   public ElectricFurnace1LevelBlockEntity(
      int size, int upgrades, BlockEntityType<? extends ElectricFurnace1LevelBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(128, ENERGY_CONSUME, OPERATION_LENGTH, size, upgrades, blockEntityType, blockPos, blockState);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableSmelting<>(this, "input", outputs);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new StandardLevelMachineMenu(containerId, playerInventory, this);
   }

   public SoundEvent getStartingSoundFile() {
      return IRSounds.SOUND_ELECTRO_FURNACE_START.get();
   }

   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_ELECTRO_FURNACE_LOOP.get();
   }

   @Override
   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_ELECTRO_FURNACE_STOP.get();
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
