package com.industrialreforged.tech.item.tool;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class IRPickaxe extends PickaxeItem {
   public IRPickaxe(Tier tier, int baseAttackDamage, float baseAttackSpeed, Properties properties) {
      super(tier, properties.attributes(createAttributes(tier, baseAttackDamage, baseAttackSpeed)));
   }
}
