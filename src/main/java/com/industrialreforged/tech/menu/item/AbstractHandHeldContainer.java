package com.industrialreforged.tech.menu.item;

import java.util.function.Consumer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHandHeldContainer implements Container {
   protected final String key;
   protected ItemStack container;
   protected final NonNullList<ItemStack> inv;
   protected final int[] counts;
   public final Player player;

   public AbstractHandHeldContainer(Inventory inv, int slots) {
      this(inv, slots, "inv");
   }

   public AbstractHandHeldContainer(Inventory inv, int slots, String key) {
      this.key = key;
      this.container = inv.getSelected();
      this.inv = NonNullList.withSize(slots, ItemStack.EMPTY);
      this.counts = new int[slots];
      this.player = inv.player;
      CompoundTag compound = this.getCompoundInv();

      for (int i = 0; i < slots; i++) {
         if (compound.contains("@" + i, 10)) {
            ItemStack stack = ItemStack.parseOptional(this.player.registryAccess(), compound.getCompound("@" + i));
            this.inv.set(i, stack);
            this.counts[i] = stack.getCount();
         }
      }
   }

   public int getContainerSize() {
      return this.inv.size();
   }

   public boolean isEmpty() {
      return this.inv.stream().allMatch(ItemStack::isEmpty);
   }

   @NotNull
   public ItemStack getItem(int index) {
      return (ItemStack)this.inv.get(index);
   }

   @NotNull
   public ItemStack removeItem(int index, int count) {
      ItemStack stack = this.getItem(index);
      if (!stack.isEmpty() && count > 0) {
         ItemStack result = stack.split(count);
         this.changeSlot(index);
         return result;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int index, @NotNull ItemStack stack) {
      this.inv.set(index, stack);
      this.changeSlot(index);
   }

   public int getMaxStackSize() {
      return 64;
   }

   public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
      return false;
   }

   public void setChanged() {
      for (int i = 0; i < this.inv.size(); i++) {
         if (((ItemStack)this.inv.get(i)).getCount() != this.counts[i]) {
            this.changeSlot(i);
         }
      }
   }

   public boolean stillValid(@NotNull Player player) {
      return this.container == player.getMainHandItem();
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int index) {
      ItemStack stack = this.getItem(index);
      if (!stack.isEmpty()) {
         ItemStack result = (ItemStack)this.inv.set(index, ItemStack.EMPTY);
         this.changeSlot(index);
         return result;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void clearContent() {
      this.inv.clear();
      this.update(tag -> tag.remove(this.key));
   }

   private void changeSlot(int slotId) {
      ItemStack stack = this.getItem(slotId);
      this.update(tag -> {
         CompoundTag compound = tag.getCompound(this.key);
         if (stack.isEmpty()) {
            compound.remove("@" + slotId);
         } else {
            compound.put("@" + slotId, stack.save(this.player.registryAccess()));
         }

         tag.put(this.key, compound);
      });
      this.counts[slotId] = stack.getCount();
   }

   private CompoundTag getCompoundInv() {
      return this.getCompoundInv(this.key);
   }

   private CompoundTag getCompoundInv(String key) {
      return ((CustomData)this.container.get(DataComponents.CUSTOM_DATA)).getUnsafe().getCompound(key);
   }

   private void update(Consumer<CompoundTag> consumer) {
      this.container.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, o -> o.update(consumer));
   }
}
