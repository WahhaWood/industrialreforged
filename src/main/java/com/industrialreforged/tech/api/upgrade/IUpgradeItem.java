package com.industrialreforged.tech.api.upgrade;

import java.util.Set;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface IUpgradeItem extends ItemLike {
   boolean isSuitableFor(ItemStack var1, Set<UpgradableProperty> var2);
}
