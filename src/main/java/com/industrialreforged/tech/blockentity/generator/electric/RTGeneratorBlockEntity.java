package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.tech.blockentity.base.AbstractElectricGeneratorBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.menu.generator.electric.RTGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class RTGeneratorBlockEntity extends AbstractElectricGeneratorBlockEntity {
   public final InvSlotConsumable input = new InvSlotConsumableItem(this, "fuel", 6, IRItems.URANIUM_PELLET, IRItems.MOX_PELLET, IRItems.RTG_PELLET);

   public RTGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.RT_GENERATOR.get(), blockPos, blockState, 1280000L, 16L);
      this.input.setMaxStackSize(1);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.input.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.input.save(tag, registries);
   }

   @Override
   public boolean needsFuel() {
      return false;
   }

   protected boolean delayActiveUpdate() {
      return true;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new RTGeneratorMenu(containerId, playerInventory, this);
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.input.createWrapper(), this.container.createWrapper()});
         }
         return this.wrapper;
      }

      return switch (direction) {
         case DOWN -> this.container.createWrapper();
         case UP, NORTH, SOUTH, EAST, WEST -> this.input.createWrapper();
      };
   }
}
