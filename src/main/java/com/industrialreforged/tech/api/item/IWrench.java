package com.industrialreforged.tech.api.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IWrench {
   default boolean canWrenchDrop(Level level, ItemStack stack, float dropChance) {
      return dropChance == 1.0F || level.random.nextFloat() <= dropChance;
   }

   default boolean canTakeDamage(ItemStack stack, int amount) {
      return true;
   }

   default void damage(ItemStack stack, int amount, Player player, InteractionHand hand) {
      stack.hurtAndBreak(amount, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
   }
}
