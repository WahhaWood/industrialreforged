package com.industrialreforged.utils;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class ContainerUtils {
   public static void saveAllItems(CompoundTag tag, Container items, Provider registries) {
      saveAllItems(tag, items, "Items", registries);
   }

   public static void saveAllItems(CompoundTag tag, Container container, String name, Provider registries) {
      saveAllItems(tag, name, registries, container.getContainerSize(), container::getItem);
   }

   public static void saveAllItems(CompoundTag tag, NonNullList<ItemStack> list, String name, Provider registries) {
      saveAllItems(tag, name, registries, list.size(), list::get);
   }

   public static void loadAllItems(CompoundTag tag, Container container, Provider registries) {
      loadAllItems(tag, container, "Items", registries);
   }

   public static void loadAllItems(CompoundTag tag, Container container, String name, Provider registries) {
      loadAllItems(tag, name, registries, container.getContainerSize(), container::setItem);
   }

   public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> list, String name, Provider registries) {
      loadAllItems(tag, name, registries, list.size(), list::set);
   }

   public static void loadAllItems(CompoundTag tag, String name, Provider registries, BiConsumer<Integer, ItemStack> consumer) {
      ListTag tags = tag.getList(name, 10);
      loadAllItems(tag, name, registries, tags.size(), consumer);
   }

   @NotNull
   public static ItemStack transferItem(BlockEntity entity, @NotNull ItemStack input, boolean simulate) {
      if (input.isEmpty()) {
         return ItemStack.EMPTY;
      }

      Level level = entity.getLevel();
      if (level == null) {
         return input;
      }

      BlockPos blockPos = entity.getBlockPos();

      for (Direction direction : Direction.values()) {
         input = transferItem(level, blockPos, input, direction, simulate);
         if (input.isEmpty()) {
            break;
         }
      }

      return input;
   }

   @NotNull
   public static ItemStack transferItem(BlockEntity entity, @NotNull ItemStack input, Direction direction, boolean simulate) {
      if (input.isEmpty()) {
         return ItemStack.EMPTY;
      }

      Level level = entity.getLevel();
      return level == null ? input : transferItem(level, entity.getBlockPos(), input, direction, simulate);
   }

   public static <T extends BlockEntity & Container> void transferItems(T t, int slotId) {
      transferItems(t, slotId, slotId + 1);
   }

   public static <T extends BlockEntity & Container> void transferItems(T t, int includeId, int excludeId) {
      Level level = t.getLevel();
      if (level != null) {
         transferItems(level, t.getBlockPos(), t, includeId, excludeId);
      }
   }

   public static void transferItems(@NotNull BlockEntity entity, @NotNull Container container) {
      for (Direction direction : Direction.values()) {
         transferItems(entity, container, direction);
      }
   }

   public static void transferItems(@NotNull BlockEntity entity, @NotNull Container container, Direction direction) {
      transferItems(entity, container, 0, container.getContainerSize(), direction);
   }

   public static void transferItems(@NotNull BlockEntity entity, @NotNull Container container, int includeId, int excludeId) {
      for (Direction direction : Direction.values()) {
         transferItems(entity, container, includeId, excludeId, direction);
      }
   }

   public static void transferItems(@NotNull BlockEntity entity, @NotNull Container container, int includeId, int excludeId, Direction direction) {
      transferItems(entity.getLevel(), entity.getBlockPos(), container, includeId, excludeId, direction);
   }

   public static void transferItems(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Container container) {
      for (Direction direction : Direction.values()) {
         transferItems(level, blockPos, container, direction);
      }
   }

   public static void transferItems(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Container container, Direction direction) {
      transferItems(level, blockPos, container, 0, container.getContainerSize(), direction);
   }

   public static void transferItems(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Container container, int includeId, int excludeId) {
      for (Direction direction : Direction.values()) {
         transferItems(level, blockPos, container, includeId, excludeId, direction);
      }
   }

   public static void transferItems(
      @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Container container, int includeId, int excludeId, Direction direction
   ) {
      IItemHandler handler = getItemHandler(level, blockPos.relative(direction), direction.getOpposite());
      if (handler != null) {
         for (int i = includeId; i < excludeId; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
               int countStack = stack.getCount();

               for (int j = 0; j < handler.getSlots(); j++) {
                  stack = handler.insertItem(j, stack, false);
                  if (stack.isEmpty()) {
                     break;
                  }
               }

               if (countStack != stack.getCount()) {
                  container.setItem(i, stack);
               }
            }
         }
      }
   }

   public static void transferItems(BlockEntity entity, @NotNull List<ItemStack> stacks) {
      Level level = entity.getLevel();
      if (level != null) {
         BlockPos blockPos = entity.getBlockPos();

         for (Direction direction : Direction.values()) {
            if (stacks.isEmpty()) {
               return;
            }

            IItemHandler handler = getItemHandler(level, blockPos.relative(direction), direction.getOpposite());
            if (handler != null) {
               Iterator<ItemStack> iterator = stacks.iterator();

               while (iterator.hasNext()) {
                  ItemStack stack = iterator.next();
                  ItemStack rejected = ItemHandlerHelper.insertItem(handler, stack, false);
                  if (rejected.isEmpty()) {
                     iterator.remove();
                  } else {
                     stack.setCount(rejected.getCount());
                  }
               }
            }
         }
      }
   }

   @NotNull
   private static ItemStack transferItem(
      @NotNull Level level, @NotNull BlockPos blockPos, @NotNull ItemStack input, @NotNull Direction direction, boolean simulate
   ) {
      IItemHandler handler = getItemHandler(level, blockPos.relative(direction), direction.getOpposite());
      if (handler == null) {
         return input;
      }

      for (int i = 0; i < handler.getSlots(); i++) {
         input = handler.insertItem(i, input, simulate);
         if (input.isEmpty()) {
            break;
         }
      }

      return input;
   }

   private static IItemHandler getItemHandler(Level level, BlockPos blockPos, Direction direction) {
      return (IItemHandler)level.getCapability(ItemHandler.BLOCK, blockPos, direction.getOpposite());
   }

   private static void saveAllItems(CompoundTag tag, String name, Provider registries, int limit, IntFunction<ItemStack> function) {
      ListTag tags = new ListTag();

      for (int i = 0; i < limit; i++) {
         ItemStack itemstack = function.apply(i);
         if (!itemstack.isEmpty()) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putByte("Slot", (byte)i);
            tags.add(itemstack.save(registries, compoundtag));
         }
      }

      tag.put(name, tags);
   }

   private static void loadAllItems(CompoundTag tag, String name, Provider registries, int limit, BiConsumer<Integer, ItemStack> consumer) {
      ListTag tags = tag.getList(name, 10);

      for (int i = 0; i < tags.size(); i++) {
         CompoundTag compoundtag = tags.getCompound(i);
         int j = compoundtag.getByte("Slot") & 255;
         if (j < limit) {
            consumer.accept(j, ItemStack.parse(registries, compoundtag).orElse(ItemStack.EMPTY));
         }
      }
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, int size) {
      return simple(entity, size, 64);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, int size, int stackLimit) {
      return simple(entity, "contents", size, stackLimit);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, String invName, int size) {
      return simple(entity, invName, size, 64);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, String invName, int size, int stackLimit) {
      return new BasicSimpleContainer(entity, invName, size, stackLimit, null);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, int size, Predicate<ItemStack> predicate) {
      return simple(entity, size, 64, predicate);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, int size, int stackLimit, Predicate<ItemStack> predicate) {
      return simple(entity, "contents", size, stackLimit, predicate);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, String invName, int size, Predicate<ItemStack> predicate) {
      return simple(entity, invName, size, 64, predicate);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, String invName, int size, int stackLimit, Predicate<ItemStack> predicate) {
      return new BasicSimpleContainer(entity, invName, size, stackLimit, (integer, stack) -> predicate.test(stack));
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, int size, BiPredicate<Integer, ItemStack> predicate) {
      return simple(entity, size, 64, predicate);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, int size, int stackLimit, BiPredicate<Integer, ItemStack> predicate) {
      return simple(entity, "contents", size, stackLimit, predicate);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, String invName, int size, BiPredicate<Integer, ItemStack> predicate) {
      return simple(entity, invName, size, 64, predicate);
   }

   public static BasicSimpleContainer simple(BaseBlockEntity entity, String invName, int size, int stackLimit, BiPredicate<Integer, ItemStack> predicate) {
      return new BasicSimpleContainer(entity, invName, size, stackLimit, predicate);
   }
}
