package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class InvSlotConsumable extends BasicSimpleContainer {
   public InvSlotConsumable(BaseBlockEntity entity, String name, int size) {
      super(entity, name, size);
      this.setCanOutput(false);
   }

   public abstract boolean canPlaceItem(int var1, ItemStack var2);

   public ItemStack consume(int amount) {
      return this.consume(amount, false, false);
   }

   public ItemStack consume(int amount, boolean simulation, boolean ignoreRemaining) {
      ItemStack result = ItemStack.EMPTY;

      for (int i = 0; i < this.getContainerSize(); i++) {
         ItemStack stack = this.getItem(i);
         if (!stack.isEmpty()
            && this.canPlaceItem(i, stack)
            && (result.isEmpty() || ItemStack.isSameItemSameComponents(result, stack))
            && (stack.getCount() == 1 || ignoreRemaining || !stack.getItem().hasCraftingRemainingItem(stack))) {
            int decrement = Math.min(amount, stack.getCount());
            amount -= decrement;
            if (!simulation) {
               if (stack.getCount() == decrement) {
                  Item item = stack.getItem();
                  if (!ignoreRemaining && item.hasCraftingRemainingItem(stack)) {
                     ItemStack remaining = item.getCraftingRemainingItem(stack);
                     if (!remaining.isEmpty() && remaining.isDamageableItem() && remaining.getDamageValue() > remaining.getMaxDamage()) {
                        remaining = ItemStack.EMPTY;
                     }

                     this.setItem(i, remaining);
                  } else {
                     this.clearContent(i);
                  }
               } else {
                  stack.shrink(decrement);
               }
            }

            if (result.isEmpty()) {
               result = stack.copyWithCount(decrement);
            } else {
               result.grow(decrement);
            }

            if (amount == 0) {
               break;
            }
         }
      }

      return result;
   }

   public int damage(int amount, boolean simulation) {
      Level level = this.entity.getLevel();
      if (level == null) {
         return 0;
      }

      int result = 0;

      for (int i = 0; i < this.getContainerSize() && amount > 0; i++) {
         ItemStack tool = this.getItem(i);
         if (!tool.isEmpty() && this.canPlaceItem(i, tool) && tool.isDamageableItem()) {
            if (simulation) {
               tool = tool.copy();
            }

            int damageValue = tool.getDamageValue();
            if (!simulation) {
               int finalI = i;
               tool.hurtAndBreak(amount, (ServerLevel)level, (LivingEntity)null, item -> this.shrink(finalI));
            }

            if (damageValue != tool.getDamageValue()) {
               result += tool.getDamageValue() - damageValue;
            }
         }
      }

      return result;
   }
}
