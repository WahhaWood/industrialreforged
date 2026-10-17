package com.industrialreforged.tech.menu.tool;

import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.item.tool.FilterData;
import com.industrialreforged.tech.item.tool.ItemFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

/** Меню фильтра: 48 образцов (8x6) + переключатель белый/чёрный список. */
public class FilterMenu extends AbstractContainerMenu {
   public static final MenuType<FilterMenu> TYPE = IMenuTypeExtension.create(
      (containerId, inventory, buffer) -> new FilterMenu(containerId, inventory, buffer.readEnum(InteractionHand.class))
   );
   /** В фильтр можно класть только руды. */
   public static final TagKey<Item> ORES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores"));

   private final Player player;
   private final InteractionHand hand;
   private final SimpleContainer container;
   public boolean blacklist;

   /** Конструктор для сети (клиент и сервер из фабрики). */
   public FilterMenu(int containerId, Inventory inventory, InteractionHand hand) {
      super(TYPE, containerId);
      this.player = inventory.player;
      this.hand = hand;
      FilterData data = this.stack().getOrDefault(IRDataComponents.ITEM_FILTER.get(), FilterData.EMPTY);
      this.blacklist = data.blacklist();
      this.container = new SimpleContainer(ItemFilter.SIZE) {
         @Override
         public void setChanged() {
            super.setChanged();
            FilterMenu.this.save();
         }
      };
      List<ItemStack> samples = new ArrayList<>(Collections.nCopies(ItemFilter.SIZE, ItemStack.EMPTY));

      int sampleIndex = 0;

      for (int i = 0; i < data.items().size() && sampleIndex < ItemFilter.SIZE; i++) {
         ItemStack sample = data.items().get(i).copyWithCount(1);
         if (sample.isEmpty()) {
            continue;
         }

         boolean duplicate = false;

         for (int j = 0; j < sampleIndex; j++) {
            if (ItemStack.isSameItem(samples.get(j), sample)) {
               duplicate = true;
               break;
            }
         }

         if (!duplicate) {
            samples.set(sampleIndex++, sample);
         }
      }

      for (int i = 0; i < ItemFilter.SIZE; i++) {
         this.container.setItem(i, samples.get(i));
      }

      for (int row = 0; row < 6; row++) {
         for (int col = 0; col < 8; col++) {
            this.addSlot(new FilterMenu.SampleSlot(this.container, col + row * 8, 16 + col * 18, 18 + row * 18));
         }
      }

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 144 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.addSlot(new Slot(inventory, col, 8 + col * 18, 202));
      }

      this.addDataSlot(new DataSlot() {
         @Override
         public int get() {
            return FilterMenu.this.blacklist ? 1 : 0;
         }

         @Override
         public void set(int value) {
            FilterMenu.this.blacklist = value == 1;
         }
      });
   }

   /** Слот образца: только руда и не больше одной штуки. */
   private static class SampleSlot extends Slot {
      SampleSlot(net.minecraft.world.Container container, int index, int x, int y) {
         super(container, index, x, y);
      }

      @Override
      public int getMaxStackSize() {
         return 1;
      }

      @Override
      public boolean mayPlace(ItemStack stack) {
         if (!stack.is(FilterMenu.ORES)) {
            return false;
         }

         // одна и та же руда не может лежать дважды
         for (int i = 0; i < this.container.getContainerSize(); i++) {
            ItemStack other = this.container.getItem(i);
            if (!other.isEmpty() && ItemStack.isSameItem(other, stack)) {
               return false;
            }
         }

         return true;
      }
   }

   private ItemStack stack() {
      return this.player.getItemInHand(this.hand);
   }

   private void save() {
      if (this.player.level().isClientSide) {
         return;
      }

      List<ItemStack> items = new ArrayList<>();

      for (int i = 0; i < ItemFilter.SIZE; i++) {
         ItemStack sample = this.container.getItem(i);
         items.add(sample.isEmpty() ? sample : sample.copyWithCount(1));
      }

      this.stack().set(IRDataComponents.ITEM_FILTER.get(), new FilterData(items, this.blacklist));
   }

   @Override
   public boolean clickMenuButton(Player player, int id) {
      if (id == 0) {
         this.blacklist = !this.blacklist;
         this.save();
         this.broadcastChanges();
         return true;
      }

      return false;
   }

   @Override
   public ItemStack quickMoveStack(Player player, int index) {
      Slot slot = this.slots.get(index);
      if (!slot.hasItem()) {
         return ItemStack.EMPTY;
      }

      ItemStack stack = slot.getItem();
      ItemStack copy = stack.copy();

      if (index < ItemFilter.SIZE) {
         if (!this.moveItemStackTo(stack, ItemFilter.SIZE, this.slots.size(), true)) {
            return ItemStack.EMPTY;
         }
      } else if (!this.moveItemStackTo(stack, 0, ItemFilter.SIZE, false)) {
         return ItemStack.EMPTY;
      }

      if (stack.isEmpty()) {
         slot.setByPlayer(ItemStack.EMPTY);
      } else {
         slot.setChanged();
      }

      return copy;
   }

   @Override
   public boolean stillValid(Player player) {
      return player.isAlive() && player.getItemInHand(this.hand).is(this.stack().getItem());
   }
}
