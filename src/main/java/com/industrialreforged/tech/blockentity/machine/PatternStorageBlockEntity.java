package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.utils.ContainerUtils;
import com.industrialreforged.tech.api.recipe.IPatternStorage;
import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.core.recipe.manager.UuRecipeManager;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ItemCrystalMemory;
import com.industrialreforged.tech.menu.machine.PatternStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PatternStorageBlockEntity extends AbstractMachineBlockEntity implements IPatternStorage, ContainerData {
   private final NonNullList<ItemStack> patterns = NonNullList.create();
   public final InvSlotConsumableItem diskSlot;
   public int index = 0;
   public int maxIndex;
   public ItemStack pattern = ItemStack.EMPTY;
   public int patternUu;

   public PatternStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.PATTERN_STORAGE.get(), blockPos, blockState);
      this.diskSlot = new InvSlotConsumableItem(this, "crystal", 1, IRItems.CRYSTAL_MEMORY);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.diskSlot.load(tag, registries);
      ContainerUtils.loadAllItems(tag, "patterns", registries, (integer, stack) -> this.addPattern(stack));
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.diskSlot.save(tag, registries);
      ContainerUtils.saveAllItems(tag, this.patterns, "patterns", registries);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new PatternStorageMenu(containerId, playerInventory, this);
   }

   public void onNetworkEvent(int n) {
      switch (n) {
         case 0:
            if (!this.patterns.isEmpty()) {
               this.index = this.index <= 0 ? this.patterns.size() - 1 : --this.index;
               this.refreshInfo();
            }
            break;
         case 1:
            if (!this.patterns.isEmpty()) {
               this.index = this.index >= this.patterns.size() - 1 ? 0 : ++this.index;
               this.refreshInfo();
            }
            break;
         case 2:
            ItemStack itemStackx;
            if (this.index >= 0
               && this.index < this.patterns.size()
               && !this.diskSlot.isEmpty()
               && (itemStackx = this.diskSlot.getItem()).getItem() instanceof ItemCrystalMemory) {
               ((ItemCrystalMemory)itemStackx.getItem()).setPattern(itemStackx, (ItemStack)this.patterns.get(this.index));
            }
            break;
         case 3:
            ItemStack itemStack;
            ItemStack itemStack2;
            if (!this.diskSlot.isEmpty()
               && (itemStack2 = this.diskSlot.getItem()).getItem() instanceof ItemCrystalMemory
               && (itemStack = ((ItemCrystalMemory)itemStack2.getItem()).getPattern(itemStack2)) != null) {
               this.addPattern(itemStack);
            }
      }
   }

   public void refreshInfo() {
      if (this.index < 0 || this.index >= this.patterns.size()) {
         this.index = 0;
      }

      this.maxIndex = this.patterns.size();
      if (this.patterns.isEmpty()) {
         this.pattern = ItemStack.EMPTY;
      } else {
         this.pattern = (ItemStack)this.patterns.get(this.index);
         this.patternUu = UuRecipeManager.instance.getCost(this.level, this.pattern);
      }
   }

   @Override
   public boolean addPattern(ItemStack value) {
      if (value.isEmpty()) {
         return false;
      }

      for (ItemStack stack : this.patterns) {
         if (ItemStack.isSameItemSameComponents(stack, value)) {
            return false;
         }
      }

      this.patterns.add(value);
      this.refreshInfo();
      return true;
   }

   @Override
   public NonNullList<ItemStack> getPatterns() {
      return this.patterns;
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> this.index;
         case 1 -> this.maxIndex;
         case 2 -> this.patternUu;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.index = value;
            break;
         case 1:
            this.maxIndex = value;
            break;
         case 2:
            this.patternUu = value;
      }
   }

   public int getCount() {
      return 3;
   }

   private int refreshCooldown = 20;

   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      if (--this.refreshCooldown <= 0) {
         this.refreshCooldown = 20;
         this.refreshInfo();
      }
   }
}
