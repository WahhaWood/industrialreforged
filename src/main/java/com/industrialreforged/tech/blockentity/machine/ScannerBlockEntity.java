package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.tech.api.recipe.IPatternStorage;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotScannable;
import com.industrialreforged.tech.core.recipe.manager.UuRecipeManager;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.item.ItemCrystalMemory;
import com.industrialreforged.tech.menu.machine.ScannerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class ScannerBlockEntity extends AbstractElectricMachineBlockEntity implements ContainerData, IContainerWrapper {
   private ItemStack currentStack = ItemStack.EMPTY;
   private ItemStack pattern = ItemStack.EMPTY;
   public int progress = 0;
   public final InvSlotConsumable inputSlot;
   public final BasicSimpleContainer diskSlot;
   private ScannerBlockEntity.State state = ScannerBlockEntity.State.IDLE;
   public int patternUu;

   public ScannerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(2048000, 8192, IRBlockEntities.UU_SCANNER.get(), blockPos, blockState);
      this.inputSlot = new InvSlotScannable(this, "input", 1);
      this.diskSlot = new InvSlotConsumableItem(this, "disk", 1, IRItems.CRYSTAL_MEMORY);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACHINE_massfabloop.get();
   }

   @Override
   protected boolean hasWork() {
      return this.progress > 0;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputSlot.load(tag, registries);
      this.diskSlot.load(tag, registries);
      this.progress = tag.getInt("progress");
      this.currentStack = ItemStack.parseOptional(registries, tag.getCompound("currentStack"));
      this.pattern = ItemStack.parseOptional(registries, tag.getCompound("pattern"));
      int n = tag.getInt("state");
      this.state = n < ScannerBlockEntity.State.values().length ? ScannerBlockEntity.State.values()[n] : ScannerBlockEntity.State.IDLE;
      this.refreshInfo();
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputSlot.save(tag, registries);
      this.diskSlot.save(tag, registries);
      tag.putInt("progress", this.progress);
      if (!this.currentStack.isEmpty()) {
         tag.put("currentStack", this.currentStack.save(registries));
      }

      if (!this.pattern.isEmpty()) {
         tag.put("pattern", this.pattern.save(registries));
      }

      tag.putInt("state", this.state.ordinal());
   }

   private static final int ENERGY_PER_TICK = 256;
   private static final int MAX_PROGRESS = 100;

   /** Сканирует предмет из входного слота, тратя энергию, и выдаёт шаблон для сохранения. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      if (this.state == State.COMPLETED) {
         return;
      }

      if (this.progress >= MAX_PROGRESS) {
         if (this.pattern.isEmpty()) {
            this.state = State.IDLE;
            this.progress = 0;
         }

         return;
      }

      ItemStack input = this.inputSlot.getItem(0);
      if (input.isEmpty() || (!this.currentStack.isEmpty() && !StackUtil.checkItemEquality(this.currentStack, input))) {
         this.reset();
         return;
      }

      if (this.getPatternStorage() == null && this.diskSlot.isEmpty()) {
         this.reset();
         this.state = State.NO_STORAGE;
         return;
      }

      if (this.energy.getStored() < ENERGY_PER_TICK) {
         this.state = State.NO_ENERGY;
         return;
      }

      if (this.currentStack.isEmpty()) {
         this.currentStack = input.copyWithCount(1);
      }

      if (UuRecipeManager.instance.getCost(this.level, this.currentStack) <= 0) {
         this.reset();
         this.state = State.FAILED;
         return;
      }

      if (this.isPatternRecorded(this.currentStack)) {
         this.reset();
         this.state = State.ALREADY_RECORDED;
         return;
      }

      this.state = State.SCANNING;
      this.energy.extractForceEnergy(ENERGY_PER_TICK);
      this.progress++;
      if (this.progress >= MAX_PROGRESS) {
         this.pattern = this.currentStack.copy();
         this.refreshInfo();
         this.state = State.COMPLETED;
         this.inputSlot.shrink(0);
      }
   }

   public void reset() {
      this.progress = 0;
      this.currentStack = ItemStack.EMPTY;
      this.pattern = ItemStack.EMPTY;
      this.state = State.IDLE;
   }

   private boolean isPatternRecorded(ItemStack itemStack) {
      ItemStack stack;
      if (!this.diskSlot.isEmpty()
         && this.diskSlot.getItem().getItem() instanceof ItemCrystalMemory
         && StackUtil.checkItemEquality(((ItemCrystalMemory)(stack = this.diskSlot.getItem()).getItem()).getPattern(stack), itemStack)) {
         return true;
      }

      IPatternStorage iPatternStorage = this.getPatternStorage();
      if (iPatternStorage == null) {
         return false;
      }

      for (ItemStack itemStack2 : iPatternStorage.getPatterns()) {
         if (StackUtil.checkItemEquality(itemStack2, itemStack)) {
            return true;
         }
      }

      return false;
   }

   private void record() {
      if (!this.pattern.isEmpty() && this.patternUu > 0) {
         if (!this.savetoDisk(this.pattern)) {
            IPatternStorage iPatternStorage = this.getPatternStorage();
            if (iPatternStorage == null) {
               this.state = ScannerBlockEntity.State.TRANSFER_ERROR;
               return;
            }

            if (!iPatternStorage.addPattern(this.pattern)) {
               this.state = ScannerBlockEntity.State.TRANSFER_ERROR;
               return;
            }
         }

         this.reset();
      } else {
         this.reset();
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new ScannerMenu(containerId, playerInventory, this);
   }

   @Nullable
   public IPatternStorage getPatternStorage() {
      Level level = this.getLevel();

      for (Direction direction : Util.ALL_DIRS) {
         BlockEntity blockEntity = level.getBlockEntity(this.worldPosition.relative(direction));
         if (blockEntity instanceof IPatternStorage) {
            return (IPatternStorage)blockEntity;
         }
      }

      return null;
   }

   public boolean savetoDisk(ItemStack itemStack) {
      if (this.diskSlot.isEmpty() || itemStack == null) {
         return false;
      } else if (this.diskSlot.getItem().getItem() instanceof ItemCrystalMemory) {
         ItemStack itemStack2 = this.diskSlot.getItem();
         ((ItemCrystalMemory)itemStack2.getItem()).setPattern(itemStack2, itemStack);
         return true;
      } else {
         return false;
      }
   }

   public void onNetworkEvent(int buttonId) {
      switch (buttonId) {
         case 0:
            this.reset();
            break;
         case 1:
            if (this.progress >= 100) {
               this.record();
            }
      }
   }

   private void refreshInfo() {
      if (!this.pattern.isEmpty()) {
         this.patternUu = UuRecipeManager.instance.getCost(this.level, this.pattern);
      }
   }

   public int getPercentageDone() {
      return 100 * this.progress / 100;
   }

   public int getSubPercentageDoneScaled(int n) {
      return (int)(n * (this.progress / 100.0F));
   }

   public boolean isDone() {
      return this.progress >= 100;
   }

   public ScannerBlockEntity.State getState() {
      return this.state;
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> this.state.ordinal();
         case 1 -> this.progress;
         case 2 -> this.patternUu;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.state = ScannerBlockEntity.State.values()[value];
            break;
         case 1:
            this.progress = value;
            break;
         case 2:
            this.patternUu = value;
      }
   }

   public int getCount() {
      return 3;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         return new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.diskSlot.createWrapper()});
      }

      return switch (direction) {
         case UP -> this.inputSlot.createWrapper();
         case DOWN -> this.diskSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.diskSlot.createWrapper()}
         );
      };
   }

   public enum State {
      IDLE,
      SCANNING,
      COMPLETED,
      FAILED,
      NO_STORAGE,
      NO_ENERGY,
      TRANSFER_ERROR,
      ALREADY_RECORDED;

      public boolean isAllowDelete() {
         return this.isAllowSave() || this == FAILED;
      }

      public boolean isAllowSave() {
         return this == COMPLETED || this == TRANSFER_ERROR;
      }
   }
}
