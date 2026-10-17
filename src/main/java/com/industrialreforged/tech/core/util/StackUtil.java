package com.industrialreforged.tech.core.util;

import com.industrialreforged.tech.container.InventoryDynamic;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class StackUtil {
   static final Set<DataComponentType<?>> ignoredNbtKeys = Set.of(DataComponents.DAMAGE);

   public static boolean isEmpty(Player player, InteractionHand interactionHand) {
      return player.getItemInHand(interactionHand).isEmpty();
   }

   public static boolean checkItemEquality(ItemStack value, ItemStack filter) {
      return value.isEmpty() && filter.isEmpty()
         || !value.isEmpty() && !filter.isEmpty() && value.getItem() == filter.getItem() && checkNbtEquality(value, filter);
   }

   public static boolean checkItemEquality(ItemStack value, ItemLike filter) {
      return value.isEmpty() ? filter == null : filter != null && value.is(filter.asItem());
   }

   public static boolean checkItemEqualityStrict(ItemStack itemStack, ItemStack itemStack2) {
      return itemStack.isEmpty() && itemStack2.isEmpty()
         || !itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.is(itemStack2.getItem()) && checkNbtEqualityStrict(itemStack, itemStack2);
   }

   private static boolean checkNbtEquality(ItemStack itemStack, ItemStack itemStack2) {
      return ItemStack.isSameItemSameComponents(itemStack, itemStack2);
   }

   public static boolean checkNbtEqualityStrict(ItemStack itemStack, ItemStack itemStack2) {
      return ItemStack.isSameItemSameComponents(itemStack, itemStack2);
   }

   public static boolean consume(Player player, InteractionHand interactionHand, ItemLike filter, int n) {
      if (n <= 0) {
         throw new IllegalArgumentException("negative/zero amount");
      }

      ItemStack itemHand = get(player, interactionHand);
      if (!itemHand.isEmpty() && (filter == null || itemHand.is(filter.asItem()))) {
         if (player.getAbilities().instabuild) {
            return true;
         }

         int count = itemHand.getCount();
         if (count < n) {
            return false;
         }

         itemHand.shrink(n);
         return true;
      } else {
         return false;
      }
   }

   public static void consumeOrError(Player player, InteractionHand hand, int amount) {
      consumeOrError(player, hand, null, amount);
   }

   public static void consumeOrError(Player player, InteractionHand hand, ItemLike filter, int amount) {
      if (!consume(player, hand, filter, amount)) {
         throw new IllegalStateException("consume failed");
      }
   }

   public static boolean damage(Player player, InteractionHand hand, ItemLike filter, int amount) {
      if (amount <= 0) {
         throw new IllegalArgumentException("negative/zero amount");
      }

      ItemStack itemHand = get(player, hand);
      if (!itemHand.isEmpty() && itemHand.getMaxDamage() > 0 && (filter == null || itemHand.is(filter.asItem()))) {
         if (!player.getAbilities().instabuild && itemHand.isDamageableItem()) {
            itemHand.hurtAndBreak(amount, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            if (itemHand.isEmpty()) {
               clear(player, hand);
            } else {
               set(player, hand, itemHand);
            }

            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static ItemStack get(Player player, InteractionHand hand) {
      return player.getItemInHand(hand);
   }

   public static void set(Player player, InteractionHand hand, ItemStack stack) {
      if (stack.isEmpty()) {
         stack = ItemStack.EMPTY;
      }

      Inventory inventory = player.getInventory();
      if (hand == InteractionHand.MAIN_HAND) {
         inventory.items.set(inventory.selected, stack);
      } else {
         if (hand != InteractionHand.OFF_HAND) {
            throw new IllegalArgumentException("invalid hand: " + hand);
         }

         inventory.offhand.set(0, stack);
      }
   }

   public static void clear(Player player, InteractionHand interactionHand) {
      set(player, interactionHand, ItemStack.EMPTY);
   }

   public static boolean storeInventoryItem(ItemStack itemStack, Player player, boolean bl) {
      if (!bl) {
         return player.getInventory().add(itemStack);
      }

      int n = itemStack.getCount();
      int n2 = Math.min(player.getInventory().getMaxStackSize(), itemStack.getMaxStackSize());

      for (int i = 0; i < player.getInventory().items.size() && n > 0; i++) {
         ItemStack itemStack2 = (ItemStack)player.getInventory().items.get(i);
         if (itemStack2.isEmpty()) {
 n -= n2;
         } else if (checkItemEqualityStrict(itemStack, itemStack2) && itemStack2.getCount() < n2) {
 n -= n2 - itemStack2.getCount();
         }
      }

      return n <= 0;
   }

   public static void dropAsEntity(Level level, BlockPos blockPos, ItemStack itemStack) {
      if (!itemStack.isEmpty()) {
         double d = 0.7;
         double d2 = level.random.nextFloat() * d + (1.0 - d) * 0.5;
         double d3 = level.random.nextFloat() * d + (1.0 - d) * 0.5;
         double d4 = level.random.nextFloat() * d + (1.0 - d) * 0.5;
         ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + d2, blockPos.getY() + d3, blockPos.getZ() + d4, itemStack.copy());
         itemEntity.setDefaultPickUpDelay();
         level.addFreshEntity(itemEntity);
      }
   }

   public static ItemStack getPickStack(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      ItemStack itemStack = blockState.getBlock().getCloneItemStack(level, blockPos, blockState);
      return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
   }

   public static void handleItemCollection(Player player, InventoryDynamic inventory) {
      if (!player.level().isClientSide) {
         for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack sis = inventory.getItem(i);
            if (!sis.isEmpty()) {
               ItemEntity item = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), sis);
               item.setPickUpDelay(0);
               player.level().addFreshEntity(item);
            }
         }

         player.giveExperiencePoints(inventory.xp);
         inventory.clearContent();
      }
   }
}
