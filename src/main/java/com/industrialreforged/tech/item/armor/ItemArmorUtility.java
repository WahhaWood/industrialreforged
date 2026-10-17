package com.industrialreforged.tech.item.armor;

import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class ItemArmorUtility extends IRArmorItem {
   public ItemArmorUtility(Properties properties, Type equipmentSlot) {
      super(IRArmorMaterials.UTILITY, equipmentSlot, properties);
   }
}
