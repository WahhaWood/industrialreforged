package com.industrialreforged.tech.item.armor;

import com.industrialreforged.tech.api.item.IHazmatLike;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class ItemArmorHazmat extends ItemArmorUtility implements IHazmatLike {
   public ItemArmorHazmat(Type equipmentSlot) {
      super(new Properties(), equipmentSlot);
   }

   @Override
   public boolean addsProtection(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack itemStack) {
      return true;
   }

   public static boolean hasCompleteHazmat(LivingEntity livingEntity) {
      for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
         if (equipmentSlot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) {
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
