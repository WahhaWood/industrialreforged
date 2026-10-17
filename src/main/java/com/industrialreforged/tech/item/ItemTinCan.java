package com.industrialreforged.tech.item;

import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.StackUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ItemTinCan extends Item {
   public ItemTinCan(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemStack = StackUtil.get(player, interactionHand);
      return !level.isClientSide && player.getFoodData().needsFood()
         ? this.onEaten(player, itemStack)
         : new InteractionResultHolder(InteractionResult.PASS, itemStack);
   }

   public InteractionResultHolder<ItemStack> onEaten(Player player, ItemStack itemStack) {
      int n = Math.min(itemStack.getCount(), 20 - player.getFoodData().getFoodLevel());
      if (n <= 0) {
         return new InteractionResultHolder(InteractionResult.PASS, itemStack);
      } else {
         ItemStack itemStack2 = new ItemStack((ItemLike)IRItems.TIN_CAN.get(), n);
         if (StackUtil.storeInventoryItem(itemStack2, player, true)) {
            player.getFoodData().eat(n, n);
            itemStack.shrink(n);
            StackUtil.storeInventoryItem(itemStack2, player, false);
            return new InteractionResultHolder(InteractionResult.SUCCESS, itemStack);
         } else {
            return new InteractionResultHolder(InteractionResult.PASS, itemStack);
         }
      }
   }
}
