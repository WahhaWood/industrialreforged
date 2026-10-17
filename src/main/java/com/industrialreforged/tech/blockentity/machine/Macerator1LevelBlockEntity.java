package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableStandard;
import com.industrialreforged.tech.core.recipe.MaceratorRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.StandardLevelMachineMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Macerator1LevelBlockEntity extends AbstractStandardMachineBlockEntity<MaceratorRecipe> {
   /** Base FE/t and ticks per operation; the ME version prices an operation from these. */
   public static final int ENERGY_CONSUME = 3;
   public static final int OPERATION_LENGTH = 300;
   public Macerator1LevelBlockEntity(BlockPos blockPos, BlockState blockState) {
      this(1, 4, IRBlockEntities.MACERATOR_1_LEVEL.get(), blockPos, blockState);
   }

   public Macerator1LevelBlockEntity(
      int size, int upgrades, BlockEntityType<? extends Macerator1LevelBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(128, ENERGY_CONSUME, OPERATION_LENGTH, size, upgrades, blockEntityType, blockPos, blockState);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableStandard<>(this, "input", outputs, IRRecipeTypes.MACERATOR.get());
   }

   @Override
   public void clientTick() {
      super.clientTick();
      Level level = this.getLevel();
      if (this.isActive() && level.random.nextInt(8) == 0) {
         for (int i = 0; i < 4; i++) {
            double d = this.worldPosition.getX() + 0.5 + level.random.nextFloat() * 0.6 - 0.3;
            double d2 = this.worldPosition.getY() + 1 + level.random.nextFloat() * 0.2 - 0.1;
            double d3 = this.worldPosition.getZ() + 0.5 + level.random.nextFloat() * 0.6 - 0.3;
            level.addParticle(ParticleTypes.SMOKE, d, d2, d3, 0.0, 0.0, 0.0);
         }
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new StandardLevelMachineMenu(containerId, playerInventory, this);
   }

   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACERATOR_START.get();
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
