package com.industrialreforged.tech.item.armor;

import com.industrialreforged.tech.item.ElectricProperties;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;

public class ItemArmorBatteryPack extends ItemArmorElectric {
   public ItemArmorBatteryPack(int capacity, int energyTick, Holder<ArmorMaterial> armorMaterial, ElectricProperties properties) {
      super(capacity, energyTick, armorMaterial, Type.CHESTPLATE, properties);
   }

   @Override
   public float getDamageAbsorptionRatio() {
      return 0.0F;
   }

   @Override
   public int getEnergyPerDamage() {
      return 0;
   }
}
