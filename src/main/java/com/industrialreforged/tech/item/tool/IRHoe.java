package com.industrialreforged.tech.item.tool;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class IRHoe extends HoeItem {
   public IRHoe(Tier tier, int baseAttackDamage, float baseAttackSpeed, Properties properties) {
      super(tier, properties.attributes(createAttributes(tier, baseAttackDamage, baseAttackSpeed)));
   }
}
