package com.industrialreforged.tech.item.tool;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class IRAxe extends AxeItem {
   public IRAxe(Tier tier, int baseAttackDamage, float baseAttackSpeed, Properties properties) {
      super(tier, properties.attributes(createAttributes(tier, baseAttackDamage, baseAttackSpeed)));
   }
}
