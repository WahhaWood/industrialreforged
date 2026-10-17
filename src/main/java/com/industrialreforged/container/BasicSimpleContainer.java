package com.industrialreforged.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.wrapper.InputInvWrapper;
import com.industrialreforged.container.wrapper.OutputInvWrapper;
import com.industrialreforged.utils.ContainerUtils;
import java.util.Collection;
import java.util.function.BiPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class BasicSimpleContainer extends SimpleContainer {
   private final String name;
   private int stackLimit;
   protected final BaseBlockEntity entity;
   protected boolean canInput = true;
   protected boolean canOutput = true;
   private boolean loadingFromTag;
   protected BiPredicate<Integer, ItemStack> predicate;

   @Deprecated
   public BasicSimpleContainer(BaseBlockEntity entity, int size) {
      this(entity, size, 64);
   }

   @Deprecated
   public BasicSimpleContainer(BaseBlockEntity entity, int size, int maxStackSize) {
      this(entity, "contents", size, maxStackSize);
   }

   @Deprecated
   public BasicSimpleContainer(BaseBlockEntity entity, String invName, int size) {
      this(entity, invName, size, 64);
   }

   @Deprecated
   public BasicSimpleContainer(BaseBlockEntity entity, String invName, int size, int stackLimit) {
      this(entity, invName, size, stackLimit, null);
   }

   public BasicSimpleContainer(BaseBlockEntity entity, String invName, int size, int stackLimit, @Nullable BiPredicate<Integer, ItemStack> predicate) {
      super(size);
      this.name = invName;
      this.stackLimit = stackLimit;
      this.entity = entity;
      this.predicate = predicate;
      this.addListener(container -> entity.markForSave());
      if (entity != null) {
         entity.registerDropContainer(this);
      }
   }

   @Nullable
   public IItemHandlerModifiable createWrapper() {
      if (this.canInput && this.canOutput) {
         return new InvWrapper(this);
      } else if (this.canInput) {
         return new InputInvWrapper(this);
      } else {
         return this.canOutput ? new OutputInvWrapper(this) : com.industrialreforged.container.wrapper.EmptyInvWrapper.INSTANCE;
      }
   }

   @Nullable
   public IItemHandlerModifiable createWrapper(@Nullable Direction direction) {
      return this.createWrapper();
   }

   public void clearListener() {
      this.listeners = null;
   }

   public void setItem(int index, ItemStack stack) {
      if (this.loadingFromTag) {
         this.getItems().set(index, stack);
      } else {
         super.setItem(index, stack);
      }
   }

   public boolean canPlaceItem(int slotId, ItemStack stack) {
      return this.canInput && (this.predicate == null || this.predicate.test(slotId, stack));
   }

   public int getMaxStackSize() {
      return this.stackLimit;
   }

   public boolean stillValid(Player player) {
      return !this.entity.isRemoved() && player.distanceToSqr(this.entity.getBlockPos().getCenter()) < 64.0;
   }

   public void setMaxStackSize(int value) {
      this.stackLimit = value;
   }

   public void setCanInput(boolean canInput) {
      this.canInput = canInput;
   }

   public void setCanOutput(boolean canOutput) {
      this.canOutput = canOutput;
   }

   public boolean isEmpty(int slot) {
      return this.getItem(slot).isEmpty();
   }

   public ItemStack getItem() {
      return this.getItem(0);
   }

   public void setItem(ItemStack itemStack) {
      this.setItem(0, itemStack);
   }

   public ItemStack add(ItemStack itemStack) {
      return this.addItem(itemStack);
   }

   public void add(@Nullable Collection<ItemStack> collection) {
      if (collection != null && !collection.isEmpty()) {
         collection.forEach(this::add);
      }
   }

   public ItemStack add(ItemStack stack, int slotId) {
      return this.add(stack, slotId, slotId + 1);
   }

   public ItemStack add(ItemStack stack, int includeId, int excludeId) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      }

      ItemStack itemstack = stack.copy();
      this.moveItemToOccupiedSlotsWithSameType(itemstack, includeId, excludeId);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      }

      this.moveItemToEmptySlots(itemstack, includeId, excludeId);
      return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
   }

   public boolean simulationAdd(ItemStack insert) {
      return this.canAddItem(insert);
   }

   public boolean simulationAdd(@Nullable Collection<ItemStack> collection) {
      if (collection != null && !collection.isEmpty()) {
         int size = this.getContainerSize();
         BasicSimpleContainer.TestItem[] copy = new BasicSimpleContainer.TestItem[size];

         for (int i = 0; i < size; i++) {
            copy[i] = BasicSimpleContainer.TestItem.of(this.getItem(i));
         }

         label29:
         for (ItemStack stack : collection) {
            for (int i = 0; i < copy.length; i++) {
               BasicSimpleContainer.TestItem s = copy[i];
               if (s.matches(stack)) {
                  copy[i] = s.appendCount(stack);
                  continue label29;
               }
            }

            return false;
         }

         return true;
      } else {
         return true;
      }
   }

   public boolean simulationAdd(ItemStack stack, int index) {
      return this.simulationAdd(stack, index, index + 1);
   }

   public boolean simulationAdd(ItemStack stack, int includeId, int excludeId) {
      boolean flag = false;

      for (int i = includeId; i < excludeId; i++) {
         ItemStack itemStack = this.getItem(i);
         if (itemStack.isEmpty() || ItemStack.isSameItemSameComponents(itemStack, stack) && itemStack.getCount() < itemStack.getMaxStackSize()) {
            flag = true;
            break;
         }
      }

      return flag;
   }

   public void shrink() {
      this.shrink(0, 1);
   }

   public void shrink(int index) {
      this.shrink(index, 1);
   }

   public void shrink(int index, int count) {
      this.removeItem(index, count);
   }

   public void clearContent(int index) {
      this.setItem(index, ItemStack.EMPTY);
   }

   public void load(CompoundTag tag, Provider registries) {
      this.loadingFromTag = true;
      this.clearContent();
      ContainerUtils.loadAllItems(tag, this, this.name, registries);
      this.loadingFromTag = false;
      this.setChanged();
   }

   public void save(CompoundTag tag, Provider registries) {
      ContainerUtils.saveAllItems(tag, this, this.name, registries);
   }

   private void moveItemToEmptySlots(ItemStack stack, int include, int exclude) {
      for (int i = include; i < exclude; i++) {
         ItemStack itemstack = this.getItem(i);
         if (itemstack.isEmpty()) {
            this.setItem(i, stack.copyAndClear());
            return;
         }
      }
   }

   private void moveItemToOccupiedSlotsWithSameType(ItemStack stack, int include, int exclude) {
      for (int i = include; i < exclude; i++) {
         ItemStack itemstack = this.getItem(i);
         if (ItemStack.isSameItemSameComponents(itemstack, stack)) {
            this.moveItemsBetweenStacks(stack, itemstack);
            if (stack.isEmpty()) {
               return;
            }
         }
      }
   }

   private static class TestItem {
      static final BasicSimpleContainer.TestItem EMPTY = new BasicSimpleContainer.TestItem(ItemStack.EMPTY);
      ItemStack stack;
      int count;

      TestItem(ItemStack stack) {
         this.stack = stack;
         this.count = stack.getCount();
      }

      boolean matches(ItemStack stack) {
         return this.stack.isEmpty() || ItemStack.isSameItemSameComponents(this.stack, stack) && this.count + stack.getCount() <= stack.getMaxStackSize();
      }

      BasicSimpleContainer.TestItem appendCount(ItemStack stack) {
         if (this.stack.isEmpty()) {
            return of(stack);
         }

         this.count = this.count + stack.getCount();
         return this;
      }

      static BasicSimpleContainer.TestItem of(ItemStack stack) {
         return stack.isEmpty() ? EMPTY : new BasicSimpleContainer.TestItem(stack);
      }
   }
}
