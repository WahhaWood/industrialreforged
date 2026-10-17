package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableSolidCanner;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableSolidCanner;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.menu.machine.SolidCannerMenu;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class SolidCannerBlockEntity extends AbstractStandardMachineBlockEntity<SolidCannerRecipe> {
   public final InvSlotConsumableSolidCanner canInputSlot = new InvSlotConsumableSolidCanner(this, "canInput", 1);
   private IItemHandlerModifiable containerWrapper;

   public SolidCannerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2, 200, 1, IRBlockEntities.SOLID_CANNER.get(), blockPos, blockState);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableSolidCanner(this, "input", 1);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.canInputSlot.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.canInputSlot.save(tag, registries);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SolidCannerMenu(containerId, playerInventory, this);
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.containerWrapper == null) {
            this.containerWrapper = new CombinedInvWrapper(
               new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.canInputSlot.createWrapper(), this.outputSlot.createWrapper()}
            );
         }
         return this.containerWrapper;
      }

      return switch (direction) {
         case UP -> new com.industrialreforged.tech.container.InsertOnlyHandler(this.inputSlot.createWrapper());
         case DOWN -> this.outputSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> {
            if (this.containerWrapper == null) {
               this.containerWrapper = new CombinedInvWrapper(
                  new IItemHandlerModifiable[]{this.canInputSlot.createWrapper(), this.inputSlot.createWrapper(), this.outputSlot.createWrapper()}
               );
            }
            yield this.containerWrapper;
         }
      };
   }

   @Override
   public SoundEvent getStartSoundFile() {
      return null;
   }

   @Override
   public SoundEvent getInterruptSoundFile() {
      return null;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.STRENGTHENING
      );
   }
}
