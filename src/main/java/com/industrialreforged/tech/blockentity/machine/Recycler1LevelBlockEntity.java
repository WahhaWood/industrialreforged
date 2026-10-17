package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableRecycler;
import com.industrialreforged.tech.core.recipe.RecyclerRecipe;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Recycler (IC2): any item except the blacklist, 45 ticks at 1 FE/t, scrap with a 1/8 chance. */
public class Recycler1LevelBlockEntity extends AbstractStandardMachineBlockEntity<RecyclerRecipe> {
   public static final int ENERGY_CONSUME = 1;
   public static final int OPERATION_LENGTH = 45;

   public Recycler1LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      this(1, 4, IRBlockEntities.RECYCLER_1_LEVEL.get(), blockPos, blockState);
   }

   public Recycler1LevelBlockEntity(
      int size, int upgrades, BlockEntityType<? extends Recycler1LevelBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(128, ENERGY_CONSUME, OPERATION_LENGTH, size, upgrades, blockEntityType, blockPos, blockState);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableRecycler(this, "input", outputs);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new StandardLevelMachineMenu(containerId, playerInventory, this);
   }

   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_RECYCLER_START.get();
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
