package com.industrialreforged.tech.api.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.ItemStack;

public interface IHazmatLike {
   boolean addsProtection(LivingEntity var1, EquipmentSlot var2, ItemStack var3);

   default boolean fullyProtects(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack itemStack) {
      return false;
   }

   static boolean hasCompleteHazmat(LivingEntity livingEntity) {
      for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
         if (equipmentSlot.getType() == Type.HUMANOID_ARMOR) {
            ItemStack itemStack = livingEntity.getItemBySlot(equipmentSlot);
            if (itemStack == null || !(itemStack.getItem() instanceof IHazmatLike iHazmatLike)) {
               return false;
            }

            if (!iHazmatLike.addsProtection(livingEntity, equipmentSlot, itemStack)) {
               return false;
            }

            if (iHazmatLike.fullyProtects(livingEntity, equipmentSlot, itemStack)) {
               return true;
            }
         }
      }

      return true;
   }
}
